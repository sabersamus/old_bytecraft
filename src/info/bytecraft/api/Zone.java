package info.bytecraft.api;

import java.util.HashMap;

import org.bukkit.Location;

import info.bytecraft.api.math.Point;
import info.bytecraft.api.math.Rectangle;

public class Zone
{
    public static enum Permission {
        OWNER("%s is now an owner of %s.", "You are now an owner of %s.",
                "%s is no longer an owner of %s.",
                "You are no longer an owner of %s.",
                "You are an owner in this zone."),
        // can build in the zone
        MAKER("%s is now a maker in %s.", "You are now a maker in %s.",
                "%s is no longer a maker in %s.",
                "You are no longer a maker in %s.",
                "You are a maker in this zone."),
        // is allowed in the zone, if this isn't the default
        ALLOWED("%s is now allowed in %s.", "You are now allowed in %s.",
                "%s is no longer allowed in %s.",
                "You are no longer allowd in %s.",
                "You are allowed in this zone."),
        // banned from the zone
        BANNED("%s is now banned from %s.", "You have been banned from %s.",
                "%s is no longer banned in %s.",
                "You are no longer banned in %s.",
                "You are banned from this zone.");

        private String addedConfirm;
        private String addedNotif;
        private String delConfirm;
        private String delNotif;
        private String permNotification;

        private Permission(String addedConfirmation, String addedNotification,
                String delConfirmation, String delNotification,
                String permNotification)
        {
            this.addedConfirm = addedConfirmation;
            this.addedNotif = addedNotification;
            this.delConfirm = delConfirmation;
            this.delNotif = delNotification;
            this.setPermNotification(permNotification);
        }

        public String getAddedConfirm()
        {
            return addedConfirm;
        }

        public void setAddedConfirm(String addedConfirm)
        {
            this.addedConfirm = addedConfirm;
        }

        public String getAddedNotif()
        {
            return addedNotif;
        }

        public void setAddedNotif(String addedNotif)
        {
            this.addedNotif = addedNotif;
        }

        public String getDelConfirm()
        {
            return delConfirm;
        }

        public void setDelConfirm(String delConfirm)
        {
            this.delConfirm = delConfirm;
        }

        public String getDelNotif()
        {
            return delNotif;
        }

        public void setDelNotif(String delNotif)
        {
            this.delNotif = delNotif;
        }

        public String getPermNotification()
        {
            return permNotification;
        }

        public void setPermNotification(String permNotification)
        {
            this.permNotification = permNotification;
        }
    }
    
    public static enum Flag{
        PVP,
        WHITELIST,
        BUILD,
        HOSTILE,
        ENTERMSG,
        EXITMSG;
    }

    private int id;
    private String name;
    private String world;

    private Rectangle rect;

    private boolean whitelisted;
    private boolean buildable;
    private boolean pvp;
    private boolean hostile;

    private String enterMsg;
    private String exitMsg;
    
    private HashMap<String, Permission> permissions;
    
    public Zone(String name)
    {
        this.name = name;
    }
    
    public Zone()
    {
        this("");
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Rectangle getRect()
    {
        return rect;
    }

    public void setRect(Rectangle rect)
    {
        this.rect = rect;
    }

    public boolean contains(Point p)
    {
        return rect.contains(p);
    }
    
    public boolean contains(Location loc)
    {
        return this.contains(new Point(loc.getBlockX(), loc.getBlockZ()));
    }

    public boolean isWhitelisted()
    {
        return whitelisted;
    }

    public void setWhitelisted(boolean whitelisted)
    {
        this.whitelisted = whitelisted;
    }

    public boolean isBuildable()
    {
        return buildable;
    }

    public void setBuildable(boolean buildable)
    {
        this.buildable = buildable;
    }

    public boolean isPvp()
    {
        return pvp;
    }

    public void setPvp(boolean pvp)
    {
        this.pvp = pvp;
    }

    public boolean isHostile()
    {
        return hostile;
    }

    public void setHostile(boolean hostile)
    {
        this.hostile = hostile;
    }

    public String getEnterMsg()
    {
        return enterMsg;
    }

    public void setEnterMsg(String enterMsg)
    {
        this.enterMsg = enterMsg;
    }

    public String getExitMsg()
    {
        return exitMsg;
    }

    public void setExitMsg(String exitMsg)
    {
        this.exitMsg = exitMsg;
    }

    public String getWorld()
    {
        return world;
    }

    public void setWorld(String world)
    {
        this.world = world;
    }

    public Permission getUser(BytecraftPlayer player)
    {
        if(permissions.containsKey(player.getName())){
            return permissions.get(player.getName());
        }
        return null;
    }
    
    public HashMap<String, Permission> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(HashMap<String, Permission> permissions)
    {
        this.permissions = permissions;
    }

    public boolean intersects(Zone otherZone)
    {
        Point p1 = rect.getPoint1();
        Point p2 = rect.getPoint2();
        int zoneWidth = Math.max(p1.getX(), p2.getX()) - Math.min(p1.getX(), p2.getX());
        int zoneHeight = Math.max(p1.getZ(), p2.getZ()) - Math.min(p1.getZ(), p2.getZ());
        
        Point p3 = otherZone.getRect().getPoint1();
        Point p4 = otherZone.getRect().getPoint2();
        
        int otherWidth = Math.max(p3.getX(), p4.getX()) - Math.min(p3.getX(), p4.getX());
        int otherHeight = Math.max(p3.getZ(), p4.getZ()) - Math.min(p3.getZ(), p4.getZ());
        
        java.awt.Rectangle r1 = new java.awt.Rectangle(p1.getX(), p1.getZ(), zoneWidth, zoneHeight);
        java.awt.Rectangle r2 = new java.awt.Rectangle(p3.getX(), p3.getZ(), otherWidth, otherHeight);
        return r1.intersects(r2);
    }
}
