package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ItemFrameListener implements Listener
{
    private Bytecraft plugin;
    
    public ItemFrameListener(Bytecraft instance)
    {
        this.plugin = instance;
    }
    
    @EventHandler
    public void onRotate(PlayerInteractEntityEvent event)
    {
        if (!(event.getRightClicked().getType().equals(EntityType.ITEM_FRAME))) { // Before we do anything lets check were interacting with ItemFrames
            return;
        }
        
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());

        Location location = event.getRightClicked().getLocation();
        
        if (!player.hasBlockPermission(location, true)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof ItemFrame)) {
            return;
        }
        
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        BytecraftPlayer player = plugin.getPlayer((Player) event.getDamager());
        
        if (!player.hasBlockPermission(event.getEntity().getLocation(), true)) {
            event.setCancelled(true);
        }
    }
    
}
