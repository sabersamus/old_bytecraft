package info.bytecraft.listener;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IInventoryDAO;
import info.bytecraft.database.IInventoryDAO.ChangeType;
import info.bytecraft.database.IInventoryDAO.InventoryType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static org.bukkit.event.inventory.InventoryType.CHEST;;

public class BookShelfListener implements Listener
{
    private Bytecraft plugin;
    private Map<BytecraftPlayer, Inventory> openInventories;
    private Map<Location, ItemStack[]> inventories;
    //private Map<BytecraftPlayer, Integer> invIds;
    private Map<Inventory, Location> locations;
    private Set<Material> materials;
    
    public BookShelfListener(Bytecraft plugin)
    {
        this.plugin = plugin;
        openInventories = new HashMap<>();
        //invIds = new HashMap<>();
        locations = new HashMap<>();
        inventories = new HashMap<>();
        materials = EnumSet.of(Material.BOOK, Material.BOOK_AND_QUILL, Material.WRITTEN_BOOK);
    }
    
    @EventHandler
    public void onOpen(PlayerInteractEvent event)
    {
        if(event.isCancelled() || event.getPlayer().getItemInHand().getType() == Material.BONE){
            return;
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)return;
        Block block = event.getClickedBlock();
        if(block.getType() != Material.BOOKSHELF)return;
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Location loc = block.getLocation();
        try(IContext ctx = plugin.createContext()){
            IInventoryDAO dao = ctx.getInventoryDAO();
            int id = dao.getInventoryId(loc);
            if(id == -1){
                id = dao.insertInventory(player, loc, InventoryType.BLOCK);
            }
            
            Inventory inv = plugin.getServer().createInventory(null, CHEST);
            inv.setContents(dao.getStacks(id, inv.getSize()));
            player.openInventory(inv);
            
            openInventories.put(player, inv);
            //invIds.put(player, id);
            locations.put(inv, loc);
            inventories.put(loc, inv.getContents());
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer((Player)event.getPlayer());
        
        Inventory inv = openInventories.get(player);
        
        if(inv == null){
            return;
        }
        
        Location loc = locations.get(inv);
        plugin.sendMessage(player.getName() + " closed inventory: " +
                "x=" + loc.getBlockX() + " " +
                "y=" + loc.getBlockY() + " " +
                "z=" + loc.getBlockZ());
        ItemStack[] stacks = inv.getContents();
        for(ItemStack stack: stacks){
            if(stack == null){
                continue;
            }
            
            if(materials.contains(stack.getType())){
                continue;
            }
            
            inv.remove(stack);
            player.getInventory().addItem(stack);
            player.updateInventory();
            player.sendMessage(ChatColor.RED + "You cant store a " + stack.getType().name().toLowerCase() + " in a bookshelf!");
        }
        
        ItemStack[] oldContents = inventories.get(loc);
        ItemStack[] currentContents = inv.getContents();

        assert oldContents.length == currentContents.length;

        
        try(IContext ctx = plugin.createContext()){
            IInventoryDAO dao = ctx.getInventoryDAO();
            int id = dao.getInventoryId(loc);
            dao.insertStacks(id, stacks);
            
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
                        dao.insertChangeLog(player, id, i, a, ChangeType.REMOVE);
                    }

                    // Added
                    if (b != null) {
                        dao.insertChangeLog(player, id, i, b, ChangeType.ADD);
                    }
                }
            }
            
            openInventories.remove(player);
            //invIds.remove(player);
            locations.remove(inv);
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
}
