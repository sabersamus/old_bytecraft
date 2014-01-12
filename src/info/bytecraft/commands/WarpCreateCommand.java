package info.bytecraft.commands;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBWarpDAO;

public class WarpCreateCommand extends AbstractCommand
{

    public WarpCreateCommand(Bytecraft instance)
    {
        super(instance, "makewarp");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.isAdmin())
            return true;
        if (args.length != 1)
            return true;

        String name = args[0];

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            DBWarpDAO dbWarp = new DBWarpDAO(conn);
            Location l = player.getLocation();
            dbWarp.createWarp(name, l);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Created warp: "
                    + ChatColor.GREEN + name + ChatColor.WHITE + " at "
                    + ChatColor.GREEN + "[" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + "]");
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
        return true;
    }
}
