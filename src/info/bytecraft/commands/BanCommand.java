package info.bytecraft.commands;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IReportDAO;
import info.bytecraft.api.PlayerReport;

public class BanCommand extends AbstractCommand
{

    public BanCommand(Bytecraft instance)
    {
        super(instance, "ban");
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
        if(!player.isAdmin()){
            return true;
        }
        if(args.length < 2){
            return true;
        }
        
        Player delegate = Bukkit.getPlayer(args[0]);
        if(delegate == null){
            return true;
        }
        
        String message = argsToMessage(args);
        BytecraftPlayer target = plugin.getPlayer(delegate);
        target.kickPlayer(ChatColor.RED + "You have been banned by " + player.getDisplayName());
        Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED + " has been banned by " + player.getDisplayName());
        
        try (IContext ctx = plugin.createContext()){
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setSubjectId(target.getId());
            report.setIssuerId(player.getId());
            report.setAction(PlayerReport.Action.BAN);
            report.setMessage(message);
            // three days default
            report.setValidUntil(new Date(
                    System.currentTimeMillis() + 3 * 86400 * 1000l));
            
            dao.insertReport(report);
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
        return true;
    }
    
}
