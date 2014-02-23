package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;

public class ActionCommand extends AbstractCommand
{

    public ActionCommand(Bytecraft instance)
    {
        super(instance, "me");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length != 0){
            if(!player.hasFlag(Flag.MUTE)){
                Bukkit.broadcastMessage("* " + player.getTemporaryChatName() + ChatColor.WHITE + " " + messageFromString(args));
            }else{
                player.sendMessage(ChatColor.RED + "You are currently muted.");
            }
        }
        return true;
    }
    
    private String messageFromString(String[] array)
    {
        StringBuilder sb = new StringBuilder();
        for(String s: array){
            sb.append(s + " ");
        }
        return sb.toString().trim();
    }
}
