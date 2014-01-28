package info.bytecraft.commands;

import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.PlayerReport.Action;
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
        if(args.length != 1)return true;
        if(!player.isModerator())return true;
        if("warn".equalsIgnoreCase(getCommand())){
            List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
            if(cantidates.size() != 1){
                return true;
            }
            
            BytecraftPlayer target = cantidates.get(0);
                warnPlayer(null, target);
                player.sendMessage(ChatColor.RED + "You have warned " + target.getDisplayName());
        }else if("hardwarn".equalsIgnoreCase(getCommand())){
            List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
            if(cantidates.size() != 1){
                return true;
            }
            
            BytecraftPlayer target = cantidates.get(0);
                    hardWarnPlayer(null, target);
                    player.sendMessage(ChatColor.RED + "You have hardwarned " + target.getDisplayName());
        }
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        if(args.length != 1)return true;
        if("warn".equalsIgnoreCase(getCommand())){
            List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
            if(cantidates.size() != 1){
                return true;
            }
            
            BytecraftPlayer target = cantidates.get(0);
                warnPlayer(null, target);
                plugin.getLogger().info(ChatColor.RED + "You have warned " + target.getDisplayName());
        }else if("hardwarn".equalsIgnoreCase(getCommand())){
            List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
            if(cantidates.size() != 1){
                return true;
            }
            
            BytecraftPlayer target = cantidates.get(0);
                    hardWarnPlayer(null, target);
                    plugin.getLogger().info(ChatColor.RED + "You have hardwarned " + target.getDisplayName());
        }
        return true;
    }
    
    public void warnPlayer(BytecraftPlayer player, BytecraftPlayer victim)
    {
        try (IContext ctx = plugin.createContext()){
            String name = victim.getName();
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setIssuerName(player == null ? "CONSOLE" : player.getName());
            report.setSubjectName(name);
            report.setAction(Action.SOFTWARN);
            report.setTimestamp(new Date(System.currentTimeMillis()));
            report.setValidUntil(new Date(System.currentTimeMillis() + 
                    7 * 86400 * 1000l));
            dao.insertReport(report);
            victim.sendMessage(ChatColor.RED + "You have been warned for one week.");
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
    
    public void hardWarnPlayer(BytecraftPlayer player, BytecraftPlayer victim)
    {
        try (IContext ctx = plugin.createContext()){
            String name = victim.getName();
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setIssuerName(player == null ? "CONSOLE" : player.getName());
            report.setSubjectName(name);
            report.setAction(Action.HARDWARN);
            report.setTimestamp(new Date(System.currentTimeMillis()));
            report.setValidUntil(new Date(System.currentTimeMillis() + 
                    7 * 86400 * 1000l));
            dao.insertReport(report);
            victim.sendMessage(ChatColor.RED + "You have been hardwarned for one week.");
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
