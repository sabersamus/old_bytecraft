package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

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
        if (!player.getRank().canBless()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        if (args.length != 1)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);
        player.setBlessTarget(target);
        player.sendMessage(ChatColor.AQUA + "Preparing to bless a block for " + target.getTemporaryChatName());
        return true;
    }
}
