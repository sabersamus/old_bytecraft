package info.bytecraft.zones;

import info.bytecraft.api.BytecraftPlayer;
import info.tregmine.quadtree.Rectangle;

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
        if(!this.getZoneName().equalsIgnoreCase(other.getZoneName())){
            return false;
        }
        
        return this.rect.intersects(other.rect);
    }
}
