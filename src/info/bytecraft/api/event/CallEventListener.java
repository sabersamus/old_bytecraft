package info.bytecraft.api.event;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.math.Point;
import info.bytecraft.zones.Zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CallEventListener implements Listener
{   
    
    private Bytecraft plugin;
    
    public CallEventListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
/*    @EventHandler
    public void PlayerChangeLot(PlayerMoveEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        Location to = event.getTo();
        Location from = event.getFrom();
        
        Zone zone = plugin.getZoneAt(to.getWorld(), new Point(to.getBlockX(), to.getBlockZ()));
        Zone fromZone = plugin.getZoneAt(from.getWorld(), new Point(from.getBlockX(), from.getBlockZ()));
        if(fromZone == null && zone == null){
            return;
        }
        Lot newLot = null, oldLot = null;
        
        if(zone != null){
            newLot = zone.findLot(to);
        }
        
        if(fromZone != null){
            oldLot = fromZone.findLot(from);
        }
        
        if (oldLot == null && newLot == null) {
            return;
        }
        
        if (oldLot.getName().equalsIgnoreCase(newLot.getName())) {
            return;
        }
        
        PlayerChangeLotEvent customEvent = new PlayerChangeLotEvent(event.getFrom(), event.getTo(), player, oldLot, newLot);
        plugin.getServer().getPluginManager().callEvent(customEvent);
    }*/
    
    @EventHandler
    public void PlayerMoveZone(PlayerMoveEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        Location to = event.getTo();
        Location from = event.getFrom();
        
        Zone newZone = plugin.getZoneAt(to.getWorld(), new Point(to.getBlockX(), to.getBlockZ()));
        String newName;
        
        Zone fromZone = plugin.getZoneAt(from.getWorld(), new Point(from.getBlockX(), from.getBlockZ()));
        String fromName;
        if (fromZone == null && newZone == null) {
            return;
        }
        
        if (fromZone == null) {
            fromName = "null";
        } else {
            fromName = fromZone.getName();
        }
        
        if (newZone == null) {
            newName = "null";
        } else {
            newName = newZone.getName();
        }
        
        if (fromName.equalsIgnoreCase(newName)) {
            return;
        }
        
        PlayerChangeZoneEvent customEvent = new PlayerChangeZoneEvent(to, from, player, fromZone, newZone);
        Bukkit.getServer().getPluginManager().callEvent(customEvent);
    }
    
    @EventHandler
    public void PlayerMoveBlock(PlayerMoveEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        if (event.getFrom().getX() == event.getTo().getX() && 
                event.getFrom().getY() == event.getTo().getY() && 
                event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }
        
        PlayerMoveBlockEvent customEvent = new PlayerMoveBlockEvent(event.getFrom(), event.getTo(), player);
        Bukkit.getPluginManager().callEvent(customEvent);
    }

}
