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
        player.teleportWithHorse(plugin.getWorldSpawn("world").add(0, 0.5, 0));
        return true;
    }

}
