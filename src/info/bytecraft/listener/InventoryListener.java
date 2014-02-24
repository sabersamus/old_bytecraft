package info.bytecraft.listener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.InventoryAccess;
import info.bytecraft.api.Rank;
import info.bytecraft.database.*;
import info.bytecraft.database.IInventoryDAO.ChangeType;
import info.bytecraft.database.IInventoryDAO.InventoryType;

public class InventoryListener implements Listener
{
    private Bytecraft plugin;
    private Map<Location, ItemStack[]> openInventories;

    public InventoryListener(Bytecraft instance)
    {
        this.plugin = instance;
        this.openInventories = new HashMap<>();
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        BytecraftPlayer player = plugin.getPlayer((Player)event.getPlayer());

        Inventory inv = event.getInventory();
        InventoryHolder holder = inv.getHolder();
        Location loc = null;
        if (holder instanceof BlockState) {
            BlockState block = (BlockState)holder;
            loc = block.getLocation();
        }
        else if (holder instanceof DoubleChest) {
            DoubleChest block = (DoubleChest)holder;
            loc = block.getLocation();
        }
        else {
            return;
        }

        ItemStack[] contents = inv.getContents();
        ItemStack[] copy = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                copy[i] = contents[i].clone();
            }
        }

        openInventories.put(loc, contents);

        try (IContext ctx = plugin.createContext()) {
            IInventoryDAO invDAO = ctx.getInventoryDAO();

            // Find inventory id, or create a new row if none exists
            int id = invDAO.getInventoryId(loc);
            if (id == -1) {
                id = invDAO.insertInventory(player, loc, InventoryType.BLOCK);
            } else {
                List<InventoryAccess> accessLog = invDAO.getAccessLog(id, 10);
                int others = 0;
                for (InventoryAccess access : accessLog) {
                    if (!access.getPlayerName().equalsIgnoreCase(player.getName())) {
                        others++;
                    }
                }

                if (others > 0 && player.hasFlag(Flag.CHEST_LOG)) {
                    player.sendMessage(ChatColor.YELLOW + "Last accessed by:");
                    SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
                    int i = 0;
                    for (InventoryAccess access : accessLog) {
                        if (!access.getPlayerName().equalsIgnoreCase(player.getName())) {
                            if (i > 2) {
                                break;
                            }
                            BytecraftPlayer p = plugin.getPlayerOffline(access.getPlayerName());
                            player.sendMessage(p.getDisplayName() + ChatColor.YELLOW + " on " +
                                dfm.format(access.getTimestamp()) + ".");
                            i++;
                        }
                    }
                }
            }

            // Insert into access log
            invDAO.insertAccessLog(player, id);
        }
        catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        BytecraftPlayer player = plugin.getPlayer((Player)event.getPlayer());

        Inventory inv = event.getInventory();
        InventoryHolder holder = inv.getHolder();
        Location loc = null;
        if (holder instanceof BlockState) {
            BlockState block = (BlockState)holder;
            loc = block.getLocation();
        }
        else if (holder instanceof DoubleChest) {
            DoubleChest block = (DoubleChest)holder;
            loc = block.getLocation();
        }
        else {
            return;
        }

        if (!openInventories.containsKey(loc)) {
            plugin.sendMessage("Inventory location not found.");
            return;
        }

        plugin.sendMessage(player.getName() + " closed inventory: " +
                             "x=" + loc.getBlockX() + " " +
                             "y=" + loc.getBlockY() + " " +
                             "z=" + loc.getBlockZ());

        ItemStack[] oldContents = openInventories.get(loc);
        ItemStack[] currentContents = inv.getContents();

        assert oldContents.length == currentContents.length;

        try (IContext ctx = plugin.createContext()) {
            IInventoryDAO invDAO = ctx.getInventoryDAO();

            // Find inventory id, or create a new row if none exists
            int id = invDAO.getInventoryId(loc);
            if (id == -1) {
                plugin.sendMessage("Inventory id " + id + " not found!");
                return;
            }

            // Store all changes
            for (int i = 0; i < oldContents.length; i++) {
                ItemStack a = oldContents[i];
                ItemStack b = currentContents[i];
                if (a == null && b == null) {
                    continue;
                }

                if (a == null || b == null || !a.equals(b)) {
                    plugin.sendMessage("Slot " + i + " changed. Was " +
                        a + " and is " + b);

                    // Removed
                    if (a != null) {
                        invDAO.insertChangeLog(player, id, i, a, ChangeType.REMOVE);
                    }

                    // Added
                    if (b != null) {
                        invDAO.insertChangeLog(player, id, i, b, ChangeType.ADD);
                    }
                }
            }

            // Store contents
            invDAO.insertStacks(id, currentContents);
        }
        catch (DAOException e) {
            throw new RuntimeException(e);
        }

        openInventories.remove(loc);
    }
    
    @EventHandler
    public void saveInventory(InventoryCloseEvent event)
    {
        if(!(event.getPlayer() instanceof Player))return;
        BytecraftPlayer player = plugin.getPlayer((Player)event.getPlayer());
        if (player.getCurrentInventory() != null) {
            player.saveInventory(player.getCurrentInventory());
        }
    }
    
    @EventHandler
    public void onOpen(InventoryOpenEvent event)
    {
        if(!(event.getPlayer() instanceof Player))return;
        if(event.isCancelled())return;
        BytecraftPlayer player = plugin.getPlayer((Player)event.getPlayer());
        if(event.getInventory().getHolder() != player.getDelegate()){
            if(player.getRank() == Rank.NEWCOMER){
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't open chests until you become a settler!");
                return;
            }
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null) {
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<String>();
                    lore.add(ChatColor.YELLOW + "Creative");
                    BytecraftPlayer p = this.plugin.getPlayer(player);
                    lore.add(ChatColor.YELLOW + "by: " + p.getDisplayName());
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(player.getGameMode() != GameMode.CREATIVE)return;
        ItemStack stack = event.getItemDrop().getItemStack();
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.YELLOW + "Creative");
        lore.add(ChatColor.YELLOW + "by: " + player.getDisplayName());
        meta.setLore(lore);
        stack.setItemMeta(meta);
    }
}
