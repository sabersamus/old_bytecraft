package info.bytecraft.zones;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.math.Point;
import info.bytecraft.api.math.Rectangle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lot
{
    private int id;
    private String zoneName;
    private String name;
    private Rectangle rect;
    private Set<String> owners;
    private Zone zone;

    public Lot()
    {
        this.owners = new HashSet<String>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getZoneName()
    {
        return zoneName;
    }

    public void setZoneName(String name)
    {
        this.zoneName = name;
    }

    public Set<String> getOwners()
    {
        return owners;
    }

    public void setOwner(List<String> owners)
    {
        this.owners.addAll(owners);
    }

    public boolean isOwner(BytecraftPlayer player)
    {
        return owners.contains(player.getName());
    }

    public void addOwner(BytecraftPlayer player)
    {
        owners.add(player.getName());
    }

    public void deleteOwner(BytecraftPlayer player)
    {
        owners.remove(player.getName());
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

    public Zone getZone()
    {
        return zone;
    }

    public void setZone(Zone zone)
    {
        this.zone = zone;
    }
    
    public boolean intersects(Lot other)
    {
        Point p1 = new Point(rect.getLeft(), rect.getTop());
        Point p2 = new Point(rect.getRight(), rect.getBottom());
        int zoneWidth = Math.max(p1.getX(), p2.getX()) - Math.min(p1.getX(), p2.getX());
        int zoneHeight = Math.max(p1.getZ(), p2.getZ()) - Math.min(p1.getZ(), p2.getZ());
        
        
        Rectangle zr2 = other.getRect();
        Point p3 = new Point(zr2.getLeft(), zr2.getTop());
        Point p4 = new Point(zr2.getRight(), zr2.getBottom());
        
        int otherWidth = Math.max(p3.getX(), p4.getX()) - Math.min(p3.getX(), p4.getX());
        int otherHeight = Math.max(p3.getZ(), p4.getZ()) - Math.min(p3.getZ(), p4.getZ());
        
        java.awt.Rectangle r1 = new java.awt.Rectangle(p1.getX(), p1.getZ(), zoneWidth, zoneHeight);
        java.awt.Rectangle r2 = new java.awt.Rectangle(p3.getX(), p3.getZ(), otherWidth, otherHeight);
        return r1.intersects(r2);
    }
}
