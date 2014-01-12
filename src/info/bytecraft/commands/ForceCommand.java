package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

/**
 * @author Peter Ogdin - anon16 
 */
public class ForceCommand extends AbstractCommand
{

    public ForceCommand(Bytecraft instance)
    {
        super(instance, "force");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length != 2)return true;
        
        Player delegate = Bukkit.getPlayer(args[0]);
        if(delegate != null){
            BytecraftPlayer target = plugin.getPlayer(delegate);
            
            String channel = args[1];
            
            target.setChatChannel(channel);
            player.setChatChannel(channel);
            
            player.sendMessage(ChatColor.AQUA + "You are now talking in channel "+ channel);
            target.sendMessage(ChatColor.AQUA + "You have been forced into channel " + channel);
            target.sendMessage(ChatColor.AQUA + "To return to the global channel type /channel global");
        }
        return true;
    }
}
