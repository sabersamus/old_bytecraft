package info.bytecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class InventoryCommand extends AbstractCommand
{

    public InventoryCommand(Bytecraft instance)
    {
        super(instance, "inv");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.isAdmin())return true;
        if(args.length == 1){
            Player delegate = Bukkit.getPlayer(args[0]);
            if(delegate != null){
                BytecraftPlayer target = plugin.getPlayer(delegate);
                player.openInventory(target.getInventory());
            }
        }
        return true;
    }
    
}
