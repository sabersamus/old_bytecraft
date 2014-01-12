package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class SummonCommand extends AbstractCommand
{

    public SummonCommand(Bytecraft instance)
    {
        super(instance, "summon");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isAdmin())return true;
        if(args.length != 1)return true;
        Player delegate = Bukkit.getPlayer(args[0]);
        if(delegate != null){
            BytecraftPlayer target = plugin.getPlayer(delegate);
            target.teleport(player.getLocation());
            player.sendMessage(ChatColor.AQUA +"You summoned " + target.getDisplayName() + ChatColor.AQUA + " to you");
            target.sendMessage(player.getDisplayName() + ChatColor.AQUA + " summoned you");
        }
        return true;
    }

}
