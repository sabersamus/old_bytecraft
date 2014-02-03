package info.bytecraft.commands;

import java.util.List;

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
        if(!player.getRank().canViewInventories())return true;
        if(args.length == 1){
            List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
            if(cantidates.size() != 1){
                return true;
            }
            
            BytecraftPlayer target = cantidates.get(0);
                player.openInventory(target.getInventory());
        }
        return true;
    }
    
}
