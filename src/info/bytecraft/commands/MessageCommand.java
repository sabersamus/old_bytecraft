package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
            Player delegate = Bukkit.getPlayer(args[0]);
            if (delegate != null) {
                BytecraftPlayer target =
                        plugin.getPlayer(delegate);
                StringBuilder message = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    message.append(args[i] + " ");
                }

                target.sendNotification(Notification.MESSAGE, ChatColor.GOLD + "<From> "
                        + player.getDisplayName() + ": " + ChatColor.GREEN
                        + message.toString().trim());
                if (!target.hasFlag(Flag.INVISIBLE)) {
                    player.sendMessage(ChatColor.GOLD + "<To> "
                            + target.getDisplayName() + ": " + ChatColor.GREEN
                            + message.toString().trim());
                }
            }
        }
        return true;
    }

}
