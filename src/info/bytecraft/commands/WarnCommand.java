package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

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
                warnPlayer(target, false);
                player.sendMessage(ChatColor.RED + "You have warned " + target.getDisplayName());
            }
        }else if(args.length == 2){
            if("hard".equalsIgnoreCase(args[1])){
                Player delegate = Bukkit.getPlayer(args[0]);
                if(delegate != null){
                    BytecraftPlayer target = plugin.getPlayer(delegate);
                    warnPlayer(target, true);
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
                warnPlayer(target, false);
                plugin.getLogger().info(ChatColor.RED + "You have warned " + target.getDisplayName());
            }
        }else if(args.length == 2){
            if("hard".equalsIgnoreCase(args[1])){
                Player delegate = Bukkit.getPlayer(args[0]);
                if(delegate != null){
                    BytecraftPlayer target = plugin.getPlayer(delegate);
                    warnPlayer(target, true);
                    plugin.getLogger().info(ChatColor.RED + "You have hardwarned " + target.getDisplayName());
                }
            }
        }
        return true;
    }
    
    public void warnPlayer(BytecraftPlayer player, boolean hard)
    {
        Rank rank = hard ? Rank.HARD_WARNED : Rank.WARNED;
        player.setRank(rank);
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            dao.updatePermissions(player);
            player.sendMessage(ChatColor.RED + "You have been demoted to " + ChatColor.GRAY + rank.toString());
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
}
