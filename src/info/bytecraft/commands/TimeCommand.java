package info.bytecraft.commands;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class TimeCommand extends AbstractCommand
{

    public TimeCommand(Bytecraft instance)
    {
        super(instance, "time");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canChangeTime()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        if(args.length != 1)return true;
        String input = args[0];
        if(input.equalsIgnoreCase("day")){
            player.setPlayerTime(6000, false);
        }else if(input.equalsIgnoreCase("night")){
            player.setPlayerTime(18000, false);
        }else if(input.equalsIgnoreCase("normal")){
            player.resetPlayerTime();
        }
        return true;
    }

}
