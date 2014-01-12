package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class ClearCommand extends AbstractCommand
{

    public ClearCommand(Bytecraft instance)
    {
        super(instance, "clear");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        player.getInventory().clear();
        player.sendMessage(ChatColor.AQUA + "Your inventory has been cleared.");
        return true;
    }

}
