package info.bytecraft.listener;

import info.bytecraft.Bytecraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class BytecraftServerListener implements Listener
{
    private Bytecraft plugin;
    
    public BytecraftServerListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPing(ServerListPingEvent event)
    {
        event.setMaxPlayers(666);
    }
    
    
}
