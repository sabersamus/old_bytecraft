package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

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
        if (args.length != 2)
            return true;

        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);

        String channel = args[1];

        target.setChatChannel(channel);
        player.setChatChannel(channel);

        player.sendMessage(ChatColor.AQUA + "You are now talking in channel "
                + channel);
        target.sendMessage(ChatColor.AQUA
                + "You have been forced into channel " + channel);
        target.sendMessage(ChatColor.AQUA
                + "To return to the global channel type /channel global");
        return true;
    }
}
