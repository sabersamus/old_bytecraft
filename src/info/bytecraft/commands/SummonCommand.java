package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

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
        if (!player.getRank().canSummon()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        if (args.length != 1)
            return true;
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);
        target.teleportWithHorse(player.getLocation());
        player.sendMessage(ChatColor.AQUA + "You summoned "
                + target.getTemporaryChatName() + ChatColor.AQUA + " to you");
        target.sendMessage(player.getTemporaryChatName() + ChatColor.AQUA
                + " summoned you");
        return true;
    }

}
