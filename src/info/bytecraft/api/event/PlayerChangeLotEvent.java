package info.bytecraft.api.event;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.zones.Lot;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeLotEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    
    private final BytecraftPlayer player;
    private Location from;
    private Location to;
    private Lot oldLot;
    private Lot newLot;
    private boolean cancelled;
    
    
    public PlayerChangeLotEvent(Location from, Location to,
            BytecraftPlayer player2, Lot oldLot2, Lot newLot2)
    {
        setFrom(from);
        setTo(to);
        this.player = player2;
        oldLot = oldLot2;
        newLot = newLot2;
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


    public Lot getOldLot()
    {
        return oldLot;
    }


    public void setOldLot(Lot oldLot)
    {
        this.oldLot = oldLot;
    }


    public Lot getNewLot()
    {
        return newLot;
    }


    public void setNewLot(Lot newLot)
    {
        this.newLot = newLot;
    }


    public Location getFrom()
    {
        return from;
    }


    public void setFrom(Location from)
    {
        this.from = from;
    }


    public Location getTo()
    {
        return to;
    }


    public void setTo(Location to)
    {
        this.to = to;
    }
    
}
