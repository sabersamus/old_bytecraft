package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IHomeDAO;

public class HomeCommand extends AbstractCommand
{

    public HomeCommand(Bytecraft instance)
    {
        super(instance, "home");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.hasFlag(Flag.NOBLE))
            return true;

        if (args.length == 0) {
            if (goHome(player))
                return true;
            else {
                player.sendMessage(ChatColor.RED
                        + "You have not yet saved your home");
                return true;
            }
        }
        else if (args.length == 1) {
            if ("save".equalsIgnoreCase(args[0])) {
                setHome(player);
                player.sendMessage(ChatColor.AQUA + "Home saved");
            }
        }
        else if (args.length == 2) {
            if ("to".equalsIgnoreCase(args[0]) && player.isAdmin()) {
                if (homeTo(player, args[1])) {
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED
                            + "No home found for player: " + ChatColor.WHITE
                            + args[1]);
                }
            }
        }

        return true;
    }

    private void setHome(BytecraftPlayer player)
    {
        try (IContext ctx = plugin.createContext()) {
            IHomeDAO dao = ctx.getHomeDAO();
            dao.setHome(player);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean goHome(final BytecraftPlayer player)
    {
        try (IContext ctx = plugin.createContext()) {
            IHomeDAO dao = ctx.getHomeDAO();
            if (dao.getHome(player) == null)
                return true;

            player.sendMessage(ChatColor.AQUA + "Initiating teleport to home!");
            final Location loc = dao.getHome(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    new Runnable() {

                        public void run()
                        {
                            player.teleportWithVehicle(loc);
                        }

                    }, 20 * 3L);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private boolean homeTo(final BytecraftPlayer player, final String toName)
    {
        try (IContext ctx = plugin.createContext()) {
            IHomeDAO dao = ctx.getHomeDAO();
            if (dao.getHome(toName) == null)
                return false;
            player.sendMessage(ChatColor.AQUA + "Initiating teleport to "
                    + toName + "'s home!");
            final Location loc = dao.getHome(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    new Runnable() {

                        public void run()
                        {
                            player.teleportWithVehicle(loc);
                        }

                    }, 20 * 3L);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
