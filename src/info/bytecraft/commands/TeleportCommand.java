package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;

import info.tregmine.api.math.Distance;

public class TeleportCommand extends AbstractCommand
{

    private class TeleportTask implements Runnable
    {
        private BytecraftPlayer player;
        private BytecraftPlayer target;

        public TeleportTask(BytecraftPlayer player, BytecraftPlayer target)
        {
            this.player = player;
            this.target = target;
        }

        @Override
        public void run()
        {
            player.teleportWithHorse(target.getLocation().add(0, 0.5, 0));

            player.setNoDamageTicks(1000);
            if (!player.getRank().canTeleportSilently()) {
                target.sendMessage(player.getTemporaryChatName() + ChatColor.AQUA
                        + " has teleported to you");
            }
        }

    }

    public TeleportCommand(Bytecraft instance)
    {
        super(instance, "teleport");
    }

    public boolean handlePlayer(final BytecraftPlayer player, String[] args)
    {
        if (args.length != 1)
            return true;
        int maxDistance = player.getRank().getMaxTeleportDistance();
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);
        if (Distance.calc2d(player.getLocation(), target.getLocation()) > maxDistance) {
            player.sendMessage(ChatColor.RED + "You are too far to teleport!");
            return true;
        }
        else {
            if (target.hasFlag(Flag.TPBLOCK)) {// teleport block
                if (!player.getRank().canOverrideTeleportBlock()) {// not admin
                    player.sendMessage(target.getTemporaryChatName() + ChatColor.RED
                            + " is not accepting teleports right now");
                    target.sendMessage(player.getTemporaryChatName() + ChatColor.RED
                            + " failed at teleporting to you");
                    return true;
                }
                else {// admin override
                    TeleportTask task = new TeleportTask(player, target);
                    plugin.getServer()
                            .getScheduler()
                            .scheduleSyncDelayedTask(plugin, task,
                                    player.getRank().getTeleportTimeout());
                    player.sendMessage(ChatColor.AQUA + "Teleporting to "
                            + target.getTemporaryChatName());
                    return true;
                }
            }
            else {// non teleport block
                TeleportTask task = new TeleportTask(player, target);
                plugin.getServer()
                        .getScheduler()
                        .scheduleSyncDelayedTask(plugin, task,
                                player.getRank().getTeleportTimeout());
                player.sendMessage(ChatColor.AQUA + "Teleporting to "
                        + target.getTemporaryChatName());
                return true;
            }
        }
    }
}
