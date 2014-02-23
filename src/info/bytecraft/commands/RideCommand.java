package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class RideCommand extends AbstractCommand
{

    public RideCommand(Bytecraft instance, String command)
    {
        super(instance, command);
    }

    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canRide()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        
        if("rideme".equalsIgnoreCase(command)){
            if(args.length == 0){
                if(player.getPassenger() != null){
                    player.eject();
                }
            }else if(args.length == 1){
                List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
                
                if(cantidates.size() != 1){
                    return true;
                }
                
                BytecraftPlayer target = cantidates.get(0);
                this.rideMe(player, target);
            }
        }else if("ride".equalsIgnoreCase(command)){
            if(args.length == 0){
                if(player.getVehicle() != null){
                    player.getVehicle().eject();    
                }
            }else if(args.length == 1){
                List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
                
                if(cantidates.size() != 1){
                    return true;
                }
                
                BytecraftPlayer target = cantidates.get(0);
                this.rideOther(player, target);
            }
        }
        
        return true;
    }
    
    private void rideMe(BytecraftPlayer player, BytecraftPlayer target)
    {
        player.getDelegate().setPassenger(target.getDelegate());
        player.sendMessage(target.getTemporaryChatName() + ChatColor.AQUA + " is now riding you :3");
        target.sendMessage(ChatColor.AQUA + "You are now riding " + player.getTemporaryChatName());
    }
    
    private void rideOther(BytecraftPlayer player, BytecraftPlayer target)
    {
        target.getDelegate().setPassenger(player.getDelegate());
        player.sendMessage(ChatColor.AQUA + "You are now riding " + target.getTemporaryChatName());
        target.sendMessage(player.getTemporaryChatName() + ChatColor.AQUA + " is now riding you :3");
    }
    
}
