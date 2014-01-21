package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Notification;
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
    
    private String argsToMessage(String[] args)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 1; i < args.length; ++i) {
            buf.append(" ");
            buf.append(args[i]);
        }

        return buf.toString();
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.isModerator()) {
            player.sendNotification(Notification.COMMAND_FAIL, null);
            return true;
        }

        if(args.length < 2){
            return true;
        }

        String message = argsToMessage(args);
        Player delegate = Bukkit.getPlayer(args[0]);
        if (delegate != null) {
            BytecraftPlayer target = plugin.getPlayer(delegate);
            target.kickPlayer(ChatColor.RED + "You were kicked by "
                    + player.getDisplayName());
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED
                    + " was kicked by " + player.getDisplayName());
            
            try(IContext ctx = Bytecraft.createContext()){
                PlayerReport report = new PlayerReport();
                report.setSubjectId(target.getId());
                report.setIssuerId(player.getId());
                report.setAction(PlayerReport.Action.KICK);
                report.setMessage(message);

                IReportDAO reportDAO = ctx.getReportDAO();
                reportDAO.insertReport(report);
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public boolean handleOther(Server server, String[] args)
    {
        if (args.length != 1)
            return true;
        Player delegate = server.getPlayer(args[0]);

        if (delegate != null) {
            BytecraftPlayer target = plugin.getPlayer(delegate);

            target.kickPlayer(ChatColor.RED + "You were kicked by "
                    + ChatColor.BLUE + "GOD");
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED
                    + " was kicked by " + ChatColor.BLUE + "GOD");
        }
        return true;
    }
}
