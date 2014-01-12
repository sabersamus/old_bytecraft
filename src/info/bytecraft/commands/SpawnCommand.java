package info.bytecraft.commands;

import org.bukkit.Bukkit;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class SpawnCommand extends AbstractCommand
{

    public SpawnCommand(Bytecraft instance)
    {
        super(instance, "spawn");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        player.teleport(new org.bukkit.Location(Bukkit.getWorld("world"), -254.5, 7, -134.5, (float) -179.39, 2));
        return true;
    }

}
