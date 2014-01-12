package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class BlessCommand extends AbstractCommand
{

    public BlessCommand(Bytecraft instance)
    {
        super(instance, "bless");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isAdmin())return true;
        if(args.length != 1)return true;
        Player delegate = Bukkit.getPlayer(args[0]);
        if(delegate != null){
            BytecraftPlayer target = plugin.getPlayer(delegate);
            player.setBlessTarget(target);
            player.sendMessage(ChatColor.AQUA + "Preparing to bless a block for " + target.getDisplayName());
        }
        return true;
    }
}
