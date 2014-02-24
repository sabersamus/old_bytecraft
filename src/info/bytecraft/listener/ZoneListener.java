package info.bytecraft.listener;

import static org.bukkit.entity.EntityType.*;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BooleanStringReturn;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.event.PlayerChangeZoneEvent;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.Zone.Flag;
import info.bytecraft.zones.Zone.Permission;
import info.bytecraft.zones.ZoneWorld;

public class ZoneListener implements Listener
{

    private Bytecraft plugin;

    public ZoneListener(Bytecraft instance)
    {
        plugin = instance;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(player == null)return;
        
        Location l = player.getLocation();
        
        ZoneWorld world = plugin.getWorld(l.getWorld());
        
        Zone zone = world.findZone(l); 
                
        if(zone == null)return;
        
        this.welcomeMessage(zone, player, zone.getUser(player));
        
        if(zone.hasFlag(Flag.CREATIVE)){
            player.loadInventory(zone.getName(), false);
            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] This is a creative zone. "
                    + "Your inventory will be saved when you leave.");
            return;
        }
        
        if(zone.hasFlag(Flag.INVENTORY)){
            player.loadInventory(zone.getName(), true);
            return;
        }
    }
    
    @EventHandler
    public void onMoveZone(PlayerChangeZoneEvent event)
    {
        BytecraftPlayer player = event.getPlayer();
        if(event.getOldZone() != null){
           
            Zone zone = event.getOldZone();
            
            if(zone.hasFlag(Flag.CREATIVE)){
                player.loadInventory("survival", false);
                player.setGameMode(GameMode.SURVIVAL);
            }
            
            if(zone.hasFlag(Flag.INVENTORY)){
                player.loadInventory("survival", true);
            }
            
            player.sendMessage(ChatColor.RED + "[" + event.getOldZone().getName()
                    + "] " + event.getOldZone().getExitMessage());
        }
        
        if(event.getNewZone() == null){
            return;
        }
        
        BooleanStringReturn returnValue = player.canBeHere(event.getTo());
        
        if (!returnValue.getBoolean()) {
            player.sendMessage(returnValue.getString());
            movePlayerBack(player, event.getFrom(), event.getTo());
            return;
        }
        
        Zone zone = event.getNewZone();
        
        welcomeMessage(zone, player, zone.getUser(player));
        
        if(zone.hasFlag(Flag.CREATIVE)){
            player.setGameMode(GameMode.CREATIVE);
            player.loadInventory(zone.getName(), true);
            player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] This is a creative zone. "
                    + "Your inventory will be saved when you leave.");
            return;
        }
        
        if(zone.hasFlag(Flag.INVENTORY)){
            player.loadInventory(zone.getName(), true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlock();
        
        boolean bool = player.hasBlockPermission(block.getLocation(), true);
        if(!bool){
            player.setFireTicks(20 * 2);
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlock();
        
        boolean bool = player.hasBlockPermission(block.getLocation(), true);
        if(!bool){
            player.setFireTicks(20 * 2);
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Location loc = player.getLocation();
        
        ZoneWorld world = plugin.getWorld(loc.getWorld());
        
        Zone zone = world.findZone(loc);
        if(zone == null)return;
        
        if(zone.hasFlag(Flag.CREATIVE)){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't drop items in a creative zone.");
        }
    }
    
    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlockClicked();
        
        boolean bool = player.hasBlockPermission(block.getLocation(), true);
        if(!bool){
            player.setFireTicks(20 * 2);
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFill(PlayerBucketFillEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlockClicked();
        
        boolean bool = player.hasBlockPermission(block.getLocation(), true);
        if(!bool){
            player.setFireTicks(20 * 2);
            event.setCancelled(true);
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
        Location loc = event.getLocation();
        
        ZoneWorld world = plugin.getWorld(loc.getWorld());
        
        Zone zone = world.findZone(loc);
        
        if(zone == null)return;
        
        if(!zone.hasFlag(Flag.HOSTILES)){
            if(types.contains(ent.getType())){
                event.setCancelled(true);
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
    
    private void welcomeMessage(Zone currentZone, BytecraftPlayer player,
            Zone.Permission perm)
    {

        player.sendMessage(ChatColor.RED + "[" + currentZone.getName() + "] "
                + currentZone.getEnterMessage());

        if (currentZone.hasFlag(Flag.PVP)) {
            player.sendMessage(ChatColor.RED
                    + "[" + currentZone.getName() + "] "
                    + "Warning! This is a PVP zone! Other players can damage or kill you here.");
        }

        if (perm != null) {
            String permNotification = perm.getPermNotification();
            player.sendMessage(ChatColor.RED + "[" + currentZone.getName()
                    + "] " + permNotification);
        }
    }
}
