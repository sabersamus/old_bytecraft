package info.bytecraft.commands;

import static org.bukkit.ChatColor.RED;

import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.PlayerReport.Action;
import info.bytecraft.api.PlayerReport.ReportTime;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IReportDAO;

public class WarnCommand extends AbstractCommand
{

    public WarnCommand(Bytecraft instance, String command)
    {
        super(instance, command);
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canWarn()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        if(args.length < 1)return true;
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);
        if(args.length == 1){
            if("warn".equalsIgnoreCase(command)){
                int i = 7;
                ReportTime time = ReportTime.DAYS;
                warnPlayer(player, target, time.getTime() * i);
                player.sendMessage(RED + "You have warned " + target.getTemporaryChatName() + RED + " for " + 
                        i + " " + time.name().toLowerCase());
                target.sendMessage(RED + "You have been warned for " + i + " " + time.name().toLowerCase());
                return true;
            }else if("hardwarn".equalsIgnoreCase(command)){
                int i = 7;
                ReportTime time = ReportTime.DAYS;
                hardWarnPlayer(player, target, time.getTime() * i);
                player.sendMessage(RED + "You have hardwarned " + target.getTemporaryChatName() + RED + " for " + 
                        i + " " + time.name().toLowerCase());
                
                target.sendMessage(RED + "You have been hardwarned for " + i + " " + time.name().toLowerCase());
                return true;
            }
        }
        if (args.length == 3) {
            if ("warn".equalsIgnoreCase(getCommand())) {
                int i = 7;
                try {
                    i = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    i = 7;
                }
                
                if(i <= 0){
                    i = 7;
                }

                ReportTime time = ReportTime.byString(args[2]);
                if (time == null) {
                    time = ReportTime.DAYS;
                }
                warnPlayer(player, target, time.getTime() * i);
                player.sendMessage(RED + "You have warned "
                        + target.getTemporaryChatName() + RED + " for " + i + " "
                        + time.name().toLowerCase());
                target.sendMessage(RED + "You have been warned for " + i + " " + time.name().toLowerCase());
            }
            else if ("hardwarn".equalsIgnoreCase(getCommand())) {
                int i = 7;
                try {
                    i = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    i = 7;
                }

                ReportTime time = ReportTime.byString(args[2]);
                if (time == null) {
                    time = ReportTime.DAYS;
                }
                hardWarnPlayer(player, target, time.getTime() * i);
                player.sendMessage(RED + "You have hardwarned "
                        + target.getTemporaryChatName() + RED + " for " + i + " "
                        + time.name().toLowerCase());
                target.sendMessage(RED + "You have been hardwarned for " + i + " " + time.name().toLowerCase());
            }
        }
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        if (args.length < 1)
            return true;
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if (cantidates.size() != 1) {
            return true;
        }

        BytecraftPlayer target = cantidates.get(0);
        if (args.length == 1) {
            if ("warn".equalsIgnoreCase(command)) {
                int i = 7;
                ReportTime time = ReportTime.DAYS;
                warnPlayer(null, target, time.getTime() * i);
                plugin.sendMessage(RED + "You have warned "
                        + target.getTemporaryChatName() + RED + " for " +  i + " "
                        + time.name().toLowerCase());
                target.sendMessage(RED + "You have been warned for " + i + " " + time.name().toLowerCase());
                target.setFlag(Flag.SOFTWARNED, true);
                return true;
            }
            else if ("hardwarn".equalsIgnoreCase(command)) {
                int i = 7;
                ReportTime time = ReportTime.DAYS;
                hardWarnPlayer(null, target, time.getTime() * i);
                plugin.sendMessage(RED + "You have hardwarned "  + target.getTemporaryChatName() + RED + " for " + i + " "
                        + time.name().toLowerCase());
                target.sendMessage(RED + "You have been hardwarned for " + i + " " + time.name().toLowerCase());
                return true;
            }
        }
        if (args.length == 3) {
            if ("warn".equalsIgnoreCase(getCommand())) {
                int i = 7;
                try {
                    i = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    i = 7;
                }

                if(i <= 0){
                    i = 7;
                }
                
                ReportTime time = ReportTime.byString(args[2]);
                if (time == null) {
                    time = ReportTime.DAYS;
                }
                warnPlayer(null, target, time.getTime() * i);
                plugin.getLogger().info(
                        ChatColor.RED + "You have warned "
                                + target.getTemporaryChatName() + RED + " for " + i + " " + time.name().toLowerCase());
                target.sendMessage(RED + "You have been warned for " + i + " " + time.name().toLowerCase());
            }
            else if ("hardwarn".equalsIgnoreCase(getCommand())) {
                int i = 7;
                try {
                    i = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    i = 7;
                }

                ReportTime time = ReportTime.byString(args[2]);
                if (time == null) {
                    time = ReportTime.DAYS;
                }
                hardWarnPlayer(null, target, time.getTime() * i);
                plugin.getLogger().info(
                        ChatColor.RED + "You have hardwarned "
                                + target.getTemporaryChatName() + RED + " for " + i + " " + time.name().toLowerCase());
                target.sendMessage(RED + "You have been hardwarned for " + i + " " + time.name().toLowerCase());
            }
        }
        return true;
    }
    
    public void warnPlayer(BytecraftPlayer player, BytecraftPlayer victim, long time)
    {
        try (IContext ctx = plugin.createContext()){
            String name = victim.getName();
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setIssuerName(player == null ? "CONSOLE" : player.getName());
            report.setSubjectName(name);
            report.setAction(Action.SOFTWARN);
            report.setMessage("Warned by " + report.getIssuerName());
            report.setTimestamp(new Date(System.currentTimeMillis()));
            report.setValidUntil(new Date(System.currentTimeMillis() + 
                    time * 1000L));
            dao.insertReport(report);
            name = ChatColor.GRAY + name + ChatColor.WHITE;
            victim.setDisplayName(name);
            victim.setFlag(Flag.SOFTWARNED, true);
            if(name.length() > 16){
                victim.setPlayerListName(name.substring(0, 15));
            }else{
                victim.setPlayerListName(name);
            }
            
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    public void hardWarnPlayer(BytecraftPlayer player, BytecraftPlayer victim, long time)
    {
        try (IContext ctx = plugin.createContext()){
            String name = victim.getName();
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setIssuerName(player == null ? "CONSOLE" : player.getName());
            report.setSubjectName(name);
            report.setAction(Action.HARDWARN);
            report.setMessage("Warned by " + report.getIssuerName());
            report.setTimestamp(new Date(System.currentTimeMillis()));
            report.setValidUntil(new Date(System.currentTimeMillis() + 
                    time * 1000L));
            dao.insertReport(report);
            name = ChatColor.GRAY + name + ChatColor.WHITE;
            victim.setDisplayName(name);
            victim.setFlag(Flag.HARDWARNED, true);
            if(name.length() > 16){
                victim.setPlayerListName(name.substring(0, 15));
            }else{
                victim.setPlayerListName(name);
            }
            
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
}
