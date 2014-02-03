package info.bytecraft.api.event;

import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMoveBlockEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final BytecraftPlayer player;
    private boolean cancelled;
    private Location to;
    private Location from;
    
    public PlayerMoveBlockEvent(Location to, Location from, BytecraftPlayer player)
    {
        this.player = player;
        this.to = to;
        this.from = from;
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

    public BytecraftPlayer getPlayer()
    {
        return player;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancelled = cancel;
    }

    public Location getTo()
    {
        return to;
    }
    
    public void setTo(Location newTo)
    {
        to = newTo;
    }

    public Location getFrom()
    {
        return from;
    }
    
    public void setFrom(Location newFrom)
    {
        from = newFrom;
    }
    
}
