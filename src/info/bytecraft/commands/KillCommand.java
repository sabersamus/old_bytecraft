package info.bytecraft.commands;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class KillCommand extends AbstractCommand
{
    
    public KillCommand(Bytecraft plugin)
    {
        super(plugin);
    }
    
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length != 0)return true;
        player.damage(20000);
        return true;
    }
    
}
