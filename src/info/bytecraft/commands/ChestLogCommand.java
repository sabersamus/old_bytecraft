package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;

public class ChestLogCommand extends AbstractCommand
{

    public ChestLogCommand(Bytecraft plugin)
    {
        super(plugin, "chestlog");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canSeeChestLogs())return true;
        
        if(args.length != 1)return true;
        
        boolean value = Boolean.parseBoolean(args[0]);
        
        player.setFlag(Flag.CHEST_LOG, value);
        player.sendMessage(ChatColor.RED + "Your chestlog flag has been set to " + value);
        
        return true;
    }

}
