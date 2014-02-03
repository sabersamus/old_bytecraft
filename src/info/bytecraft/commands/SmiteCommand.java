package info.bytecraft.commands;

import java.util.List;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class SmiteCommand extends AbstractCommand
{

    public SmiteCommand(Bytecraft instance)
    {
        super(instance, "smite");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isAdmin())return true;
        if(args.length != 1)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if(cantidates.size() != 1){
            return true;
        }
        
        BytecraftPlayer victim = cantidates.get(0);
        
        victim.getWorld().strikeLightningEffect(victim.getLocation());
        return true;
    }

}
