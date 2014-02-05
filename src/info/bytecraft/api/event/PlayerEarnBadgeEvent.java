package info.bytecraft.api.event;

import info.bytecraft.api.Badge;
import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEarnBadgeEvent extends Event
{
    
    private static final HandlerList handlers = new HandlerList();
    
    private final BytecraftPlayer player;
    private final Badge badge;
    
    public PlayerEarnBadgeEvent(BytecraftPlayer player, Badge badge)
    {
        this.player = player;
        this.badge = badge;
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

    public Badge getBadge()
    {
        return badge;
    }

    public BytecraftPlayer getPlayer()
    {
        return player;
    }

}
