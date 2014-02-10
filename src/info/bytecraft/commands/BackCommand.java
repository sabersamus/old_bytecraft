package info.bytecraft.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

public class BackCommand extends AbstractCommand implements Listener
{

    public BackCommand(Bytecraft instance)
    {
        super(instance, "back");
        deathLocation = new HashMap<>();
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }
    
    private class BackTask extends BukkitRunnable{
        private BytecraftPlayer player;
        private Location loc;
        
        public BackTask(BytecraftPlayer player, Location loc)
        {
            this.player = player;
            this.loc = loc;
        }
        
        
        @Override
        public void run()
        {
            player.teleport(loc);
            deathLocation.remove(player);
            player.sendMessage(ChatColor.GOLD + "" + player.getBackCost() + 
                    ChatColor.AQUA + " bytes has been taken from you wallet");
        }
        
    }
    
    private Map<BytecraftPlayer, Location> deathLocation;
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length != 0){
            return true;
        }
        
        if(!deathLocation.containsKey(player)){
            player.sendMessage(ChatColor.RED + "You have not died.");
            return true;
        }
        
        int cost = player.getBackCost();
        
        try(IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            if(dao.take(player, cost)){
                Location loc = deathLocation.get(player);
                player.sendMessage(ChatColor.AQUA + "Returning to where you died...");
                
                World world = loc.getWorld();
                Chunk chunk = world.getChunkAt(loc);
                
                world.loadChunk(chunk);
                
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BackTask(player, loc), 2 * 20L);
                return true;
            }else{
                String string = String.format("You dont have enough money to use that command! (%s)", 
                        ChatColor.GOLD + "" + cost + ChatColor.RED);
                player.sendMessage(ChatColor.RED + string);
            }
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
        
        return true;
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event){
        BytecraftPlayer player = plugin.getPlayer(event.getEntity());
        Location loc = player.getLocation();
        this.deathLocation.put(player, loc);
        
        int cost = player.getBackCost();
        
        player.sendMessage(ChatColor.YELLOW + 
                "You can use \"/back\" to get back to where you died! Cost: " 
                + ChatColor.GOLD + cost);
        return;
    }
    
}
