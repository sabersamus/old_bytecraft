package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Notification;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.commands.AbstractCommand;

public class MessageCommand extends AbstractCommand
{

    public MessageCommand(Bytecraft instance)
    {
        super(instance, "message");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length >= 2) {

            List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
            if (cantidates.size() != 1) {
                return true;
            }

            BytecraftPlayer target = cantidates.get(0);
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i] + " ");
            }

            target.sendNotification(Notification.MESSAGE, ChatColor.GOLD
                    + "<From> " + player.getDisplayName() + ": "
                    + ChatColor.GREEN + message.toString().trim());
            if (!target.hasFlag(Flag.INVISIBLE)) {
                player.sendMessage(ChatColor.GOLD + "<To> "
                        + target.getDisplayName() + ": " + ChatColor.GREEN
                        + message.toString().trim());
            }
        }
        return true;
    }

}
