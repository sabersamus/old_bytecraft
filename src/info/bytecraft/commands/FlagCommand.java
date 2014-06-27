package info.bytecraft.commands;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;

public class FlagCommand extends AbstractCommand
{

    public FlagCommand(Bytecraft instance)
    {
        super(instance, "flag");
    }
    
    @Override
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length != 0){
            return true;
        }
        
        for(Flag flag: Flag.values()){
            if(!player.hasFlag(flag)){
                player.sendMessage(flag + ": False");
            }else{
                player.sendMessage(flag + ": True");
            }
        }
        
        return true;
    }

}
