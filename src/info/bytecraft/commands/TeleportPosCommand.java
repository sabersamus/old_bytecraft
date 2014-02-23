package info.bytecraft.commands;

import org.bukkit.Location;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class TeleportPosCommand extends AbstractCommand
{

    public TeleportPosCommand(Bytecraft instance)
    {
        super(instance, "tppos");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canTeleportToPosition()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        if(args.length != 3)return true;
        int x, y, z;
        try{
            x = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
            x = player.getLocation().getBlockX();
        }
        try{
            y = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            y = player.getLocation().getBlockY();
        }
        try{
            z = Integer.parseInt(args[2]);
        }catch(NumberFormatException e){
            z = player.getLocation().getBlockZ();
        }
        player.teleportWithHorse(new Location(player.getWorld(), x, y, z));
        return true;
    }

}
