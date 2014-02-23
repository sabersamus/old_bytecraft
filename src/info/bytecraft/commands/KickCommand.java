package info.bytecraft.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IReportDAO;

public class KickCommand extends AbstractCommand
{

    public KickCommand(Bytecraft instance)
    {
        super(instance, "kick");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.getRank().canKick()) {
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }

        if (args.length != 1) {
            return true;
        }

        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);
        target.kickPlayer(ChatColor.RED + "You were kicked by "
                + player.getTemporaryChatName());
        Bukkit.broadcastMessage(target.getTemporaryChatName() + ChatColor.RED
                + " was kicked by " + player.getTemporaryChatName());

        try (IContext ctx = plugin.createContext()) {
            PlayerReport report = new PlayerReport();
            report.setSubjectName(target.getName());
            report.setIssuerName(player.getName());
            report.setAction(PlayerReport.Action.KICK);
            report.setMessage("Kicked by " + player.getName());

            IReportDAO reportDAO = ctx.getReportDAO();
            reportDAO.insertReport(report);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean handleOther(Server server, String[] args)
    {
        if (args.length != 1)
            return true;

        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);

        try (IContext ctx = plugin.createContext()) {
            PlayerReport report = new PlayerReport();
            report.setSubjectName(target.getName());
            report.setIssuerName("CONSOLE");
            report.setAction(PlayerReport.Action.KICK);
            report.setMessage("Kicked by console");

            IReportDAO reportDAO = ctx.getReportDAO();
            reportDAO.insertReport(report);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        target.kickPlayer(ChatColor.RED + "You were kicked by "
                + ChatColor.BLUE + "GOD");
        Bukkit.broadcastMessage(target.getTemporaryChatName() + ChatColor.RED
                + " was kicked by " + ChatColor.BLUE + "GOD");
        return true;
    }
}
