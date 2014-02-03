package info.bytecraft.api.event;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.zones.Zone;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeZoneEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final BytecraftPlayer player;
    private Location to;
    private Location from;
    private final Zone newZone;
    private final Zone oldZone;
    private boolean cancelled;
    
    public PlayerChangeZoneEvent(Location to, Location from, BytecraftPlayer player, Zone oldZone, Zone newZone)
    {
        this.player = player;
        this.newZone = newZone;
        this.oldZone = oldZone;
        setTo(to);
        setFrom(from);
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }

    public BytecraftPlayer getPlayer()
    {
        return player;
    }

    public Zone getNewZone()
    {
        return newZone;
    }

    public Zone getOldZone()
    {
        return oldZone;
    }

    public Location getTo()
    {
        return to;
    }

    public void setTo(Location to)
    {
        this.to = to;
    }

    public Location getFrom()
    {
        return from;
    }

    public void setFrom(Location from)
    {
        this.from = from;
    }
    
}
