package info.bytecraft.api.event;

import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    public enum Reason{
        KICK,
        QUIT,
        UNKNOWN;
    }
    
    private final BytecraftPlayer player;
    private String message;
    private Reason reason;
    
    public PlayerLeaveEvent(BytecraftPlayer player, String message, Reason reason)
    {
        this.player = player;
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


    public String getMessage()
    {
        return message;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }


    public Reason getReason()
    {
        return reason;
    }


    public void setReason(Reason reason)
    {
        this.reason = reason;
    }

}
