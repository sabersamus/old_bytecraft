package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SelectListener implements Listener
{
    private Bytecraft plugin;
    
    public SelectListener(Bytecraft instance)
    {
        plugin = instance;
    }
    
    @EventHandler
    public void onSelectZone(PlayerInteractEvent event)
    {
        if(event.isCancelled())return;
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(!player.isAdmin())return;
        if(player.getCurrentZone() != null)return;
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.STICK){
            Block block = event.getClickedBlock();
            if(player.getZoneBlock1() == null && player.getZoneBlock2() == null){
                //player has selected no blocks
                player.setZoneBlock1(block);
                player.sendMessage(ChatColor.YELLOW + "First block of a new zone selected at " + format(block.getLocation()));
                event.setCancelled(true);
                return;
            }else if(player.getZoneBlock1() != null && player.getZoneBlock2() == null){
                //player has selected first block
                player.setZoneBlock2(block);
                player.sendMessage(ChatColor.YELLOW + "Second block of a new zone selected at " + format(block.getLocation()));
                event.setCancelled(true);
            }else if(player.getZoneBlock1() != null && player.getZoneBlock2() != null){
                //both blocks selected
                player.setZoneBlock2(null);
                player.setZoneBlock1(block);
                player.sendMessage(ChatColor.YELLOW + "First block of a new zone selected at " + format(block.getLocation()));
                event.setCancelled(true);
                return;
            }
        }
    }
    
    public String format(Location loc)
    {
        return String.format("[%d, %d]", loc.getBlockX(), loc.getBlockZ());
    }
}
