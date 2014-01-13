package info.bytecraft;

import info.bytecraft.api.*;
import info.bytecraft.commands.*;
import info.bytecraft.database.*;
import info.bytecraft.database.db.DBContextFactory;
import info.bytecraft.listener.*;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

public class Bytecraft extends JavaPlugin
{
    private HashMap<String, BytecraftPlayer> players;
    private static IContextFactory contextFactory;
    
    public void onLoad()
    {
        reloadConfig();
        
        FileConfiguration config = getConfig();
        
        contextFactory = new DBContextFactory(config);
    }

    public void onEnable()
    {
        players = Maps.newHashMap();
        for (Player delegate : Bukkit.getOnlinePlayers()) {
            try {
                addPlayer(delegate);
            } catch (PlayerBannedException e) {}
        }

        registerEvents();

        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("bless").setExecutor(new BlessCommand(this));
        getCommand("clear").setExecutor(new ClearCommand(this));
        getCommand("creative").setExecutor(
                new GameModeCommand(this, "creative"));
        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("fill").setExecutor(new FillCommand(this));
        getCommand("force").setExecutor(new ForceCommand(this));
        getCommand("gamemode").setExecutor(
                new GameModeCommand(this, "gamemode"));
        getCommand("give").setExecutor(new GiveCommand(this));
        getCommand("god").setExecutor(new SayCommand(this, "god"));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("inv").setExecutor(new InventoryCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("makewarp").setExecutor(new WarpCreateCommand(this));
        getCommand("me").setExecutor(new ActionCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this));
        getCommand("say").setExecutor(new SayCommand(this, "say"));
        getCommand("summon").setExecutor(new SummonCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("survival").setExecutor(
                new GameModeCommand(this, "survival"));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("tpblock").setExecutor(new TeleportBlockCommand(this));
        getCommand("teleport").setExecutor(new TeleportCommand(this));
        getCommand("tppos").setExecutor(new TeleportPosCommand(this));
        getCommand("user").setExecutor(new UserCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("wallet").setExecutor(new WalletCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("who").setExecutor(new WhoCommand(this));
        getCommand("zone").setExecutor(new ZoneCommand(this));
    }
    
    public static IContextFactory getContextFactory()
    {
        return contextFactory;
    }
    
    public static IContext createContext()
    throws DAOException
    {
        return contextFactory.createContext();
    }
    
    private IContext getContext()
    throws DAOException
    {
        return contextFactory.createContext();
    }

    private void registerEvents()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new BytecraftPlayerListener(this), this);
        pm.registerEvents(new BlessListener(this), this);        
        pm.registerEvents(new BytecraftBlockListener(this), this);
        pm.registerEvents(new FillListener(this), this);
        pm.registerEvents(new PlayerPromotionListener(this), this);
        pm.registerEvents(new SelectListener(this), this);
        pm.registerEvents(new ZoneListener(this), this);
    }
    
    // ========================================================
    // Player
    // ========================================================
    
    public void reloadPlayer(BytecraftPlayer player)
    {
        try{
            addPlayer(player.getDelegate());
        }catch(PlayerBannedException e){
            player.kickPlayer(e.getMessage());
        }
    }

    public BytecraftPlayer getPlayer(Player player)
    {
        return getPlayer(player.getName());
    }

    public BytecraftPlayer getPlayer(String name)
    {
        if (players.containsKey(name)) {
            return players.get(name);
        }
        else {
            try {
                return addPlayer(Bukkit.getPlayer(name));
            } catch (PlayerBannedException e) {}
        }
        return null;
    }
    
    public BytecraftPlayer getPlayerOffline(String name)
    {
        if(this.players.containsKey(name)){
            return players.get(name);
        }
        
        return null;
    }

    public BytecraftPlayer addPlayer(Player srcPlayer)
            throws PlayerBannedException
    {
        if (players.containsKey(srcPlayer.getName())) {
            return players.get(srcPlayer.getName());
        }
        
        try (IContext ctx = getContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            BytecraftPlayer player = dao.getPlayer(srcPlayer);
            
            player.setDisplayName(player.getRank().getColor() + player.getName() + ChatColor.WHITE);
            if(player.getName().length() + 4 > 16){
                player.setPlayerListName(player.getRank() + player.getName().substring(0, 12) + ChatColor.WHITE);
            }else{
                player.setPlayerListName(player.getRank() + player.getName() + ChatColor.WHITE);
            }
            
            players.put(player.getName(), player);
            return player;
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }

    public void removePlayer(BytecraftPlayer player)
    {
        this.players.remove(player.getName());
    }

    public List<BytecraftPlayer> getOnlinePlayers()
    {
        List<BytecraftPlayer> playersList = new ArrayList<BytecraftPlayer>();
        for (BytecraftPlayer player : players.values()) {
            playersList.add(player);
        }
        return playersList;
    }

    // ========================================================
    // Zones
    // ========================================================
    
    public List<Zone> getZones(String world)
    {
        return null;
    }
    
    public Zone getZone(String name)
    {
        return null;
    }

    public long getValue(Block block)
    {
        switch (block.getType()) {
        case STONE:
            return 1;
        case DIRT:
            return 1;
        case GRASS:
            return 1;
        case SAND:
            return 2;
        case IRON_ORE:
            return 30;
        case COAL_ORE:
            return 5;
        case LAPIS_ORE:
            return 5;
        case GOLD_ORE:
            return 50;
        case EMERALD_ORE:
            return 100;
        case DIAMOND_ORE:
            return 200;
        default:
            return 1;
        }
    }
    
}
