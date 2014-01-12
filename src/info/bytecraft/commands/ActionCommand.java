package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class ActionCommand extends AbstractCommand
{

    public ActionCommand(Bytecraft instance)
    {
        super(instance, "me");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length != 0){
            Bukkit.broadcastMessage("* " + player.getDisplayName() + ChatColor.WHITE + " " + messageFromString(args));
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
