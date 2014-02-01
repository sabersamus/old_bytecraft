package info.bytecraft.zones;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.math.Point;
import info.bytecraft.api.math.Rectangle;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class Zone
{
    public enum Permission {
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
        
        public static Permission fromString(String name)
        {
            for(Permission perm: values()){
                if(perm.name().equalsIgnoreCase(name)){
                    return perm;
                }
            }
            return null;
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
    private final Map<Flag, Boolean> flags;
    
    private Rectangle rect;
    private String world;
    
    private String enterMessage;
    private String exitMessage;
    private Map<String, Permission> permissions;
    
    public Zone(String name)
    {
        this.setName(name);
        flags = new HashMap<>();
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
    
    public Map<String, Permission> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(Map<String, Permission> permissions)
    {
        this.permissions = permissions;
    }
    
    public void addPermissions(String name, Permission perm)
    {
        this.permissions.put(name, perm);
    }
    
    public void removePermission(String name)
    {
        this.permissions.remove(name);
    }
    
    public Permission getUser(BytecraftPlayer player)
    {
        if(permissions.containsKey(player.getName())){
            return permissions.get(player.getName());
        }
        return null;
    }

    public boolean hasFlag(Flag flag)
    {
        return this.flags.get(flag);
    }
    
    public void setFlag(Flag flag, boolean value)
    {
        flags.put(flag, value);
    }
    
    public Rectangle getRectangle()
    {
        return rect;
    }

    public void setRectangle(Rectangle rect)
    {
        this.rect = rect;
    }

    public String getEnterMessage()
    {
        return enterMessage;
    }

    public void setEnterMessage(String enterMessage)
    {
        this.enterMessage = enterMessage;
    }

    public String getExitMessage()
    {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage)
    {
        this.exitMessage = exitMessage;
    }

    public void setWorld(String world)
    {
        this.world = world;
    }
    
    public String getWorld()
    {
        return this.world;
    }

    public boolean intersects(Zone other)
    {
        Point p1 = rect.getPoint1();
        Point p2 = rect.getPoint2();
        int zoneWidth = Math.max(p1.getX(), p2.getX()) - Math.min(p1.getX(), p2.getX());
        int zoneHeight = Math.max(p1.getZ(), p2.getZ()) - Math.min(p1.getZ(), p2.getZ());
        
        Point p3 = other.getRectangle().getPoint1();
        Point p4 = other.getRectangle().getPoint2();
        
        int otherWidth = Math.max(p3.getX(), p4.getX()) - Math.min(p3.getX(), p4.getX());
        int otherHeight = Math.max(p3.getZ(), p4.getZ()) - Math.min(p3.getZ(), p4.getZ());
        
        java.awt.Rectangle r1 = new java.awt.Rectangle(p1.getX(), p1.getZ(), zoneWidth, zoneHeight);
        java.awt.Rectangle r2 = new java.awt.Rectangle(p3.getX(), p3.getZ(), otherWidth, otherHeight);
        return r1.intersects(r2);
    }

    public boolean contains(Location to)
    {
        return this.rect.contains(new Point(to.getBlockX(), to.getBlockZ()));
    }

}
