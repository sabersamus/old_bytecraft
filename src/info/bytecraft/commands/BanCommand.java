package info.bytecraft.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

public class BanCommand extends AbstractCommand
{

    public BanCommand(Bytecraft instance)
    {
        super(instance, "ban");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canBan()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        if(args.length != 1)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if(cantidates.size() != 1){
            return true;
        }
        
        BytecraftPlayer target = cantidates.get(0);
        target.kickPlayer(ChatColor.RED + "You have been banned by "
                + player.getTemporaryChatName());
        Bukkit.broadcastMessage(target.getTemporaryChatName() + ChatColor.RED
                + " has been banned by " + player.getTemporaryChatName());

        try (IContext ctx = plugin.createContext()) {
            IPlayerDAO dao = ctx.getPlayerDAO();
            dao.ban(target);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        if(args.length != 1)return true;
        
        List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
        if(cantidates.size() != 1){
            return true;
        }
        
        BytecraftPlayer target = cantidates.get(0);

        target.kickPlayer(ChatColor.RED + "You have been banned by "
                + ChatColor.BLUE + "GOD");
        Bukkit.broadcastMessage(target.getTemporaryChatName() + ChatColor.RED
                + " has been banned by " + ChatColor.BLUE + "GOD");
        try (IContext ctx = plugin.createContext()) {
            IPlayerDAO dao = ctx.getPlayerDAO();
            dao.ban(target);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        
        return true;
    }
    
}
