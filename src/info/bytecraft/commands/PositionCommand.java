package info.bytecraft.commands;

import org.bukkit.Location;

import static org.bukkit.ChatColor.*;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.tregmine.api.math.Distance;

public class PositionCommand extends AbstractCommand
{

    public PositionCommand(Bytecraft instance)
    {
        super(instance, "pos");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        Location loc = player.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        String world = loc.getWorld().getName();
        int distance = (int) Distance.calc2d(loc, plugin.getWorldSpawn(world));
        
        player.sendMessage(GOLD + "World: " + WHITE + world);
        player.sendMessage(GOLD + "X: " + WHITE + x + "(" + GRAY + (int)x + ")");
        player.sendMessage(GOLD + "Y: " + WHITE + y + "(" + GRAY + (int)y + ")");
        player.sendMessage(GOLD + "Z: " + WHITE + z + "(" + GRAY + (int)z + ")");
        player.sendMessage(GOLD + "Yaw: " + WHITE + yaw);
        player.sendMessage(GOLD + "Pitch: " + WHITE + pitch);
        player.sendMessage(GOLD + "Distance from spawn: " + distance);
        return true;
    }

}
