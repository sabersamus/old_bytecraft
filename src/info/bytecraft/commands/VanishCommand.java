package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

public class VanishCommand extends AbstractCommand
{

    public VanishCommand(Bytecraft instance)
    {
        super(instance, "vanish");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (player.getRank() == Rank.ELDER || player.getRank() == Rank.PRINCESS) {
            if (args.length == 0) {
                vanish(player, args);
            }
            else {
                if ("hiddenloc".equalsIgnoreCase(args[0])
                        || "hl".equalsIgnoreCase(args[0])) {
                    hiddenLocation(player);
                }
                else if ("silentjoin".equalsIgnoreCase(args[0])
                        || "sj".equalsIgnoreCase(args[0])) {
                    silentJoin(player);
                }
            }
        }
        return true;
    }

    private void vanish(BytecraftPlayer player, String[] args)
    {
        if (args.length == 0) {
            try (IContext ctx = plugin.createContext()) {
                IPlayerDAO dao = ctx.getPlayerDAO();

                if (player.hasFlag(Flag.INVISIBLE)) {
                    player.setFlag(Flag.INVISIBLE, false);
                    for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                        other.showPlayer(player.getDelegate());
                    }
                    player.sendMessage(ChatColor.AQUA + "You have re-appeared");
                }
                else {
                    player.setFlag(Flag.INVISIBLE, true);
                    for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                        if (other.getRank() == Rank.ELDER
                                || other.getRank() == Rank.PRINCESS) {
                            continue;
                        }
                        other.hidePlayer(player.getDelegate());
                    }
                    player.sendMessage(ChatColor.AQUA + "You have disappeared");
                }

                dao.updateFlag(player, Flag.INVISIBLE);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void silentJoin(BytecraftPlayer player)
    {
        try (IContext ctx = plugin.createContext()) {
            IPlayerDAO dao = ctx.getPlayerDAO();
            if (player.hasFlag(Flag.SILENT_JOIN)) {
                player.setFlag(Flag.SILENT_JOIN, false);
                player.sendMessage(ChatColor.AQUA
                        + "You will be announced on login");
            }
            else {
                player.setFlag(Flag.SILENT_JOIN, true);
                player.sendMessage(ChatColor.AQUA
                        + "You will no longer be announced on login");
            }

            dao.updateFlag(player, Flag.SILENT_JOIN);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void hiddenLocation(BytecraftPlayer player)
    {
        try (IContext ctx = plugin.createContext()) {
            IPlayerDAO dao = ctx.getPlayerDAO();
            if (player.hasFlag(Flag.HIDDEN_LOCATION)) {
                player.setFlag(Flag.HIDDEN_LOCATION, false);
                player.sendMessage(ChatColor.AQUA
                        + "Your location is no longer hidden");
            }
            else {
                player.setFlag(Flag.HIDDEN_LOCATION, true);
                player.sendMessage(ChatColor.AQUA
                        + "Your location will no longer be revealed on login");
            }

            dao.updateFlag(player, Flag.HIDDEN_LOCATION);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
}
