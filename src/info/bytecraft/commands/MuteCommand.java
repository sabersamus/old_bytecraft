package info.bytecraft.commands;

import java.sql.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.PlayerReport.Action;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IReportDAO;

public class MuteCommand extends AbstractCommand
{

    public MuteCommand(Bytecraft instance)
    {
        super(instance, "mute");
    }

    public String modifierFrom(String args)
    {
        return args.split(":")[1];
    }

    public int timeFrom(String args)
    {
        try {
            return Integer.parseInt(args.split(":")[0]);
        } catch (NumberFormatException e) {
            return modifierFrom(args).equalsIgnoreCase("m") ? 5 : 1;
        }
    }

    public boolean handleUser(BytecraftPlayer player, String[] args)
    {
        if (!player.isModerator())
            return true;
        if (args.length != 2)
            return true;

        Player delegate = Bukkit.getPlayer(args[0]);
        BytecraftPlayer victim = plugin.getPlayer(delegate);
        if (victim.isModerator())
            return true;

        boolean hours = modifierFrom(args[1]).equalsIgnoreCase("h");
        long l =  60 * 1000l * timeFrom(args[1]);
        String time = l + " minutes";

        if (hours) {
            l = l * 60L;
            time = l + " hours";
        }

        try (IContext ctx = plugin.createContext()) {
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setAction(Action.MUTE);
            report.setIssuerName(player.getName());
            report.setSubjectName(victim.getName());
            report.setTimestamp(new Date(System.currentTimeMillis()));
            report.setValidUntil(new Date(System.currentTimeMillis() + l));
            dao.insertReport(report);

            player.sendMessage(ChatColor.RED + "You muted "
                    + victim.getDisplayName() + ChatColor.RED + " for" + time);
            victim.sendMessage(ChatColor.RED + "You have been muted for" + time);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
