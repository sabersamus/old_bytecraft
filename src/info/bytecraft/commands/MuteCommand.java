package info.bytecraft.commands;

import static org.bukkit.ChatColor.RED;

import java.util.Date;
import java.util.List;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.PlayerReport;
import info.bytecraft.api.PlayerReport.ReportTime;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IReportDAO;

public class MuteCommand extends AbstractCommand
{

    public MuteCommand(Bytecraft instance)
    {
        super(instance, "mute");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.getRank().canMute()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        
        if(args.length == 0)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if(cantidates.size() != 1){
            return true;
        }
        
        BytecraftPlayer target = cantidates.get(0);
        
        if(args.length == 1){
            int i = 10;
            ReportTime time = ReportTime.MINUTES;
            player.sendMessage(RED + "You have muted " + target.getTemporaryChatName() + RED + " for " + 
                    i + " " + time.name().toLowerCase());
            target.sendMessage(RED + "You have been muted for " + i + " " + time.name().toLowerCase());
            target.setFlag(Flag.MUTE, true);
            try(IContext ctx = plugin.createContext()){
                IReportDAO dao = ctx.getReportDAO();
                PlayerReport report = new PlayerReport();
                report.setSubjectName(target.getName());
                report.setIssuerName(player.getName());
                report.setAction(PlayerReport.Action.MUTE);
                report.setMessage("Muted by " + player.getName());
                report.setTimestamp(new Date(System.currentTimeMillis()));
                report.setValidUntil(new Date(System.currentTimeMillis() + 
                        time.getTime() * i * 1000L));
                dao.insertReport(report);
            }catch(DAOException e){
                throw new RuntimeException(e);
            }
            return true;
        }else if (args.length == 3) {

            int i = 10;
            try {
                i = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                i = 10;
            }

            if(i <= 0){
                i = 10;
            }
            
            ReportTime time = ReportTime.byString(args[2]);
            if (time == null) {
                time = ReportTime.MINUTES;
            }

            player.sendMessage(RED + "You have muted " + target.getTemporaryChatName() + RED + " for " + 
                    i + " " + time.name().toLowerCase());
            target.sendMessage(RED + "You have been muted for " + i + " " + time.name().toLowerCase());
            target.setFlag(Flag.MUTE, true);
            try (IContext ctx = plugin.createContext()) {
                IReportDAO dao = ctx.getReportDAO();
                PlayerReport report = new PlayerReport();
                report.setSubjectName(target.getName());
                report.setIssuerName(player.getName());
                report.setAction(PlayerReport.Action.MUTE);
                report.setMessage("Muted by " + player.getName());
                report.setTimestamp(new Date(System.currentTimeMillis()));
                report.setValidUntil(new Date(System.currentTimeMillis()
                        + time.getTime() * i * 1000L));

                dao.insertReport(report);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
