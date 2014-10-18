/*
 * Copyright (c) 2010-2014, Ein Andersson, Emil Hernvall, Josh Morgan, James Sherlock, Rob Catron, Joe Notaro
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgment:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package info.bytecraft.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.MathUtil;
import info.bytecraft.api.SaleSign;
import info.bytecraft.database.*;
import info.bytecraft.database.ISaleSignDAO.TransactionType;
import info.bytecraft.zones.Lot;
import info.bytecraft.zones.ZoneWorld;

import info.tregmine.quadtree.Point;

public class SaleSignListener implements Listener
{
    private Bytecraft plugin;

    public SaleSignListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getChatState() != BytecraftPlayer.ChatState.SALESIGN_SETUP &&
            player.getChatState() != BytecraftPlayer.ChatState.SALESIGN_WITHDRAW &&
            player.getChatState() != BytecraftPlayer.ChatState.SALESIGN_BUY) {
            return;
        }

        event.setCancelled(true);

        Map<Location, SaleSign> saleSigns = plugin.getSaleSigns();

        String text = event.getMessage().trim();
        String[] textSplit = text.split(" ");

        player.sendMessage(ChatColor.AQUA + "[SALE] " +
                ChatColor.WHITE + "<" +
                player.getDisplayName() +
                ChatColor.WHITE + "> " + text);

        if (player.getChatState() == BytecraftPlayer.ChatState.SALESIGN_SETUP) {

            SaleSign newSaleSign = player.getNewSaleSign();
            if (newSaleSign == null) {
                player.setChatState(BytecraftPlayer.ChatState.CHAT);
                return;
            }

            // expect price
            if (newSaleSign.getCost() == 0) {
                try {
                    int cost = Integer.parseInt(text);
                    if (cost <= 0) {
                        player.sendMessage(ChatColor.RED +
                                "Please enter a positive number.");
                        return;
                    }

                    newSaleSign.setCost(cost);

                    player.sendMessage(ChatColor.GREEN +
                        "Cost set to " + cost + " bytes. Sale sign set up.");

                    saleSigns.put(newSaleSign.getBlockLocation(), newSaleSign);
                    saleSigns.put(newSaleSign.getSignLocation(), newSaleSign);

                    player.setNewSaleSign(null);
                    player.setChatState(BytecraftPlayer.ChatState.CHAT);

                    // Create info sign
                    World world = player.getWorld();
                    updateSign(world, newSaleSign);
                    
                    world.getBlockAt(newSaleSign.getBlockLocation()).setType(Material.OBSIDIAN);

                    try (IContext ctx = plugin.createContext()) {
                        ISaleSignDAO saleSignDAO = ctx.getSaleSignDAO();
                        saleSignDAO.insert(newSaleSign);
                    } catch (DAOException e) {
                        throw new RuntimeException(e);
                    }
                }
                catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED +
                            "Please enter a positive number.");
                    return;
                }
            }
        }
        else if (player.getChatState() == BytecraftPlayer.ChatState.SALESIGN_WITHDRAW) {
            SaleSign saleSign = player.getCurrentSaleSign();

            if ("changecost".equalsIgnoreCase(textSplit[0])) {
                if (textSplit.length != 2) {
                    player.sendMessage(ChatColor.RED + "Type \"changecost x\", with " +
                            "x being the cost in bytes of the item.");
                    return;
                }

                int cost = 0;
                try {
                    cost = Integer.parseInt(textSplit[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Type \"changecost x\", with " +
                            "x being the cost in bytes of the item.");
                    return;
                }

                if (cost <= 0) {
                    player.sendMessage(ChatColor.RED + "Type \"changecost x\", with " +
                            "x being the cost in bytes of the item.");
                    return;
                }

                int oldCost = saleSign.getCost();
                saleSign.setCost(cost);

                player.sendMessage(ChatColor.GREEN + "Cost changed to " +
                        cost + " bytes.");

                player.setChatState(BytecraftPlayer.ChatState.CHAT);
                player.setCurrentSaleSign(null);

                updateSign(player.getWorld(), saleSign);

                try (IContext ctx = plugin.createContext()) {
                    ISaleSignDAO saleSignDAO = ctx.getSaleSignDAO();
                    saleSignDAO.update(saleSign);
                    saleSignDAO.insertCostChange(saleSign, oldCost);
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if ("withdraw".equalsIgnoreCase(textSplit[0])) {
                if (textSplit.length != 2) {
                    player.sendMessage(ChatColor.RED + "Type \"withdraw x\", with " +
                            "x being the number of items you wish to withdraw.");
                    return;
                }

                int num = 0;
                if ("all".equalsIgnoreCase(textSplit[1])) {
                    int available = saleSign.getAvailableInventory();
                    num = available;
                } else {
                    try {
                        num = Integer.parseInt(textSplit[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Type \"withdraw x\", with " +
                                "x being the number of items you wish to withdraw.");
                        return;
                    }
                }

                if (num <= 0) {
                    player.sendMessage(ChatColor.RED + "Type \"withdraw x\", with " +
                            "x being the number of items you wish to withdraw.");
                    return;
                }

                int available = saleSign.getAvailableInventory();
                if (num > available) {
                    player.sendMessage(ChatColor.RED + "There are only " +
                            available + "items available.");
                    return;
                }

                int added = transferToInventory(saleSign, player, num);

                if (num != added) {
                    player.sendMessage(ChatColor.GREEN + "Your inventory is full. " +
                        "Could only withdraw " + added + " items.");
                } else {
                    player.sendMessage(ChatColor.GREEN + "" +
                        added + " items withdrawn successfully.");
                }

                player.setChatState(BytecraftPlayer.ChatState.CHAT);
                player.setCurrentSaleSign(null);

                updateSign(player.getWorld(), saleSign);

                try (IContext ctx = plugin.createContext()) {
                    ISaleSignDAO saleSignDAO = ctx.getSaleSignDAO();
                    saleSignDAO.update(saleSign);
                    saleSignDAO.insertTransaction(saleSign,
                                                    player,
                                                    TransactionType.WITHDRAW,
                                                    added);
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if ("quit".equalsIgnoreCase(textSplit[0])) {
                player.sendMessage(ChatColor.GREEN +
                    "Quitting without action.");
                player.setChatState(BytecraftPlayer.ChatState.CHAT);
                player.setCurrentSaleSign(null);
            }
            else {
                player.sendMessage(ChatColor.RED +
                                   "Type withdraw, changecost or quit.");
            }
        }
        else if (player.getChatState() == BytecraftPlayer.ChatState.SALESIGN_BUY) {
            SaleSign saleSign = player.getCurrentSaleSign();

            if ("buy".equalsIgnoreCase(textSplit[0])) {
                if (textSplit.length != 2) {
                    player.sendMessage(ChatColor.RED + "Type \"buy x\", with " +
                            "x being the number of items you wish to biy.");
                    return;
                }

                int num = 0;
                try {
                    num = Integer.parseInt(textSplit[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Type \"buy x\", with " +
                            "x being the number of items you wish to biy.");
                    return;
                }

                if (num <= 0) {
                    player.sendMessage(ChatColor.RED + "Type \"buy x\", with " +
                            "x being the number of items you wish to buy.");
                    return;
                }

                int available = saleSign.getAvailableInventory();
                if (num > available) {
                    player.sendMessage(ChatColor.RED + "There are only " +
                            available + "items available.");
                    return;
                }

                player.setSaleBuyCount(num);

                int cost = num * saleSign.getCost();

                player.sendMessage(ChatColor.AQUA + "Do you wish to buy " +
                    num + " items for a total cost of " + cost + " bytes?");
                player.sendMessage(ChatColor.AQUA + "Type \"accept\" to confirm " +
                    "or quit to exit.");
            }
            else if ("accept".equalsIgnoreCase(textSplit[0])) {
                if (player.getSaleBuyCount() == 0) {
                    player.sendMessage(ChatColor.RED + "Please specify how many " +
                            "items you wish to buy using \"buy x\".");
                    return;
                }

                int num = player.getSaleBuyCount();

                // Check availability again to make sure, in case someone else
                // is buying at the same time
                int available = saleSign.getAvailableInventory();
                if (num > available) {
                    player.sendMessage(ChatColor.RED + "There are only " +
                            available + "items available.");
                    return;
                }

                int cost = num * saleSign.getCost();
                try (IContext dbCtx = plugin.createContext()) {
                    IPlayerDAO dao = dbCtx.getPlayerDAO();
                    
                    long balance = dao.getBalance(player);
                    if (balance < cost) {
                        player.sendMessage(ChatColor.RED + "You do not have " +
                            "enough money to complete your purchase.");
                        return;
                    }

                    int added = transferToInventory(saleSign, player, num);

                    if (num != added) {
                        player.sendMessage(ChatColor.GREEN + "Your inventory is full. " +
                            "Could only buy " + added + " items.");
                        cost = added * saleSign.getCost();
                    } else {
                        player.sendMessage(ChatColor.GREEN + "" +
                            added + " items added to your inventory.");
                    }

                    BytecraftPlayer seller =
                        plugin.getPlayerOffline(saleSign.getPlayerName());

                    if (dao.take(player, cost)) {
                        long perc = MathUtil.percentage(cost, 5);
                        dao.give(seller, cost - perc);
                        ILogDAO log = dbCtx.getLogDAO();
                        log.insertTransactionLog(player, seller,
                                cost);

                        player.sendMessage(ChatColor.GREEN + "" + cost
                                + " bytes was taken from your wallet!");

                        ISaleSignDAO saleSignDAO = dbCtx.getSaleSignDAO();
                        saleSignDAO.update(saleSign);
                        saleSignDAO.insertTransaction(saleSign,
                                                        player,
                                                        TransactionType.BUY,
                                                        added);

                    }
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }

                player.setCurrentSaleSign(null);
                player.setSaleBuyCount(0);
                player.setChatState(BytecraftPlayer.ChatState.CHAT);

                updateSign(player.getWorld(), saleSign);
            }
            else if ("quit".equalsIgnoreCase(textSplit[0])) {
                player.sendMessage(ChatColor.GREEN +
                    "Quitting without buying.");
                player.setChatState(BytecraftPlayer.ChatState.CHAT);
                player.setCurrentSaleSign(null);
            }
            else {
                player.sendMessage(ChatColor.RED + "Type buy or quit.");
            }

        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Map<Location, SaleSign> saleSigns = plugin.getSaleSigns();

        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getChatState() != BytecraftPlayer.ChatState.CHAT) {
            return;
        }

        Block block = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Location loc = block.getLocation();

        ItemStack heldItem = player.getItemInHand();

        // Whenever someone clicks on a sale block that's already setup
        if (saleSigns.containsKey(loc)) {

            if (player.getGameMode() == GameMode.CREATIVE) {
                player.sendMessage(ChatColor.RED + "Cannot use sale blocks " +
                        "whilst in creative mode.");
                event.setCancelled(true);
                return;
            }

            if (player.getChatState() == BytecraftPlayer.ChatState.SALESIGN_WITHDRAW ||
                player.getChatState() == BytecraftPlayer.ChatState.SALESIGN_BUY) {

                event.setCancelled(true);
                return;
            }

            SaleSign saleSign = saleSigns.get(loc);

            MaterialData material = saleSign.getMaterial();
            if (material.getData() != 0) {
                player.sendMessage(ChatColor.AQUA +
                    "You are now talking to a sale block that is selling: " +
                    material.getItemType().toString() + ":" +
                    material.getData() + ".");
            } else {
                player.sendMessage(ChatColor.AQUA +
                    "You are now talking to a sale block that is selling: " +
                    material.getItemType().toString() + ".");
            }

            Map<Enchantment, Integer> enchantments = saleSign.getEnchantments();
            if (enchantments.size() > 0) {
                if (saleSign.hasStoredEnchantments()) {
                    player.sendMessage(ChatColor.AQUA +
                            "With the following STORED enchants:");
                } else {
                    player.sendMessage(ChatColor.AQUA +
                            "With the following enchants:");
                }
                for (Map.Entry<Enchantment, Integer> entry : enchantments
                        .entrySet()) {
                    Enchantment enchant = entry.getKey();
                    Integer level = entry.getValue();
                    String enchantName = enchant.getName().toLowerCase();
                    player.sendMessage("- " + enchantName + " Level: "
                            + level.toString());
                }
            }

            // Player owns this sale block, and should either enter withdraw
            // mode or add items to this sale block
            if (saleSign.getPlayerName().equalsIgnoreCase(player.getName())) {

                // Check if the held item equals the type of the sale block
                MaterialData saleMaterial = saleSign.getMaterial();
                if (saleMaterial == null) {
                    return;
                }

                MaterialData heldMaterial = heldItem.getData();

                boolean match = false;
                boolean all = false;
                if (saleMaterial.equals(heldMaterial)) {
                    match = true;
                    
                    if (player.isSneaking()) {
                        all = true;
                    }

                    if (saleSign.hasStoredEnchantments()) {
                        EnchantmentStorageMeta storageMeta = getStorageMeta(heldItem);
                        if (storageMeta != null) {
                            match = compareEnchants(saleSign.getEnchantments(),
                                                    storageMeta.getStoredEnchants());
                        }
                        else if (saleSign.getEnchantments().size() > 0) {
                            match = false;
                        }
                    } else {
                        match = compareEnchants(saleSign.getEnchantments(),
                                                heldItem.getEnchantments());
                    }
                }

                // Add to block inventory
                if (match) {
                    Material type = heldMaterial.getItemType();
                    if (type.getMaxDurability() != 0 && heldMaterial.getData() != 0) {
                        player.sendMessage(ChatColor.RED + "You cannot add " +
                                "damaged items.");
                        return;
                    }
                    
                    int allAmount = 0;
                    boolean massEnchant = false;
                    if (all) {
                        for (ItemStack i : player.getInventory().getContents()) {
                            boolean allow = true;
                            if (i == null) {
                                continue; // Get rid of NPE
                            }
                            if (i.getType().getMaxDurability() != 0 && i.getData().getData() != 0) {
                                continue; // Ignore damaged items
                            }
                            if (!saleMaterial.equals(i.getData())) {
                                continue; // Ignore items that do not match
                            }
                            if (saleSign.hasStoredEnchantments()) {
                                if (massEnchant == false) {
                                    player.sendMessage(ChatColor.RED + 
                                            "Mass Submition only works with non enchanted items and blocks.");
                                    massEnchant = true;
                                }
                                continue;
                            }
                            if (i.getEnchantments().size() > 0) {
                                continue;
                            }
                            if (!allow) {
                                continue;
                            }
                            allAmount += i.getAmount();
                            player.getInventory().remove(i);
                        }
                    } else {
                        allAmount = heldItem.getAmount();
                        player.setItemInHand(null);
                    }
                    
                    saleSign.addAvailableInventory(allAmount);
                    player.sendMessage(ChatColor.GREEN + "" +
                            allAmount + " items added to sale block.");

                    updateSign(player.getWorld(), saleSign);

                    try (IContext ctx = plugin.createContext()) {
                        ISaleSignDAO saleSignDAO = ctx.getSaleSignDAO();
                        saleSignDAO.update(saleSign);
                        saleSignDAO.insertTransaction(saleSign,
                                                        player,
                                                        TransactionType.DEPOSIT,
                                                        allAmount);
                    } catch (DAOException e) {
                        throw new RuntimeException(e);
                    }
                }
                // Enter withdrawal mode
                else {
                    player.sendMessage(ChatColor.AQUA + "There are " +
                        saleSign.getAvailableInventory() + " items available. ");
                    player.sendMessage(ChatColor.AQUA +
                        "Type \"withdraw x\" to withdraw items to your inventory.");
                    player.sendMessage(ChatColor.AQUA +
                        "Type \"changecost x\" to change the cost of the items " +
                        "sold by this block.");
                    player.sendMessage(ChatColor.AQUA +
                        "Type \"quit\" to exit without doing anything.");

                    player.setChatState(BytecraftPlayer.ChatState.SALESIGN_WITHDRAW);
                    player.setCurrentSaleSign(saleSign);
                }
            }

            // This is somebody else, and the should enter buy mode
            else {
                event.setCancelled(true);

                player.sendMessage(ChatColor.AQUA + "Each block is " +
                    saleSign.getCost() + " bytes. Type \"buy x\" to buy, " +
                    "with x being the number of items you want to buy. Type " +
                    "\"quit\" to exit without buying anything.");

                player.setChatState(BytecraftPlayer.ChatState.SALESIGN_BUY);
                player.setCurrentSaleSign(saleSign);
            }
        }
        else if (block.getType() == Material.OBSIDIAN) {

            SaleSign newSaleSign = player.getNewSaleSign();

            // We're creating a new sale block
            if (heldItem.getType() == Material.COAL && newSaleSign == null) {

                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(ChatColor.RED + "Cannot use sale blocks " +
                            "whilst in creative mode.");
                    event.setCancelled(true);
                    return;
                }

                if (face.getModY() != 0) {
                    player.sendMessage(ChatColor.RED + "Click on the sides of " +
                            "the block to set up a sale block.");
                    return;
                }

                event.setCancelled(true);

                Location signLoc = new Location(loc.getWorld(),
                                                loc.getX() + face.getModX(),
                                                loc.getY() + face.getModY(),
                                                loc.getZ() + face.getModZ());

                // Check if this is the players lot
                ZoneWorld zoneWorld = plugin.getWorld(player.getWorld());
                Point blockPos = new Point(loc.getBlockX(), loc.getBlockZ());
                Point signPos = new Point(signLoc.getBlockX(), signLoc.getBlockZ());

                Lot blockLot = zoneWorld.findLot(blockPos);
                Lot signLot = zoneWorld.findLot(signPos);

                // Make sure that both the block and the sign are in an lot
                if (blockLot == null && signLot == null) {
                    player.sendMessage(ChatColor.RED +
                        "Sale signs can only be created in lots.");
                    return;
                }
                // In case one of them is, but the other one isn't
                else if (blockLot == null || signLot == null) {
                    player.sendMessage(ChatColor.RED +
                            "Too close to the edge of the lot.");
                    return;
                }
                // Make sure it's the same lot
                else if (blockLot.getId() != signLot.getId()) {
                    player.sendMessage(ChatColor.RED +
                            "Too close to the edge of the lot.");
                    return;
                }
                // Since we've already verified that it's the same lot, we only
                // need to check the owner for one of the lots
                else if (!blockLot.isOwner(player)) {
                    player.sendMessage(ChatColor.RED +
                            "You have to be owner of the lot to create sale blocks.");
                    return;
                }

                // Check if this has already been converted
                if (saleSigns.containsKey(loc) ||
                    saleSigns.containsKey(signLoc)) {

                    player.sendMessage(ChatColor.RED + "This block has already is already a sale sign.");
                    return;
                }

                World world = block.getWorld();
                Block signBlock = world.getBlockAt(signLoc);
                if (signBlock.getType() != Material.AIR) {
                    player.sendMessage(ChatColor.RED + "There must be at least one " +
                            "empty block in front of the block.");
                    return;
                }

                SaleSign saleSign = new SaleSign();
                saleSign.setPlayerName(player.getName());
                saleSign.setBlockLocation(loc);
                saleSign.setSignLocation(signLoc);

                player.setNewSaleSign(saleSign);

                player.sendMessage(ChatColor.GREEN + "You are creating a new " +
                        "sale sign, which can be used to sell items. Now " +
                        "select the item or material you want to sell, " +
                        "and left click on this block again.");

                heldItem.setAmount(heldItem.getAmount()-1);
                player.setItemInHand(heldItem);
            }
            // This is when the player sets the type of the sale block
            else if (newSaleSign != null &&
                     loc.equals(newSaleSign.getBlockLocation())) {

                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(ChatColor.RED + "Cannot use sale blocks " +
                            "whilst in creative mode.");
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(true);

                MaterialData material = heldItem.getData();
                Material type = material.getItemType();
                // This is an item with limited durability that has been
                // damaged
                if (type.getMaxDurability() != 0 && material.getData() != 0) {
                    player.sendMessage(ChatColor.RED + "You cannot sell " +
                            "damaged items.");
                    return;
                }

                Map<Enchantment, Integer> enchantments = heldItem.getEnchantments();
                if (enchantments == null || enchantments.size() == 0) {
                    if (heldItem.hasItemMeta()) {
                        EnchantmentStorageMeta enchantMeta =
                            getStorageMeta(heldItem);
                        if (enchantMeta != null) {
                            enchantments = enchantMeta.getStoredEnchants();
                            newSaleSign.setStoredEnchantments(true);
                        }
                    }
                }

                newSaleSign.setMaterial(material);
                newSaleSign.setEnchantments(enchantments);

                player.setChatState(BytecraftPlayer.ChatState.SALESIGN_SETUP);

                if (material.getData() != 0) {
                    player.sendMessage(ChatColor.GREEN +
                        "This sale block will sell " +
                        material.getItemType().toString() + ":" +
                        material.getData() + ".");
                } else {
                    player.sendMessage(ChatColor.GREEN +
                        "This sale block will sell " +
                        material.getItemType().toString() + ".");
                }

                if (enchantments.size() > 0) {
                    if (newSaleSign.hasStoredEnchantments()) {
                        player.sendMessage(ChatColor.GREEN +
                                "With the following STORED enchants:");
                    } else {
                        player.sendMessage(ChatColor.GREEN +
                                "With the following enchants:");
                    }
                    for (Map.Entry<Enchantment, Integer> entry : enchantments
                            .entrySet()) {
                        Enchantment enchant = entry.getKey();
                        Integer level = entry.getValue();
                        String enchantName = enchant.getName().toLowerCase();
                        player.sendMessage("- " + enchantName + " Level: "
                                + level.toString());
                    }
                }

                player.sendMessage(ChatColor.AQUA +
                        "How much should one item cost?");
                return;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());

        Block block = event.getBlock();
        Location loc = block.getLocation();

        Map<Location, SaleSign> saleSigns = plugin.getSaleSigns();
        if (!saleSigns.containsKey(loc)) {
            return;
        }

        SaleSign saleSign = saleSigns.get(loc);
        if (!saleSign.getPlayerName().equalsIgnoreCase(player.getName())) {
            event.setCancelled(true);
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(ChatColor.RED + "Cannot use sale blocks " +
                    "whilst in creative mode.");
            event.setCancelled(true);
            return;
        }

        Location blockLoc = saleSign.getBlockLocation();
        Location signLoc = saleSign.getSignLocation();

        if (signLoc.equals(loc)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot delete the sign " +
                    "for a sale sign. Delete the obisidan to remove it.");
            return;
        }
        else if (blockLoc.equals(loc)) {
            player.sendMessage(ChatColor.GREEN + "Sale sign deleted.");

            try (IContext ctx = plugin.createContext()) {
                ISaleSignDAO saleSignDAO = ctx.getSaleSignDAO();
                saleSignDAO.delete(saleSign);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            saleSigns.remove(blockLoc);
            saleSigns.remove(signLoc);

            World world = player.getWorld();
            Block signBlock = world.getBlockAt(saleSign.getSignLocation());
            signBlock.setType(Material.AIR);

            SaleSign currentSaleSign = player.getCurrentSaleSign();
            if (currentSaleSign.getId() == saleSign.getId()) {
                player.setCurrentSaleSign(null);
                player.setChatState(BytecraftPlayer.ChatState.CHAT);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void updateSign(World world, SaleSign saleSign)
    {
        Location blockLoc = saleSign.getBlockLocation();
        Location signLoc = saleSign.getSignLocation();

        Block signBlock = world.getBlockAt(signLoc);
        Block mainBlock = world.getBlockAt(blockLoc);
        BlockFace facing = mainBlock.getFace(signBlock);

        signBlock.setType(Material.WALL_SIGN);
        switch (facing) {
            case WEST:
                signBlock.setData((byte)0x04);
                break;
            case EAST:
                signBlock.setData((byte)0x05);
                break;
            case NORTH:
                signBlock.setData((byte)0x02);
                break;
            case SOUTH:
                signBlock.setData((byte)0x03);
                break;
            default:
                break;
        }
        //signBlock.getState().setData(new MaterialData(Material.WALL_SIGN));

        BytecraftPlayer player = plugin.getPlayerOffline(saleSign.getPlayerName());
        MaterialData material = saleSign.getMaterial();

        Sign sign = (Sign)signBlock.getState();
        sign.setLine(0, player.getDisplayName());
        if (material.getData() != 0) {
            sign.setLine(1, material.toString());
        } else {
            sign.setLine(1, material.getItemType().toString());
        }
        sign.setLine(2, saleSign.getCost() + " bytes");
        sign.setLine(3, saleSign.getAvailableInventory() + " available");
        sign.update();
    }

    private int transferToInventory(SaleSign saleSign,
                                    BytecraftPlayer player,
                                    int num)
    {
        MaterialData type = saleSign.getMaterial();
        Material material = type.getItemType();

        int stacks = num / material.getMaxStackSize();
        Inventory inventory = player.getInventory();
        int added = 0;
        boolean full = false;
        for (int i = 0; i < stacks; i++) {
            ItemStack stack = type.toItemStack(material.getMaxStackSize());
            addEnchants(stack, saleSign);

            HashMap<Integer, ItemStack> notAdded = inventory.addItem(stack);

            added += material.getMaxStackSize();
            if (notAdded.size() > 0) {
                full = true;
                for (ItemStack partialStack : notAdded.values()) {
                    added -= partialStack.getAmount();
                }
            }
        }

        if (!full) {
            int rem = num % material.getMaxStackSize();
            if (rem != 0) {
                ItemStack stack = type.toItemStack(rem);
                addEnchants(stack, saleSign);
                inventory.addItem(stack);
                added += rem;
            }
        }

        saleSign.removeAvailableInventory(added);

        return added;
    }

    private void addEnchants(ItemStack stack, SaleSign saleSign)
    {
        Map<Enchantment, Integer> enchants = saleSign.getEnchantments();
        if (saleSign.hasStoredEnchantments()) {
            EnchantmentStorageMeta enchantMeta = getStorageMeta(stack);
            for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
                enchantMeta.addStoredEnchant(enchant.getKey(),
                                             enchant.getValue(),
                                             false);
            }
            stack.setItemMeta(enchantMeta);
        } else {
            stack.addEnchantments(saleSign.getEnchantments());
        }
    }

    private EnchantmentStorageMeta getStorageMeta(ItemStack stack)
    {
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta) {
            return (EnchantmentStorageMeta)meta;
        }

        return null;
    }

    private boolean compareEnchants(Map<Enchantment, Integer> fst,
                                    Map<Enchantment, Integer> snd)
    {
        boolean match = true;
        if (fst.size() == snd.size()) {
            for (Enchantment ench : fst.keySet()) {
                Integer a = fst.get(ench);
                Integer b = snd.get(ench);
                if (a != b) {
                    match = false;
                    break;
                }
            }
        } else {
            match = false;
        }

        return match;
    }
}
