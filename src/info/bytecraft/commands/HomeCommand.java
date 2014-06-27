package info.bytecraft.commands;

import java.util.List;

import org.bukkit.*;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IHomeDAO;

public class HomeCommand extends AbstractCommand
{

    public HomeCommand(Bytecraft instance)
    {
        super(instance, "home");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.getRank().canSaveHomes()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        
        //home
        //home go [name]
        //home save [name]
        //home to <player> [name]

        if(args.length == 0){
            this.teleportHome(player, "default");
            return true;
        }
        
        if(args.length == 1){
            if("save".equalsIgnoreCase(args[0])){
                this.setHome(player, "default");
                return true;
            }
        }
        
        if(args.length == 2){
            if("save".equalsIgnoreCase(args[0])){
                this.setHome(player, args[1]);
                return true;
            }
            
            if("go".equalsIgnoreCase(args[0])){
                this.teleportHome(player, args[1]);
                return true;
            }
            
            if("delete".equalsIgnoreCase(args[0])){
                this.deleteHome(player, args[1]);
                return true;
            }
            
            if("to".equalsIgnoreCase(args[0]) && player.getRank().canGoToPlayersHomes()){
                BytecraftPlayer offline = plugin.getPlayerOffline(args[1]);
                if(offline != null){
                    this.homeTo(player, offline.getName(), "default");
                    return true;
                }
            }
        }
        
        if(args.length == 3){
            if("to".equalsIgnoreCase(args[0]) && player.getRank().canGoToPlayersHomes()){
                BytecraftPlayer offline = plugin.getPlayerOffline(args[1]);
                if(offline != null){
                    this.homeTo(player, offline.getName(), args[2]);
                    return true;
                }
            }
        }
        
        
        
        return true;
    }

    private void setHome(BytecraftPlayer player, String name)
    {
        try (IContext ctx = plugin.createContext()) {
            IHomeDAO dao = ctx.getHomeDAO();
            
            
            List<String> homes = dao.getHomeNames(player);
            int limit = player.getRank().getMaxHomes();
            if(homes.size() > limit){
                player.sendMessage(ChatColor.RED + "You can't have more than " + limit + " homes!");
                return;
            }
            dao.setHome(player, name);
            player.sendMessage(ChatColor.AQUA + "Home saved!");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean teleportHome(final BytecraftPlayer player, String name)
    {
        
        Location loc = null;
        try (IContext ctx = plugin.createContext()) {
            IHomeDAO dao = ctx.getHomeDAO();

            loc = dao.getHome(player, name);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        
        if(loc == null){
            player.sendMessage(ChatColor.RED + "Teleport failed! Unknown location!");
            return true;
        }
        
        final Location newLoc = loc;
            
        player.sendMessage(ChatColor.AQUA + "Initiating teleport to home!");    
        
        World world = loc.getWorld();
        Chunk chunk = world.getChunkAt(loc);
        world.loadChunk(chunk);
        
        if(chunk.isLoaded()){
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                public void run(){
                            player.teleportWithHorse(newLoc);
                }
            }, 3 * 20L);
            
            return true;
        }else{
            player.sendMessage(ChatColor.RED + "Chunk load failed, please try again!");
            return true;
        }
        
    }

    private boolean homeTo(final BytecraftPlayer player, final String toName, String homeName)
    {
        try (IContext ctx = plugin.createContext()) {
            IHomeDAO dao = ctx.getHomeDAO();
            if (dao.getHome(plugin.getPlayer(toName), homeName) == null) return false;
            player.sendMessage(ChatColor.AQUA + "Initiating teleport to "
                    + toName + "'s home!");
            final Location loc = dao.getHome(plugin.getPlayer(toName), homeName);
            
            World world = loc.getWorld();
            Chunk chunk = world.getChunkAt(loc);
            
            world.loadChunk(chunk);
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                    new Runnable() {

                        public void run()
                        {
                            player.teleportWithHorse(loc);
                        }

                    }, 20 * 3L);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    public void deleteHome(BytecraftPlayer player, String name)
    {
        try(IContext ctx = plugin.createContext()){
            IHomeDAO dao = ctx.getHomeDAO();
            
            if(dao.getHomeNames(player).contains(name)){
                dao.deleteHome(player, name);
                player.sendMessage(ChatColor.RED + "Deleted home " + name);
                return;
            }else{
                player.sendMessage(ChatColor.RED + "You dont have a home named that!");
                return;
            }
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }

}
