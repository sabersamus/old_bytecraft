package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Notification;

public class KickCommand extends AbstractCommand
{

    public KickCommand(Bytecraft instance)
    {
        super(instance, "kick");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.isModerator()) {
            player.sendNotification(Notification.COMMAND_FAIL);
            return true;
        }

        if (args.length != 1) {
            player.sendNotification(Notification.COMMAND_FAIL);
            return true;
        }

        Player delegate = Bukkit.getPlayer(args[0]);
        if (delegate != null) {
            BytecraftPlayer target = plugin.getPlayer(delegate);
            target.kickPlayer(ChatColor.RED + "You were kicked by "
                    + player.getDisplayName());
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED
                    + " was kicked by " + player.getDisplayName());
        }

        return true;
    }

    public boolean handleOther(Server server, String[] args)
    {
        if (args.length != 1)
            return true;
        Player delegate = server.getPlayer(args[0]);

        if (delegate != null) {
            BytecraftPlayer target = plugin.getPlayer(delegate);

            target.kickPlayer(ChatColor.RED + "You were kicked by "
                    + ChatColor.BLUE + "GOD");
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED
                    + " was kicked by " + ChatColor.BLUE + "GOD");
        }
        return true;
    }
}
