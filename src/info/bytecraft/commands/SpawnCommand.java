package info.bytecraft.commands;

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
        player.teleport(plugin.getWorldSpawn(player.getWorld().getName()));
        return true;
    }

}
