package info.bytecraft.api.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.ZoneWorld;

public class CallEventListener implements Listener
{   
    
    private Bytecraft plugin;
    
    public CallEventListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void PlayerMoveZone(PlayerMoveEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        Location to = event.getTo();
        Location from = event.getFrom();
        
        ZoneWorld toWorld = plugin.getWorld(to.getWorld());
        
        Zone newZone = toWorld.findZone(to);
        String newName;
        
        ZoneWorld fromWorld = plugin.getWorld(from.getWorld());
        
        Zone fromZone = fromWorld.findZone(from);
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
 
    
    @EventHandler
    public void TeleporZone(PlayerTeleportEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        
        Location to = event.getTo();
        Location from = event.getFrom();
        
        ZoneWorld toWorld = plugin.getWorld(to.getWorld());
        
        Zone newZone = toWorld.findZone(to);
        String newName;
        
        ZoneWorld fromWorld = plugin.getWorld(from.getWorld());
        
        Zone fromZone = fromWorld.findZone(from);
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
    
    
}
