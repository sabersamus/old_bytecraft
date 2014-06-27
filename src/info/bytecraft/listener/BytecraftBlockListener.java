package info.bytecraft.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IPlayerDAO;

public class BytecraftBlockListener implements Listener
{
    private Bytecraft plugin;

    public BytecraftBlockListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (!player.getRank().canBuild() || player.hasFlag(Flag.HARDWARNED)) {
            event.setCancelled(true);
            return;
        }
        
        if((player.getRank().canCompassTeleport()) &&
                player.getItemInHand().getType() == Material.COMPASS){
            event.setCancelled(true);
            return;
        }

        Location loc = event.getBlock().getLocation();
        try (IContext ctx = plugin.createContext()) {
            ILogDAO dbLog = ctx.getLogDAO();
            IPlayerDAO dbPlayer = ctx.getPlayerDAO();
            if (dbLog.isLegal(event.getBlock())) {
                dbPlayer.give(player, plugin.getValue(event.getBlock()));
            }
            dbLog.insertPaperLog(player, loc, event.getBlock().getType(),
                    "broke");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (!player.getRank().canBuild() || player.hasFlag(Flag.HARDWARNED)) {
            event.setCancelled(true);
            return;
        }
        
        Location loc = event.getBlock().getLocation();
        try (IContext ctx = plugin.createContext()) {
            ILogDAO dao = ctx.getLogDAO();
            dao.insertPaperLog(player, loc, event.getBlock().getType(),
                    "placed");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @EventHandler
    public void onExplode(EntityExplodeEvent event)
    {
        if (event.getEntity() instanceof Creeper) {
            event.setCancelled(true);
            return;
        }
        else if (event.getEntityType() == EntityType.PRIMED_TNT) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onIgnite(BlockIgniteEvent event)
    {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBurn(BlockBurnEvent event)
    {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onFall(BlockPhysicsEvent event)
    {
        event.setCancelled(false);
    }
    
    @EventHandler
    public void onDestroy(org.bukkit.event.entity.EntityChangeBlockEvent event)
    {
        if(event.getEntityType() == EntityType.CREEPER || event.getEntityType() == EntityType.ENDER_DRAGON){
            event.setCancelled(true);
        }
    }
}
