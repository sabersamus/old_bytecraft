package info.bytecraft.listener;

import java.util.EnumSet;
import java.util.Set;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Zone;
import info.bytecraft.api.Zone.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class ZoneListener implements Listener
{

    private Bytecraft plugin;

    public ZoneListener(Bytecraft instance)
    {
        plugin = instance;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Location to = event.getTo();
        Location from = event.getFrom();
        for(Zone zone: plugin.getZones(to.getWorld().getName())){
            if(zone.contains(to) && !zone.contains(from)){
                Permission p = zone.getUser(player);
                if(zone.isWhitelisted()){
                    if((p == null || p == Permission.BANNED)){//whitelisted and doesnt have an account, or is banned
                        if(!player.isAdmin()){//doesnt affect admins
                            movePlayerBack(player, from, to);
                            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed in " + zone.getName());
                            return;
                        }else{
                            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getEnterMsg());
                            this.permissionsMessage(zone, player);
                            player.setCurrentZone(zone);
                            return;
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getEnterMsg());
                        this.permissionsMessage(zone, player);
                        player.setCurrentZone(zone);
                        return;
                    }
                }else{
                    if((p != null && p == Permission.BANNED) && !player.isAdmin()){
                        movePlayerBack(player, from, to);
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed in " + zone.getName());
                        return;
                    }else{
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getEnterMsg());
                        this.permissionsMessage(zone, player);
                        player.setCurrentZone(zone);
                        return;
                    }
                }
            }else if(zone.contains(from) && !zone.contains(to)){
                player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getExitMsg());
                player.setCurrentZone(null);
                return;
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(player.getCurrentZone() != null){
            Zone zone = player.getCurrentZone();
            if(!zone.isBuildable()){
                Permission p = zone.getUser(player);
                if(p == null){
                    if(!player.isAdmin()){
                        event.setBuild(false);
                        event.setCancelled(true);
                        player.setFireTicks(20 * 2);
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed to build in " + zone.getName());
                        return;
                    }
                }else{
                    if((p != Permission.MAKER && p != Permission.OWNER)){
                        if(!player.isAdmin()){
                            event.setBuild(false);
                            event.setCancelled(true);
                            player.setFireTicks(20 * 2);
                            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed to build in " + zone.getName());
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(player.getCurrentZone() != null){
            Zone zone = player.getCurrentZone();
            if(!zone.isBuildable()){
                Permission p = zone.getUser(player);
                if(p == null){
                    if(!player.isAdmin()){
                        event.setCancelled(true);
                        player.setFireTicks(20 * 2);
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed to build in " + zone.getName());
                        return;
                    }
                }else{
                    if((p != Permission.MAKER && p != Permission.OWNER)){
                        if(!player.isAdmin()){
                            event.setCancelled(true);
                            player.setFireTicks(20 * 2);
                            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed to build in " + zone.getName());
                            return;
                        }
                    }
                }
            }
        }
    }

    public void permissionsMessage(Zone zone, BytecraftPlayer player)
    {
        String prefix = ChatColor.RED + "[" + zone.getName() + "] ";
        String message = null;
        Permission perm = zone.getUser(player);
        if(perm != null){
            switch(perm){
            case ALLOWED: message = prefix + "You are allowed in " + zone.getName();
            break;
            case BANNED: message = prefix + "You are banned from " + zone.getName();
            break;
            case OWNER: message = prefix + "You are an owner in " + zone.getName();
            break;
            case MAKER: message = prefix + "You are a maker in " + zone.getName();
            break;
            default: break;
            }
            if(message != null && !message.equalsIgnoreCase("")){
                player.sendMessage(message);
            }
        }
    }

    private final Set<EntityType> types = EnumSet.of(BLAZE, CREEPER, ENDERMAN, ENDER_DRAGON, CAVE_SPIDER, MAGMA_CUBE, SLIME,
            SILVERFISH, SKELETON, SPIDER, WITCH, WITHER, ZOMBIE);

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event)
    {
        Entity ent = event.getEntity();
        for(Zone zone: plugin.getZones(ent.getWorld().getName())){
            if(zone.contains(ent.getLocation())){
                if(!zone.isHostile()){
                    if(types.contains(ent.getType())){
                        event.setCancelled(true);
                        ent = null;
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)
    {
        Location to = event.getTo();
        Location from = event.getFrom();
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        for(Zone zone: plugin.getZones(to.getWorld().getName())){
            if(zone.contains(to) && !zone.contains(from)){
                Permission p = zone.getUser(player);
                if(zone.isWhitelisted()){
                    if((p == null || p == Permission.BANNED)){
                        if(!player.isAdmin()){
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed in " + zone.getName());
                            return;
                        }else{
                            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getEnterMsg());
                            this.permissionsMessage(zone, player);
                            player.setCurrentZone(zone);
                            return;
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getEnterMsg());
                        this.permissionsMessage(zone, player);
                        player.setCurrentZone(zone);
                        return;
                    }
                }else{
                    if((p != null && p == Permission.BANNED) && !player.isAdmin()){
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] You are not allowed in " + zone.getName());
                        return;
                    }else{
                        player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getEnterMsg());
                        this.permissionsMessage(zone, player);
                        player.setCurrentZone(zone);
                        return;
                    }
                }
            }else if(zone.contains(from) && !zone.contains(to)){
                player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] " + zone.getExitMsg());
                player.setCurrentZone(null);
                return;
            }
        }
    }
    
    private void movePlayerBack(BytecraftPlayer player, Location movingFrom,
            Location movingTo)
    {
        Vector a = new Vector(movingFrom.getX(),
                              movingFrom.getY(),
                              movingFrom.getZ());

        Vector b = new Vector(movingTo.getX(),
                              movingTo.getY(),
                              movingTo.getZ());

        Vector diff = b.subtract(a);
        diff = diff.multiply(-5);

        Vector newPosVector = a.add(diff);

        Location newPos = new Location(player.getWorld(),
                                       newPosVector.getX(),
                                       newPosVector.getY(),
                                       newPosVector.getZ());

        player.teleport(newPos);
    }
}
