package info.bytecraft.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;

public class PlayerChangeRankEvent extends Event
{
    public static final HandlerList handlers = new HandlerList();
    
    private final BytecraftPlayer player;
    private final Rank oldRank;
    private Rank newRank;
    
    public PlayerChangeRankEvent(BytecraftPlayer player, Rank oldRank, Rank newRank)
    {
        this.player = player;
        this.oldRank = oldRank;
        this.setNewRank(newRank);
    }
    
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }


    public BytecraftPlayer getPlayer()
    {
        return player;
    }


    public Rank getOldRank()
    {
        return oldRank;
    }


    public Rank getNewRank()
    {
        return newRank;
    }


    public void setNewRank(Rank newRank)
    {
        this.newRank = newRank;
    }

}
