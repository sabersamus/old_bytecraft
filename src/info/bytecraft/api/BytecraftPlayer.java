package info.bytecraft.api;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.math.Vector2D;
import info.bytecraft.blockfill.Fill;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;
import info.bytecraft.database.db.DBPlayerDAO;

import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BytecraftPlayer extends PlayerDelegate
{
    
    public static enum Flag{
        HARDWARNED,
        SOFTWARNED,
        MUTE,
        INVISIBLE,
        TPBLOCK;
    }
    
    private static Bytecraft plugin;
    
    private int id = 0;
    private Rank rank;

    private String chatChannel = "GLOBAL";
    
    private Block fillBlock1;
    private Block fillBlock2;
    private Block zoneBlock1;
    private Block zoneBlock2;
    private Block lotBlock1;
    private Block lotBlock2;
    
    private Fill lastFill;
    private Zone currZone = null;
    
    private Set<Flag> flags;
    
    private BytecraftPlayer blessTarget;
    
    private Date loginTime;

    public BytecraftPlayer(Player player)
    {
        super(player);
        loginTime = new Date();
        flags = EnumSet.noneOf(Flag.class);
    }
    
    public static void setPlugin(Bytecraft plugin)
    {
        BytecraftPlayer.plugin = plugin;
    }

    public BytecraftPlayer(String name)
    {
        super(Bukkit.getPlayer(name));
        loginTime = new Date();
        flags = EnumSet.noneOf(Flag.class);
    }

    //Bytecraft stored values
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    public Rank getRank()
    {
        return this.rank;
    }
    
    public void setRank(Rank rank)
    {
        this.rank = rank;
        setPlayerListName(rank.getColor() + getName());
    }
    
    public String getChatChannel()
    {
        return chatChannel.toUpperCase();
    }

    public void setChatChannel(String chatChannel)
    {
        this.chatChannel = chatChannel;
    }
    
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
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return ((DBPlayerDAO)dao).formattedBalance(this);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public ChatColor getGodColor(){
        try (IContext ctx = plugin.createContext()){
            IPlayerDAO dao = ctx.getPlayerDAO();
            return dao.getGodColor(this);
        }catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    //Zones
    public Zone getCurrentZone()
    {
        return currZone;
    }
    
    public void setCurrentZone(Zone zone)
    {
        this.currZone = zone;
    }
    
    //org.bukkit.entity.Player override
    public void setPlayerListName(String name)
    {
        super.setPlayerListName(name);
    }
    
    public String getDisplayName()
    {
        return getRank().getColor() + getName();
    }
    
    //Bytecraft non-persistent 
    public int getMaxTeleportDistance()
    {
        if(isAdmin())return Integer.MAX_VALUE;
        if(isDonator())return 15000;
        
        return 300;
    }
    
    public long getTeleportTimeout()
    {
        if(isAdmin()) return 20 * 0L;
        if(rank == Rank.GAURD) return 20 * 3L;
        
        if(rank == Rank.DONATOR) return 20 * 4L;
        
        return 20 * 5L;
    }
    
    public BytecraftPlayer getBlessTarget()
    {
        return blessTarget;
    }

    public void setBlessTarget(BytecraftPlayer blessTarget)
    {
        this.blessTarget = blessTarget;
    }
    
    public void sendNotification(Notification notif, String message)
    {
        if(message != null && !message.equalsIgnoreCase("")){
            sendMessage(message);
        }
        this.playSound(getLocation(), notif.getSound(), 2F, 1F);
    }

    public Block getFillBlock1()
    {
        return fillBlock1;
    }

    public void setFillBlock1(Block fillBlock1)
    {
        this.fillBlock1 = fillBlock1;
    }

    public Block getFillBlock2()
    {
        return fillBlock2;
    }

    public void setFillBlock2(Block fillBlock2)
    {
        this.fillBlock2 = fillBlock2;
    }

    public void setLastFill(Fill fill)
    {
        this.lastFill = fill;
    }
    
    public Block getZoneBlock1()
    {
        return zoneBlock1;
    }

    public void setZoneBlock1(Block zoneBlock1)
    {
        this.zoneBlock1 = zoneBlock1;
    }

    public Block getZoneBlock2()
    {
        return zoneBlock2;
    }

    public void setZoneBlock2(Block zoneBlock2)
    {
        this.zoneBlock2 = zoneBlock2;
    }

    public Block getLotBlock1()
    {
        return lotBlock1;
    }

    public void setLotBlock1(Block lotBlock1)
    {
        this.lotBlock1 = lotBlock1;
    }

    public Block getLotBlock2()
    {
        return lotBlock2;
    }

    public void setLotBlock2(Block lotBlock2)
    {
        this.lotBlock2 = lotBlock2;
    }

    public Fill getLastFill()
    {
        return this.lastFill;
    }
    
    public boolean hasFlag(Flag flag)
    {
        return flags.contains(flag);
    }
    
    public void setFlag(Flag flag)
    {
        flags.add(flag);
    }
    
    public void removeFlag(Flag flag)
    {
        flags.remove(flag);
    }
    
    //Bytecraft Rank-Inheritance 
    public boolean isAdmin()
    {
        return (this.rank == Rank.ADMIN || this.rank == Rank.SENIOR_ADMIN || isCoder());
    }
    
    public boolean isCoder()
    {
        return this.rank == Rank.CODER;
    }
    
    public boolean isModerator()
    {
        return (isAdmin() || this.rank == Rank.GAURD);
    }
    
    public boolean canFill()
    {
        return (isAdmin() || this.rank == Rank.BUILDER);
    }
    
    public boolean isDonator()
    {
        return (isModerator() || this.rank == Rank.DONATOR || this.rank == Rank.BUILDER);
    }
    
    public boolean isMentor()
    {
        return (isAdmin() || this.rank == Rank.MENTOR);
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
    
    public Vector2D getVector()
    {
        Location loc = getLocation();
        return new Vector2D(loc.getBlockX(), loc.getBlockZ(), loc.getWorld());
    }
    
}
