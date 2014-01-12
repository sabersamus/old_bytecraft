package info.bytecraft.listener;

import java.sql.Connection;
import java.sql.SQLException;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBLogDAO;
import info.bytecraft.database.DBPlayerDAO;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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
        if (!player.getRank().canBuild()) {
            event.setCancelled(true);
            return;
        }
        Location loc = event.getBlock().getLocation();
        Connection conn = null;
        DBLogDAO dbLog = null;
        DBPlayerDAO dbPlayer = null;
        try {
            conn = ConnectionPool.getConnection();
            dbLog = new DBLogDAO(conn);
            dbPlayer = new DBPlayerDAO(conn);
            if(dbLog.isLegal(event.getBlock())){
                dbPlayer.give(player, plugin.getValue(event.getBlock()));
            }
            dbLog.insertPaperLog(player, loc, event.getBlock().getType(),
                    "broke");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (!player.getRank().canBuild()) {
            event.setCancelled(true);
            return;
        }
        Location loc = event.getBlock().getLocation();
        Connection conn = null;
        DBLogDAO dbLog = null;
        try {
            conn = ConnectionPool.getConnection();
            dbLog = new DBLogDAO(conn);
            dbLog.insertPaperLog(player, loc, event.getBlock().getType(),
                    "placed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    @EventHandler
    public void onExplode(EntityExplodeEvent event)
    {
        if(event.getEntity() instanceof Creeper){
            event.setCancelled(true);
            return;
        }else if(event.getEntityType() == EntityType.PRIMED_TNT){
        	event.setCancelled(true);
        }
    }
}
