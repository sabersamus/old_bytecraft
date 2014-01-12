package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class ChannelCommand extends AbstractCommand
{
    
    public ChannelCommand(Bytecraft instance)
    {
        super(instance, "channel");
    }
    
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length != 1) {
            return false;
        }

        String channel = args[0];

        player.sendMessage(ChatColor.BLUE + "You are now talking in channel " + channel
                + ".");
        player.sendMessage(ChatColor.BLUE + "Write /channel global to switch to "
                + "the global chat.");
        player.setChatChannel(channel);

        return true;
    }
}
