package info.bytecraft.listener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Notification;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

public class BlessListener implements Listener
{

    private static Set<Material> allowedBlocks;

    private Bytecraft plugin;

    public BlessListener(Bytecraft bytecraft)
    {
        this.plugin = bytecraft;
        allowedBlocks = new HashSet<Material>();
        allowedBlocks.add(Material.CHEST);
        allowedBlocks.add(Material.CAULDRON);
        allowedBlocks.add(Material.ENCHANTMENT_TABLE);
        allowedBlocks.add(Material.FURNACE);
        allowedBlocks.add(Material.BURNING_FURNACE);
        allowedBlocks.add(Material.WOOD_DOOR);
        allowedBlocks.add(Material.WOODEN_DOOR);
        allowedBlocks.add(Material.LEVER);
        allowedBlocks.add(Material.STONE_BUTTON);
        allowedBlocks.add(Material.WORKBENCH);
        allowedBlocks.add(Material.BOOKSHELF);
        allowedBlocks.add(Material.SIGN_POST);
        allowedBlocks.add(Material.WALL_SIGN);
        allowedBlocks.add(Material.DIODE);
        allowedBlocks.add(Material.DIODE_BLOCK_OFF);
        allowedBlocks.add(Material.TRAP_DOOR);
        allowedBlocks.add(Material.DIODE_BLOCK_ON);
        allowedBlocks.add(Material.JUKEBOX);
        allowedBlocks.add(Material.SIGN);
        allowedBlocks.add(Material.FENCE_GATE);
        allowedBlocks.add(Material.DISPENSER);
        allowedBlocks.add(Material.WOOD_BUTTON);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getRank().canBless()
                && player.getItemInHand().getType() == Material.BONE
                && allowedBlocks.contains(event.getClickedBlock().getType())) {
            BytecraftPlayer target = player.getBlessTarget();
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Use /bless [name] first");
                return;
            }
            
            if(target.getId() == player.getId()){
                player.sendMessage(ChatColor.AQUA + "5000 bytes have been taken from your wallet");
                try(IContext ctx = plugin.createContext()){
                    IPlayerDAO dao = ctx.getPlayerDAO();
                    
                    dao.take(player, 5000);
                }catch(DAOException e){
                    throw new RuntimeException(e);
                }
            }

            Location loc = event.getClickedBlock().getLocation();
            
            if(plugin.blessBlock(loc, target)){
                target.sendNotification(Notification.BLESS, ChatColor.AQUA
                        + "Your god has blessed a block in your name!");
                player.sendMessage(ChatColor.AQUA
                        + "You have blessed a block for "
                        + target.getTemporaryChatName());
                event.setCancelled(true);
                return;
            }else{
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This block is already blessed!");
                return;
            }
        }
        else {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location loc = event.getClickedBlock().getLocation();
                
                if(plugin.isBlessed(loc)){
                    BytecraftPlayer owner = plugin.getOwner(loc);
                    String name = owner.getNameColor() + owner.getName();
                    if (!player.getName().equalsIgnoreCase(
                            owner.getName())) {
                        player.sendMessage(ChatColor.RED + "Blessed to: " + name);
                        if (!player.getRank().canOverrideBless()) {
                            event.setCancelled(true);
                            return;
                        }
                    }else {
                        player.sendMessage(ChatColor.AQUA
                                + "Blessed to you");
                        return;
                    }
                }
            }
        }
    }
    
    
    
    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        if(event.isCancelled())return;
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        Location loc = event.getBlock().getLocation();
        
        if(plugin.isBlessed(loc)){
            BytecraftPlayer owner = plugin.getOwner(loc);
            if(!player.getName().equalsIgnoreCase(owner.getName()) 
                    && !player.getRank().canOverrideBless()){
                player.sendMessage(ChatColor.RED + "You can not destroy blessed blocks.");
                event.setCancelled(true);
                player.setFireTicks(20);
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlockPlaced();
        Map<Location, String> blessedBlocks = plugin.getBlessedBlocks();
        if (block.getType() == Material.CHEST) {
            Player player = event.getPlayer();

            Location loc = block.getLocation();
            Location loc1 = loc.add(new Vector(1, 0, 0));
            Location loc2 = loc.subtract(new Vector(1, 0, 0));
            Location loc3 = loc.add(new Vector(0, 0, 1));
            Location loc4 = loc.subtract(new Vector(0, 0, 1));

            if (blessedBlocks.containsKey(loc1)
                    || blessedBlocks.containsKey(loc2)
                    || blessedBlocks.containsKey(loc3)
                    || blessedBlocks.containsKey(loc4)) {

                player.sendMessage(ChatColor.RED
                        + "You can't place a chest next to one that is already blessed.");
                event.setCancelled(true);
                return;
            }
        }
        else if (block.getType() == Material.HOPPER) {
            BytecraftPlayer player = plugin.getPlayer(event.getPlayer());

            Location loc = block.getLocation();
            Location loc1 = loc.subtract(new Vector(0, 1, 0));

            if (blessedBlocks.containsKey(loc)
                    || blessedBlocks.containsKey(loc1)) {

                player.sendMessage(ChatColor.RED
                        + "You can't place a hopper under a blessed chest.");
                event.setCancelled(true);
            }
        }
    }
}
