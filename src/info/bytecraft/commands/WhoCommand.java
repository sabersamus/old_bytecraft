package info.bytecraft.commands;

import static org.bukkit.ChatColor.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;

public class WhoCommand extends AbstractCommand
{

    public WhoCommand(Bytecraft instance)
    {
        super(instance, "who");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();
            String delim = "";
            int playerCounter = 0;
            for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                if (other.hasFlag(Flag.INVISIBLE)) {
                    continue;
                }
                sb.append(delim);
                sb.append(other.getDisplayName());
                delim = ChatColor.WHITE + ", ";
                playerCounter++;
            }
            player.sendMessage(GRAY + "************" + DARK_PURPLE
                    + "Player List" + GRAY + "************");
            player.sendMessage(sb.toString().trim());
            player.sendMessage(GRAY + "************" + GOLD + playerCounter
                    + " player(s) online" + GRAY + "*****");
        }
        else if (args.length == 1) {
            if (!player.isAdmin())
                return true;
            Player delegate = Bukkit.getPlayer(args[0]);
            if (delegate != null) {
                BytecraftPlayer target = plugin.getPlayer(delegate);
                whoOther(player.getDelegate(), target);
            }
        }
        return true;
    }

    public boolean handleOther(Server server, String[] args)
    {
        ConsoleCommandSender sender = server.getConsoleSender();
        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();
            String delim = "";
            int playerCounter = 0;
            for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                sb.append(delim);
                sb.append(other.getDisplayName());
                delim = ChatColor.WHITE + ", ";
                playerCounter++;
            }
            sender.sendMessage(GRAY + "************" + DARK_PURPLE
                    + "Player List" + GRAY + "************");
            sender.sendMessage(sb.toString().trim());
            sender.sendMessage(GRAY + "************" + GOLD + playerCounter
                    + " player(s) online" + GRAY + "*****");
        }
        else if (args.length == 1) {
            Player delegate = Bukkit.getPlayer(args[0]);
            if (delegate != null) {
                BytecraftPlayer target = plugin.getPlayer(delegate);
                whoOther(sender, target);
            }
        }
        return true;
    }
    
    public void whoOther(CommandSender player, BytecraftPlayer target)
    {
        int x = target.getLocation().getBlockX();
        int y = target.getLocation().getBlockY();
        int z = target.getLocation().getBlockZ();

        String ip = target.getAddress().getHostName();
        
        player.sendMessage(DARK_GRAY + "******************** "
                + DARK_PURPLE + "PLAYER INFO" + DARK_GRAY
                + " ********************");
        player.sendMessage(GRAY + "Player: " + target.getDisplayName());
        player.sendMessage(GRAY + "Id: " + GREEN + target.getId());
        player.sendMessage(GRAY + "World: " + GREEN
                + target.getWorld().getName());
        player.sendMessage(GRAY + "Location: " + GREEN + x + ", " + y
                + ", " + z);
        player.sendMessage(GRAY + "Channel: " + GREEN
                + target.getChatChannel());
        player.sendMessage(GRAY + "Wallet: "
                + target.getFormattedBalance());
        player.sendMessage(GRAY + "IP: " + GREEN
                + ip);
        player.sendMessage(DARK_GRAY
                + "******************************************************");
    }
}
