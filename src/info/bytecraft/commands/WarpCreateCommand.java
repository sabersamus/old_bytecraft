package info.bytecraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IWarpDAO;

public class WarpCreateCommand extends AbstractCommand
{

    public WarpCreateCommand(Bytecraft instance)
    {
        super(instance, "makewarp");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.getRank().canCreateWarps())
            return true;
        if (args.length != 1)
            return true;

        String name = args[0];

        try (IContext ctx = plugin.createContext()) {
            IWarpDAO dao = ctx.getWarpDAO();
            Location l = player.getLocation();
            if(dao.getWarp(name) != null){
                player.sendMessage(ChatColor.RED + "This is already a warp!");
                return true;
            }
            dao.createWarp(name, l);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Created warp: "
                    + ChatColor.GREEN + name + ChatColor.WHITE + " at "
                    + ChatColor.GREEN + "[" + l.getBlockX() + ", "
                    + l.getBlockY() + ", " + l.getBlockZ() + "]");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
