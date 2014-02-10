package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

public class ToolCommand extends AbstractCommand
{

    public ToolCommand(Bytecraft instance)
    {
        super(instance, "tool");
    }
    
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length == 0)return true;
        
        if("workbench".equalsIgnoreCase(args[0]) || "wb".equalsIgnoreCase(args[0])){
            this.workBench(player);
        }
        
        return true;
    }
    
    
    
    private void workBench(BytecraftPlayer player)
    {
        int cost = player.getToolCost();
        
        try(IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            if(dao.take(player, cost)){
                player.openWorkbench(null, true);
                player.sendMessage(ChatColor.GOLD + "" + cost + ChatColor.AQUA + " bytes have been taken from your wallet");
            }else{
                player.sendMessage(ChatColor.RED + "You can not afford that!");
            }
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }

}
