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
        if (player.isAdmin()) {
            if (args.length == 0) {
                try (IContext ctx = plugin.createContext()) {
                    IPlayerDAO dao = ctx.getPlayerDAO();

                    if (player.hasFlag(Flag.INVISIBLE)) {
                        player.setFlag(Flag.INVISIBLE, false);
                        for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                            other.showPlayer(player.getDelegate());
                        }
                        player.sendMessage(ChatColor.AQUA
                                + "You have re-appeared");
                    }
                    else {
                        player.setFlag(Flag.INVISIBLE, true);
                        for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                            if (other.getRank() == Rank.ELDER) {
                                continue;
                            }
                            other.hidePlayer(player.getDelegate());
                        }
                        player.sendMessage(ChatColor.AQUA
                                + "You have disappeared");
                    }

                    dao.updateFlag(player, Flag.INVISIBLE);
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

}
