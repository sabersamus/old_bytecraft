package info.bytecraft.commands;

import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PlayerReport.ReportTime;
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
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isAdmin()){
            return true;
        }
        if(args.length < 1)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if(cantidates.size() != 1){
            return true;
        }
        
        BytecraftPlayer target = cantidates.get(0);
        
        if(args.length == 1){
            int i = 3;
            ReportTime time = ReportTime.DAYS;
            target.kickPlayer(ChatColor.RED + "You have been banned by " + player.getDisplayName());
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED
                    + " has been banned by " + player.getDisplayName());
            
            try(IContext ctx = plugin.createContext()){
                IReportDAO dao = ctx.getReportDAO();
                PlayerReport report = new PlayerReport();
                report.setSubjectName(target.getName());
                report.setIssuerName(player.getName());
                report.setAction(PlayerReport.Action.BAN);
                report.setMessage("Banned by " + player.getName());
                report.setTimestamp(new Date(System.currentTimeMillis()));
                report.setValidUntil(new Date(System.currentTimeMillis() + 
                        time.getTime() * i * 1000L));
                dao.insertReport(report);
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
            return true;
        }
        
        if(args.length == 3){
            
            int i = 3;
            try{
                i = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                i = 3;
            }
            
            ReportTime time = ReportTime.byString(args[2]);
            if(time == null){
                time = ReportTime.DAYS;
            }
            
            target.kickPlayer(ChatColor.RED + "You have been banned by " + player.getDisplayName());
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED + " has been banned by " + player.getDisplayName());
            
            try (IContext ctx = plugin.createContext()){
                IReportDAO dao = ctx.getReportDAO();
                PlayerReport report = new PlayerReport();
                report.setSubjectName(target.getName());
                report.setIssuerName(player.getName());
                report.setAction(PlayerReport.Action.BAN);
                report.setMessage("Banned by " + player.getName());
                report.setTimestamp(new Date(System.currentTimeMillis()));
                report.setValidUntil(new Date(
                        System.currentTimeMillis() + time.getTime() * i * 1000L));
                
                dao.insertReport(report);
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        if(args.length < 1)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if(cantidates.size() != 1){
            return true;
        }
        
        BytecraftPlayer target = cantidates.get(0);
        
        if(args.length == 1){
            int i = 3;
            ReportTime time = ReportTime.DAYS;
            target.kickPlayer(ChatColor.RED + "You have been banned by " + ChatColor.BLUE + "GOD");
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED
                    + " has been banned by " + ChatColor.BLUE + "GOD");
            
            try(IContext ctx = plugin.createContext()){
                IReportDAO dao = ctx.getReportDAO();
                PlayerReport report = new PlayerReport();
                report.setSubjectName(target.getName());
                report.setIssuerName("CONSOLE");
                report.setAction(PlayerReport.Action.BAN);
                report.setMessage("Banned by CONSOLE");
                report.setTimestamp(new Date(System.currentTimeMillis()));
                report.setValidUntil(new Date(System.currentTimeMillis() + 
                        time.getTime() * i * 1000L));
                dao.insertReport(report);
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
            return true;
        }
        
        if(args.length == 3){
            int i = 3;
            try{
                i = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                i = 3;
            }
            
            ReportTime time = ReportTime.byString(args[2]);
            System.out.println(args[2]);
            if(time == null){
                time = ReportTime.DAYS;
            }
            System.out.println(time.getTime());
            
            target.kickPlayer(ChatColor.RED + "You have been banned by " + ChatColor.BLUE + "GOD");
            Bukkit.broadcastMessage(target.getDisplayName() + ChatColor.RED + 
                    " has been banned by " + ChatColor.BLUE + "GOD");
            
            try (IContext ctx = plugin.createContext()){
                IReportDAO dao = ctx.getReportDAO();
                PlayerReport report = new PlayerReport();
                report.setSubjectName(target.getName());
                report.setIssuerName("CONSOLE");
                report.setAction(PlayerReport.Action.BAN);
                report.setMessage("Banned by CONSOLE");
                report.setTimestamp(new Date(System.currentTimeMillis()));
                report.setValidUntil(new Date(
                        System.currentTimeMillis() + time.getTime() * i * 1000L));
                
                dao.insertReport(report);
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }
    
}
