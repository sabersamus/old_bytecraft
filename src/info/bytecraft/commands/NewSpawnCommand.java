package info.bytecraft.commands;

import org.bukkit.Location;
import org.bukkit.World;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;

public class NewSpawnCommand extends AbstractCommand
{

    public NewSpawnCommand(Bytecraft instance)
    {
        super(instance, "newspawn");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(player.getRank() != Rank.ELDER){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        
        World world = player.getWorld();
        
        Location loc = player.getLocation();
        
        world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        
        return true;
    }
}
