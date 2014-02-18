package info.bytecraft.commands;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;
import info.bytecraft.tools.ToolRegistry;

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
        }else if("veinminer".equalsIgnoreCase(args[0]) || "vm".equalsIgnoreCase(args[0])){
            if(!player.getRank().canSpawnTools()){
                return true;
            }
            ItemStack tool = ToolRegistry.getVeinMinerCoupon();
            
            HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(tool);
            
            if (failedItems.size() > 0) {
                player.sendMessage(ChatColor.RED + "You have a full inventory, Can't add tool!");
                return true;
            }
            
            player.sendMessage(ChatColor.GREEN + "Spawned in tool token successfully!");
            return true;
            
        }
        
        return true;
    }
    
    
    
    private void workBench(BytecraftPlayer player)
    {
        int cost = player.getRank().getToolCost();
        
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
