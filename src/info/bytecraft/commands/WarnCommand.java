package info.bytecraft.commands;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.PlayerReport.Action;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IReportDAO;

public class WarnCommand extends AbstractCommand
{

    public WarnCommand(Bytecraft instance)
    {
        super(instance, "warn");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isModerator())return true;
        if(args.length == 1){
            Player delegate = Bukkit.getPlayer(args[0]);
            if(delegate != null){
                BytecraftPlayer target = plugin.getPlayer(delegate);
                warnPlayer(player, target, false);
                player.sendMessage(ChatColor.RED + "You have warned " + target.getDisplayName());
            }
        }else if(args.length == 2){
            if("hard".equalsIgnoreCase(args[1])){
                Player delegate = Bukkit.getPlayer(args[0]);
                if(delegate != null){
                    BytecraftPlayer target = plugin.getPlayer(delegate);
                    warnPlayer(player, target, true);
                    player.sendMessage(ChatColor.RED + "You have hardwarned " + target.getDisplayName());
                }
            }
        }
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        if(args.length == 1){
            Player delegate = Bukkit.getPlayer(args[0]);
            if(delegate != null){
                BytecraftPlayer target = plugin.getPlayer(delegate);
                warnPlayer(null, target, false);
                plugin.getLogger().info(ChatColor.RED + "You have warned " + target.getDisplayName());
            }
        }else if(args.length == 2){
            if("hard".equalsIgnoreCase(args[1])){
                Player delegate = Bukkit.getPlayer(args[0]);
                if(delegate != null){
                    BytecraftPlayer target = plugin.getPlayer(delegate);
                    warnPlayer(null, target, true);
                    plugin.getLogger().info(ChatColor.RED + "You have hardwarned " + target.getDisplayName());
                }
            }
        }
        return true;
    }
    
    public void warnPlayer(BytecraftPlayer player, BytecraftPlayer victim, boolean hard)
    {
        Flag flag = hard? Flag.HARDWARNED: Flag.SOFTWARNED;
        try (IContext ctx = plugin.createContext()){
            IReportDAO dao = ctx.getReportDAO();
            PlayerReport report = new PlayerReport();
            report.setIssuerName(player == null ? "CONSOLE" : player.getName());
            report.setSubjectName(victim.getName());
            report.setAction(hard ? Action.HARDWARN: Action.SOFTWARN);
            report.setTimestamp(new Date(System.currentTimeMillis()));
            report.setValidUntil(new Date(System.currentTimeMillis() + 
                    7 * 86400 * 1000l));
            dao.insertReport(report);
            victim.sendMessage(ChatColor.RED + "You have been " + flag.name().toLowerCase() + " for one week.");
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
}
