package info.bytecraft;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maxmind.geoip.LookupService;

import info.bytecraft.api.*;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.event.CallEventListener;
import info.bytecraft.commands.*;
import info.bytecraft.database.*;
import info.bytecraft.database.db.DBContextFactory;
import info.bytecraft.listener.*;
import info.bytecraft.tools.ToolRegistry;
import info.bytecraft.zones.Lot;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.ZoneWorld;

import info.tregmine.quadtree.IntersectionException;

public class Bytecraft extends JavaPlugin
{
    private HashMap<String, BytecraftPlayer> players;
    private IContextFactory contextFactory;
    
    private List<String> deathMessages;
    private List<String> quitMessages;
    //private List<String> swordNames;
    //private Map<String, List<String>> armorNames;
    
    private Map<Location, String> blessedBlocks;
    
    private LookupService lookup = null;
    
    private Map<String, ZoneWorld> worlds;
    private Map<String, Zone> zones;
    private Map<Location, SaleSign> saleSigns;
    
    private World rolePlayWorld;
    
    public void onLoad()
    {
        File folder = getDataFolder();
        reloadConfig();
        
        FileConfiguration config = getConfig();
        contextFactory = new DBContextFactory(config, this);
        
        worlds = new TreeMap<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String a, String b)
                    {
                        return a.compareToIgnoreCase(b);
                    }
                });
        
        zones = new HashMap<>();
        
        try{
            lookup = new LookupService(new File(folder, "GeoIPCity.dat"), 
                    LookupService.GEOIP_MEMORY_CACHE);
        }catch(IOException e){
            getLogger().warning("Could not find GeoIPCity.dat, is it in the correct folder?");
        }
    }

    public void onEnable()
    {
        players = Maps.newHashMap();
        for (Player delegate : Bukkit.getOnlinePlayers()) {
            try {
                addPlayer(delegate, delegate.getAddress().getAddress());
            } catch (PlayerBannedException e) {}
        }
        
        try(IContext ctx = createContext()){
            IBlessDAO bless = ctx.getBlessDAO();
            this.blessedBlocks = bless.getBlessedBlocks();
            
            ISaleSignDAO saleDao = ctx.getSaleSignDAO();
            this.saleSigns = saleDao.loadSaleSigns();
            
            IMessageDAO messages = ctx.getMessageDAO();
            this.deathMessages = messages.loadDeathMessages();
            this.quitMessages = messages.loadQuitMessages();
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
        
        ToolRegistry.registerRecipes(getServer());
        
        registerEvents();

        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("bless").setExecutor(new BlessCommand(this));
        getCommand("brush").setExecutor(new BrushCommand(this));
        getCommand("clear").setExecutor(new ClearCommand(this));
        getCommand("cmob").setExecutor(new CreateMobCommand(this));
        getCommand("cname").setExecutor(new ChangeNameCommand(this));
        getCommand("creative").setExecutor(new GameModeCommand(this, "creative"));
        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("chestlog").setExecutor(new ChestLogCommand(this));
        getCommand("fill").setExecutor(new FillCommand(this, "fill"));
        getCommand("flag").setExecutor(new FlagCommand(this));
        getCommand("force").setExecutor(new ForceCommand(this));
        getCommand("gamemode").setExecutor(new GameModeCommand(this, "gamemode"));
        getCommand("give").setExecutor(new GiveCommand(this));
        getCommand("god").setExecutor(new SayCommand(this, "god"));
        getCommand("hardwarn").setExecutor(new WarnCommand(this, "hardwarn"));
        getCommand("head").setExecutor(new HeadCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("inv").setExecutor(new InventoryCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("lot").setExecutor(new LotCommand(this));
        getCommand("makewarp").setExecutor(new WarpCreateCommand(this));
        getCommand("me").setExecutor(new ActionCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this, "message"));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("newspawn").setExecutor(new NewSpawnCommand(this));
        getCommand("nuke").setExecutor(new NukeCommand(this));
        getCommand("pos").setExecutor(new PositionCommand(this));
        getCommand("reply").setExecutor(new MessageCommand(this, "reply"));
        getCommand("ride").setExecutor(new RideCommand(this, "ride"));
        getCommand("rideme").setExecutor(new RideCommand(this, "rideme"));
        getCommand("say").setExecutor(new SayCommand(this, "say"));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("summon").setExecutor(new SummonCommand(this));
        getCommand("smite").setExecutor(new SmiteCommand(this));
        //getCommand("support").setExecutor(new SupportCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("survival").setExecutor(new GameModeCommand(this, "survival"));
        getCommand("testfill").setExecutor(new FillCommand(this, "testfill"));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("tool").setExecutor(new ToolCommand(this));
        getCommand("town").setExecutor(new TownCommand(this));
        getCommand("tpblock").setExecutor(new TeleportBlockCommand(this));
        getCommand("teleport").setExecutor(new TeleportCommand(this));
        getCommand("tppos").setExecutor(new TeleportPosCommand(this));
        getCommand("user").setExecutor(new UserCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("wallet").setExecutor(new WalletCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this, "warn"));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("who").setExecutor(new WhoCommand(this));
        getCommand("zone").setExecutor(new ZoneCommand(this));
        
        WorldCreator albion = new WorldCreator("albion");
        albion.environment(Environment.NORMAL);
        albion.type(WorldType.AMPLIFIED);
        this.rolePlayWorld = albion.createWorld();
    }
    
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        
        for(BytecraftPlayer player: getOnlinePlayers()){
            player.saveInventory(player.getCurrentInventory());
            this.removePlayer(player);
        }
    }
    
    public IContextFactory getContextFactory()
    {
        return contextFactory;
    }
    
    public IContext createContext()
    throws DAOException
    {
        return contextFactory.createContext();
    }

    private void registerEvents()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlessListener(this), this);
        pm.registerEvents(new BookShelfListener(this), this);
        pm.registerEvents(new ButtonListener(this), this);
        pm.registerEvents(new BytecraftPlayerListener(this), this);
        pm.registerEvents(new BytecraftBlockListener(this), this);
        pm.registerEvents(new CallEventListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new ElevatorListener(this), this);
        pm.registerEvents(new FillListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new ItemFrameListener(this), this);
        pm.registerEvents(new PlayerLookupListener(this), this);
        pm.registerEvents(new PlayerPromotionListener(this), this);
        pm.registerEvents(new SaleSignListener(this), this);
        pm.registerEvents(new SelectListener(this), this);
        pm.registerEvents(new SignColorListener(), this);
        pm.registerEvents(new ToolListener(), this);
        pm.registerEvents(new VeinListener(this), this);
        pm.registerEvents(new WorldPortalListener(this), this);
        pm.registerEvents(new ZoneListener(this), this);
        
        //rare drop
        //pm.registerEvents(new RareDropListener(this), this);
        pm.registerEvents(new DamageListener(this), this);
        
        //rpg
        //pm.registerEvents(new PlayerListener(this), this);
        
    }
    
    // ========================================================
    // ================== Player Methods ======================
    // ========================================================
    
    public void refreshPlayer(BytecraftPlayer player)
    {
        try(IContext ctx = createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            dao.updatePlayTime(player);
            dao.loadFlags(player);
            player.setRank(dao.getRank(player));
            ChatColor color = player.getRank().getColor();
            
            String name = color + player.getName();
            player.setDisplayName(name + ChatColor.WHITE);
            if(name.length() > 16){
                player.setPlayerListName(name.substring(0, 15));
            }else{
                player.setPlayerListName(name);
            }
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    public void reloadPlayer(BytecraftPlayer player)
    {
        try{
            addPlayer(player.getDelegate(), player.getAddress().getAddress());
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
        return players.get(name);
    }
    
    public BytecraftPlayer getPlayerOffline(String name)
    {
        if(players.containsKey(name)){
            return players.get(name);
        }
        
        try(IContext ctx = createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return dao.getPlayerOffline(name);
        }catch(DAOException ex){
            throw new RuntimeException(ex);
        }
    }
    
    //this method should be okay
    public BytecraftPlayer addPlayer(Player srcPlayer,  InetAddress addr)
            throws PlayerBannedException
    {
        if (players.containsKey(srcPlayer.getName())) {
            return players.get(srcPlayer.getName());
        }
        
        try (IContext ctx = createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            //as long as this one is changed
            BytecraftPlayer player = dao.getPlayer(srcPlayer);
            
            if(player == null){
                //and this one
                player = dao.createPlayer(srcPlayer);
            }
            
            //and this one
            if(dao.isBanned(player)){
                throw new PlayerBannedException(ChatColor.RED + "You are not allowed on this server");
            }
            
            player.setFlag(Flag.HARDWARNED, false);
            player.setFlag(Flag.SOFTWARNED, false);
            player.setFlag(Flag.MUTE, false);
            
            IReportDAO reportDao = ctx.getReportDAO();
            //and this one
            List<PlayerReport> reports = reportDao.getReports(player);
            for(PlayerReport report: reports){
                Date validUntil = report.getValidUntil();
                if (validUntil == null) {
                    continue;
                }
                if (validUntil.getTime() < System.currentTimeMillis()) {
                    continue;
                }
                
                if (report.getAction() == PlayerReport.Action.SOFTWARN) {
                    player.setFlag(Flag.SOFTWARNED, true);
                }
                else if (report.getAction() == PlayerReport.Action.HARDWARN) {
                    player.setFlag(Flag.HARDWARNED, true);
                }else if(report.getAction() == PlayerReport.Action.MUTE){
                    player.setFlag(Flag.MUTE, true);
                }
            }
            
            ChatColor color = player.getRank().getColor();
            
            if(player.hasFlag(Flag.HARDWARNED) || player.hasFlag(Flag.SOFTWARNED)){
                color = ChatColor.GRAY;
            }
            
            player.setDisplayName(color + player.getName() + ChatColor.WHITE);
            String name = color + player.getName();
            
            if(name.length() > 16){
                player.setPlayerListName(name.substring(0, 15));
            }else{
                player.setPlayerListName(name);
            }
            
            player.setTemporaryChatName(name);
            player.setIp(addr.getHostAddress());
            player.setHost(addr.getCanonicalHostName());
            
            if(lookup != null){
                com.maxmind.geoip.Location loc = lookup.getLocation(player.getIp());
                if(loc != null){
                    player.setCountry(loc.countryName);
                    player.setCity(loc.city);
                }
            }
            
            
            
            ILogDAO logs = ctx.getLogDAO();
            logs.insertLogin(player, "login");
            
            players.put(player.getName(), player);
            
            return player;
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }

    public void removePlayer(BytecraftPlayer player)
    {
        try(IContext ctx = createContext()){
            ctx.getPlayerDAO().updatePlayTime(player);
            ctx.getLogDAO().insertLogin(player, "logout");
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
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
    
    public List<BytecraftPlayer> matchPlayer(String name)
    {
        List<BytecraftPlayer> players = Lists.newArrayList();
        for(BytecraftPlayer player: getOnlinePlayers()){
            String playerName = player.getName().toLowerCase();
            if(playerName.startsWith(name.toLowerCase())){
                players.add(player);
            }
        }
        return players;
    }
    
    // ========================================================
    // ======================= Zones ==========================
    // ========================================================
    public ZoneWorld getWorld(World world)
    {
        ZoneWorld zoneWorld = worlds.get(world.getName());

        // lazy load zone worlds as required
        if (zoneWorld == null) {
            try (IContext ctx = contextFactory.createContext()) {
                IZoneDAO dao = ctx.getZoneDAO();

                zoneWorld = new ZoneWorld(world);
                List<Zone> zones = dao.getZones(world.getName());
                for (Zone zone : zones) {
                    try {
                        zoneWorld.addZone(zone);
                        this.zones.put(zone.getName(), zone);
                    } catch (IntersectionException e) {
                        getLogger().warning("Failed to load zone " + zone.getName()
                                + " with id " + zone.getId() + ".");
                    }
                    
                }

                List<Lot> lots = dao.getLots(world.getName());
                for (Lot lot : lots) {
                    try {
                        zoneWorld.addLot(lot);
                    } catch (IntersectionException e) {
                        getLogger().warning("Failed to load lot " + lot.getName()
                                + " with id " + lot.getId() + ".");
                    }
                }

                worlds.put(world.getName(), zoneWorld);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
        }

        return zoneWorld;
    }
    
    public Zone getZone(String name)
    {
        return zones.get(name);
    }
    
    // ========================================================
    // ===================== Bless ============================
    // ========================================================
    
    public Map<Location, String> getBlessedBlocks()
    {
        return blessedBlocks;
    }
    
    public boolean isBlessed(Location loc)
    {
        return this.blessedBlocks.containsKey(loc);
    }
    
    public BytecraftPlayer getOwner(Location loc)
    {
        if(!isBlessed(loc)){
            return null;
        }
        
        return getPlayerOffline(this.blessedBlocks.get(loc));
    }
    
    public boolean blessBlock(Location loc, BytecraftPlayer owner)
    {
        if(isBlessed(loc)){
            return false;
        }
        
        this.blessedBlocks.put(loc, owner.getName());
        
        try(IContext ctx = createContext()){
            IBlessDAO dao = ctx.getBlessDAO();
            
            dao.bless(loc.getBlock(), owner);
            return true;
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    public Map<Location, SaleSign> getSaleSigns()
    {
        return saleSigns;
    }
    

    // ========================================================
    // ===================== Other ============================
    // ========================================================
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
        case OBSIDIAN:
            return 50;
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
    
    public World getRolePlayWorld()
    {
        return this.rolePlayWorld;
    }
    
    public Location getWorldSpawn(String name)
    {
        String loc = this.getConfig().getString("spawn." + name);
        
        if(loc == null || loc.equalsIgnoreCase("")){
            return Bukkit.getWorld(name).getSpawnLocation();
        }
        
        String[] args = loc.split(", ");
        
        Location location = new Location(Bukkit.getWorld(name), parseDouble(args[0]), parseDouble(args[1]), 
                parseDouble(args[2]), parseFloat(args[3]), parseFloat(args[4]));
        
        return location;
    }
    
    private double parseDouble(String s)
    {
        try{
            return Double.parseDouble(s);
        }catch(NumberFormatException e){
            return 0.0;
        }
    }
    
    private float parseFloat(String s)
    {
        try{
            return Float.parseFloat(s);
        }catch(NumberFormatException e){
            return 0.0F;
        }
    }

    public void sendMessage(String string)
    {
        Bukkit.getConsoleSender().sendMessage(string);
    }
    
    public List<String> getQuitMessages()
    {
        return this.quitMessages;
    }
    
    public List<String> getDeathMessages()
    {
        return this.deathMessages;
    }

    /*public List<String> getSwordNames()
    {
        return swordNames;
    }
    
    public List<String> getArmorNames(String type)
    {
        return this.armorNames.get(type);
    }*/
    
    public static int locationChecksum(Location loc)
    {
        int checksum = (loc.getBlockX() + "," +
                loc.getBlockZ() + "," +
                loc.getBlockY() + "," +
                loc.getWorld().getName()).hashCode();
        return checksum;
    }
}
