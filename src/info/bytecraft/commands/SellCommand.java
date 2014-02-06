package info.bytecraft.commands;

import static org.bukkit.ChatColor.*;

import java.util.HashMap;
import java.util.Map;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.ChatState;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IItemDAO;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IPlayerDAO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

public class SellCommand extends AbstractCommand implements Listener
{
    
    private Map<BytecraftPlayer, Inventory> inventories;

    public SellCommand(Bytecraft instance)
    {
        super(instance, "sell");
        
        inventories = new HashMap<BytecraftPlayer, Inventory>();
        
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(player.getChatState() != ChatState.CHAT){
            player.sendMessage(ChatColor.RED + "You are already in a trade!");
            return true;
        }
        
        Server server = plugin.getServer();
        
        player.setChatState(ChatState.SELL);
        
        player.sendMessage(BLUE + "[Sell]" + GREEN + " Welcome to the Federal Reserve " +
                " of Bytecraft!");
        player.sendMessage(BLUE + "[Sell]" + GREEN + " What do you want to sell?");
        
        Inventory inv = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(inv);

        inventories.put(player, inv);
        
        return true;
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer((Player)event.getPlayer());
        
        Inventory inventory = inventories.get(player);
        if (inventory == null) {
            return;
        }
        
        player.sendMessage(BLUE + "[Sell]" + LIGHT_PURPLE + " You are offering: ");
        
        int bid = 0;
        try(IContext ctx = plugin.createContext()){
            IItemDAO dao = ctx.getItemDAO();
            
            for(ItemStack stack: inventory.getContents()){
                if(stack == null){
                    continue;
                }
                
                Material mat = stack.getType();
                int amount = stack.getAmount();
                int value = dao.getValue(stack);
                int enchValue = 0;
                
                ItemMeta meta = stack.getItemMeta();
                if(meta.hasEnchants()){
                    Map<Enchantment, Integer> enchants = meta.getEnchants();
                    for(Enchantment ench: enchants.keySet()){
                        enchValue += dao.getEnchantValue(ench.getId(), meta.getEnchantLevel(ench));
                    }
                }
                
                if(enchValue != 0){
                    player.sendMessage(BLUE + "[Sell] " + GREEN
                            + mat.toString() + YELLOW + " [Enchanted]" + GREEN + ": " + GOLD
                            + amount + GREEN + " * " + GOLD + value + GREEN
                            + " + " + GOLD + enchValue + GREEN + " = " + GOLD
                            + ((amount * value) + enchValue) + " bytes");
                }else{
                    player.sendMessage(BLUE + "[Sell] " + GREEN + mat.toString()
                            + ": " + GOLD + amount + GREEN + " * "
                            + GOLD + value + GREEN + " = " + GOLD
                            + (amount * value) + " bytes");
                }
                //[Sell] name [Enchantedt=x] : i * j = k bytes
                bid += (value * amount) + enchValue;
            }
            
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
        
        player.sendMessage(BLUE + "[Sell]" + DARK_AQUA + " The Federal Reserve of Bytecraft " +
                "bids " + GOLD + bid + DARK_AQUA + " bytes. Type \"accept\" to sell, \"change\" " +
                "to modify your offering, and \"quit\" to abort.");
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getChatState() != ChatState.SELL) {
            return;
        }

        Inventory inventory = inventories.get(player);
        if (inventory == null) {
            player.setChatState(ChatState.CHAT);
            return;
        }

        event.setCancelled(true);

        String text = event.getMessage();

        if ("quit".equalsIgnoreCase(text)) {
            player.setChatState(ChatState.CHAT);
            inventories.remove(player);

            // Restore contents to player inventory
            ItemStack[] contents = inventory.getContents();
            Inventory playerInv = player.getInventory();
            for (ItemStack stack : contents) {
                if (stack == null) {
                    continue;
                }
                playerInv.addItem(stack);
                player.updateInventory();
            }

            player.sendMessage(BLUE + "[Sell]" + GREEN + " Trade ended.");
        }
        else if ("change".equalsIgnoreCase(text)) {
            player.openInventory(inventory);
        }
        else if ("accept".equalsIgnoreCase(text)) {
            ItemStack[] contents = inventory.getContents();

            try (IContext ctx = plugin.createContext()) {
                IItemDAO itemDAO = ctx.getItemDAO();

                // Recalculate cost
                int bid = 0;
                for (ItemStack stack : contents) {
                    if (stack == null) {
                        continue;
                    }

                    int amount = stack.getAmount();
                    int value = itemDAO.getValue(stack);
                    int enchValue = 0;
                    
                    ItemMeta meta = stack.getItemMeta();
                    if(meta.hasEnchants()){
                        Map<Enchantment, Integer> enchants = meta.getEnchants();
                        for(Enchantment ench: enchants.keySet()){
                            enchValue += itemDAO.getEnchantValue(ench.getId(), meta.getEnchantLevel(ench));
                        }
                    }

                    bid += ((amount * value) + enchValue);
                }

                IPlayerDAO pDao = ctx.getPlayerDAO();
                ILogDAO log = ctx.getLogDAO();

                pDao.give(player, bid);
                log.insertTransactionLog("SERVER", player, bid);
                log.insertSellLog(player, bid);

                player.sendMessage(BLUE + "[Sell] " + GOLD + bid
                      + GREEN + " bytes was " + "added to your wallet!");
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            // Finalize
            player.setChatState(ChatState.CHAT);
            inventories.remove(player);
        }
    }

}
