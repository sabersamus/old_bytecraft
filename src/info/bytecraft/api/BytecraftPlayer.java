package info.bytecraft.api;

import info.bytecraft.Bytecraft;
import info.bytecraft.zones.Lot;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.Zone.Permission;
import info.bytecraft.zones.ZoneWorld;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

import java.text.NumberFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BytecraftPlayer extends PlayerDelegate
{
    
    public static enum Flag{
        HARDWARNED,
        HIDDEN_LOCATION,
        SILENT_JOIN,
        SOFTWARNED,
        MUTE,
        INVISIBLE,
        TPBLOCK,
        CAN_FLY,
        CHEST_LOG,
        IMMORTAL;
    }
    
    public static enum ChatState{
        CHAT,
        SELL,
        SALESIGN_BUY,
        SALESIGN_WITHDRAW,
        SALESIGN_SETUP;
    }

    private Bytecraft plugin;
    private String name;
    private int id = 0;
    private Rank rank;

    private String chatChannel = "GLOBAL";
    private ChatState chatState = ChatState.CHAT;
    
    private Block fillBlock1;
    private Block fillBlock2;
    private Block zoneBlock1;
    private Block zoneBlock2;
    private Block lotBlock1;
    private Block lotBlock2;
    
    private String ip;
    private String host;
    private String city;
    private String country;
    
    private Set<Flag> flags;
    
    private BytecraftPlayer blessTarget;
    private BytecraftPlayer lastMessager;
    
    private SaleSign currentSaleSign;
    private SaleSign newSaleSign;
    private int saleBuyCount;
    
    private HashMap<Badge, Integer> badges;
    
    private Date loginTime;

    public BytecraftPlayer(Player player, Bytecraft plugin)
    {
        super(player);
        this.setName(player.getName());
        loginTime = new Date();
        flags = EnumSet.noneOf(Flag.class);
        this.plugin = plugin;
        badges = new HashMap<>();
    }


    public BytecraftPlayer(String name, Bytecraft plugin)
    {
        super(null);
        this.setName(name);
        loginTime = new Date();
        flags = EnumSet.noneOf(Flag.class);
        this.plugin = plugin;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ChatColor getNameColor()
    {
        try(IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            ChatColor color = dao.getRank(this).getColor();
            
            if(hasFlag(Flag.SOFTWARNED) || hasFlag(Flag.HARDWARNED)){
                color = ChatColor.GRAY;
            }
            
            return color;
            
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }


    //Bytecraft stored values
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
    
    public Rank getRank() { return this.rank; }
    
    public void setRank(Rank rank) { this.rank = rank; }
    
    public long getBalance()
    {
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return dao.getBalance(this);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getFormattedBalance()
    {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return ChatColor.GOLD + nf.format(getBalance()) + ChatColor.AQUA
                + " bytes";
    }
    
    public ChatColor getGodColor(){
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return dao.getGodColor(this);
        }catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    //Non-Persistent
    public String getChatChannel() { return chatChannel.toUpperCase(); }

    public void setChatChannel(String chatChannel) { this.chatChannel = chatChannel; }
    
    public ChatState getChatState() { return chatState; }

    public void setChatState(ChatState chatState) { this.chatState = chatState; }
    
    public BytecraftPlayer getBlessTarget() { return blessTarget; }

    public void setBlessTarget(BytecraftPlayer blessTarget) { this.blessTarget = blessTarget; }
    
    public BytecraftPlayer getLastMessager() { return lastMessager; }
    
    public void setLastMessager(BytecraftPlayer player) { this.lastMessager = player; }
    
    public void setIp(String v) { this.ip = v; }
    public String getIp() { return ip; }

    public void setHost(String v) { this.host = v; }
    public String getHost() { return host; }

    public void setCity(String v) { this.city = v; }
    public String getCity() { return city; }

    public void setCountry(String v) { this.country = v; }
    public String getCountry() { return country; }

    public void sendNotification(Notification notif, String message)
    {
        if(message != null && !message.equalsIgnoreCase("")){
            sendMessage(message);
        }
        this.playSound(getLocation(), notif.getSound(), 2F, 1F);
    }

    //Target blocks
    public Block getFillBlock1() { return fillBlock1; }

    public void setFillBlock1(Block fillBlock1) { this.fillBlock1 = fillBlock1; }

    public Block getFillBlock2() { return fillBlock2; }

    public void setFillBlock2(Block fillBlock2) { this.fillBlock2 = fillBlock2; }

    public Block getZoneBlock1() { return zoneBlock1; }

    public void setZoneBlock1(Block zoneBlock1) { this.zoneBlock1 = zoneBlock1; }

    public Block getZoneBlock2() { return zoneBlock2; }

    public void setZoneBlock2(Block zoneBlock2) { this.zoneBlock2 = zoneBlock2; }

    public Block getLotBlock1() { return lotBlock1; }

    public void setLotBlock1(Block lotBlock1) { this.lotBlock1 = lotBlock1; }

    public Block getLotBlock2() { return lotBlock2; }

    public void setLotBlock2(Block lotBlock2) { this.lotBlock2 = lotBlock2; }

    public SaleSign getCurrentSaleSign()
    {
        return currentSaleSign;
    }


    public void setCurrentSaleSign(SaleSign currentSaleSign)
    {
        this.currentSaleSign = currentSaleSign;
    }


    public SaleSign getNewSaleSign()
    {
        return newSaleSign;
    }


    public void setNewSaleSign(SaleSign newSaleSign)
    {
        this.newSaleSign = newSaleSign;
    }


    public int getSaleBuyCount()
    {
        return saleBuyCount;
    }


    public void setSaleBuyCount(int count)
    {
        this.saleBuyCount = count;
    }


    //Flags
    public boolean hasFlag(Flag flag)
    {
        return flags.contains(flag);
    }
    
    public void setFlag(Flag flag, boolean value)
    {
        if(!value){
            flags.remove(flag);
        }else{
            flags.add(flag);
        }
        
    }
    
    public HashMap<Badge, Integer> getBadges() { return badges; }
    public void setBadges(HashMap<Badge, Integer> badges) { this.badges = badges; }

    public void addBadge(Badge badge, int level)
    {
        if(level > badge.getMaxLevel()){
            level = badge.getMaxLevel();
        }
        this.badges.put(badge, level);
    }
    
    public boolean hasBadge(Badge badge)
    {
        return getBadges().containsKey(badge);
    }

    public int getOnlineTime()
    {
        return (int)((new Date().getTime() - loginTime.getTime())/1000L);
    }
    
    public int getPlayTime()
    {
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return dao.getPlayTime(this);
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    public long getPromotedTime()
    {
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return dao.getPromotedTime(this);
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    public BooleanStringReturn canBeHere(Location loc)
    {
        ZoneWorld world = plugin.getWorld(loc.getWorld());
        Zone zone = world.findZone(loc);
        
        if (zone == null) { // Wilderness - Can be there
            return new BooleanStringReturn(true, null);
        }

        if (getRank().canEditZones()) { // Admins can be there
            return new BooleanStringReturn(true, null);
        }

        Zone.Permission permission = zone.getUser(this);

        if (!zone.hasFlag(Zone.Flag.WHITELIST)) {
            // Banned - Can not be there
            if (permission != null && permission == Permission.BANNED) {
                return new BooleanStringReturn(false, ChatColor.RED + "[" +
                        zone.getName() + "] You are banned from " + zone.getName());
            }

        } else {
            // If no permission (Allowed, Maker, Owner, Banned) then stop
            if (permission == null) {
                return new BooleanStringReturn(false, ChatColor.RED + "[" +
                        zone.getName() + "] You are not allowed to enter " +
                        zone.getName());
            }

            // If the permission is banned then stop
            if (permission == Permission.BANNED) {
                return new BooleanStringReturn(false, ChatColor.RED + "[" +
                        zone.getName() + "] You are banned from " + zone.getName());
            }
        }

        return new BooleanStringReturn(true, null);
    }
    
    public boolean hasBlockPermission(Location loc, boolean punish)
    {
        ZoneWorld world = plugin.getWorld(loc.getWorld());
        
        Zone zone = world.findZone(loc);
        
        if (zone == null) { // Is in the wilderness - So return true
            return true;
        }
        
        Lot lot = zone.findLot(loc);

        if (this.hasFlag(Flag.HARDWARNED)) {
            if (punish == true) {
                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "["
                        + zone.getName() + "] "
                        + "You are hardwarned!");
            }
            return false;
        }

        if (this.getRank() == Rank.NEWCOMER) {
            return false;
            // Don't punish as that's just cruel ;p
        }

        if (this.getRank().canEditZones()) { // Lets people with canModifyZones have block permission
            return true;
        }

        Zone.Permission perm = zone.getUser(this);

        if (perm == Zone.Permission.BANNED) { // If banned then return false
            if (punish == true) {
                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "["
                        + zone.getName() + "] "
                        + "You are banned from this zone!");
            }
            return false;
        }

        if (lot == null &&
                (perm == Zone.Permission.MAKER ||
                 perm == Zone.Permission.OWNER)) { // If allowed/maker/owner and not in a lot : return true
            return true;
        }

        if (lot == null &&
                zone.hasFlag(Zone.Flag.BUILD)) { // If placeDefault and not in a lot : return true
            return true;
        }

        if (lot != null &&
                perm == Zone.Permission.OWNER) { // If communist zone return true
            return true;
        }

        if (lot != null &&
                lot.isOwner(this)) { // If is lot owner
            return true;
        }


        if (punish == true) {
            if (lot != null && zone != null) { // Lot Error Message

                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "["
                        + zone.getName() + "] "
                        + "You do not have sufficient permissions in "
                        + lot.getName() + ".");

            } else { // Zone Error Message

                this.setFireTicks(100);
                this.sendMessage(ChatColor.RED + "["
                        + zone.getName() + "] "
                        + "You do not have sufficient permissions in "
                        + zone.getName() + ".");

            }
        }

        return false; // If they don't fit into any of that. Return false
    }
    
    //org.bukkit.entity.Player override
    @Override
    public boolean teleport(Entity player)
    {
        this.teleportWithHorse(player.getLocation());
        return true;
    }
    
    @Override
    public boolean teleport(Location location)
    {
        this.teleportWithHorse(location);
        return true;
    }
    
    public boolean teleport(BytecraftPlayer player)
    {
        return this.teleport(player.getDelegate());
    }


    public void teleportWithHorse(Location loc)
    {
        Entity v = getVehicle();
        if (v != null && v.getType().isAlive()) {
            v.setPassenger(null);
            teleport(loc);
            v.teleport(loc);
            v.setPassenger(getDelegate());
        }else{
            getDelegate().teleport(loc);
        }
    }
}
