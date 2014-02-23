package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class ChangeNameCommand extends AbstractCommand
{

    public ChangeNameCommand(Bytecraft instance)
    {
        super(instance, "cname");
    }
    
    @Override
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length != 2) {
            return false;
        }
        if (!player.getRank().canChangeName()) {
            return true;
        }

        ChatColor color = ChatColor.getByChar(args[0]);
        player.setTemporaryChatName(color + args[1]);
        player.sendMessage("You are now: " + player.getTemporaryChatName());
        
        try{
            player.setPlayerListName(color + args[1]);
        }catch(Exception e){
            player.sendMessage(ChatColor.RED + "You cant set your player list name to that, sorry.");
        }
        
        return true;
    }

    
        
}
