package info.bytecraft.commands;

import java.util.List;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.Notification;
import info.bytecraft.api.Rank;
import info.bytecraft.api.util.StringUtil;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;

public class MessageCommand extends AbstractCommand
{

    public MessageCommand(Bytecraft instance, String command)
    {
        super(instance, command);
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(command.equalsIgnoreCase("reply")){
            if(args.length >= 1){ 
                this.replyToLastMessage(player, StringUtil.join(args, ' ', 1, args.length));
                return true;
            }
        }else if(command.equalsIgnoreCase("message")){
            if(args.length >= 2){
                List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[0]);
                if (cantidates.size() != 1) {
                    return true;
                
                }
                BytecraftPlayer target = cantidates.get(0);
                sendMessage(player, target, StringUtil.join(args, ' ', 1, args.length));
                plugin.getLogger().info("[Message] " + player.getName() + " -> " + 
                target.getName() + " : " + StringUtil.join(args, ' ', 1, args.length));
            }
        }
        return true;
    }
    
    private void sendMessage(BytecraftPlayer player, BytecraftPlayer target, String message)
    {
        target.sendNotification(Notification.MESSAGE, ChatColor.GREEN
                + "<msg> " + player.getTemporaryChatName() + ": "
                + ChatColor.GREEN + message);
        if(target.hasFlag(Flag.INVISIBLE)){
            if(player.getRank() == Rank.ELDER || player.getRank() == Rank.PRINCESS){
                player.sendMessage(ChatColor.GREEN + "<To> "
                        + target.getTemporaryChatName() + ": " + ChatColor.GREEN
                        + message);
            }
        }else{
            player.sendMessage(ChatColor.GREEN + "<To> "
                    + target.getTemporaryChatName() + ": " + ChatColor.GREEN
                    + message);
        }
        target.setLastMessager(player);
        
        try(IContext ctx = plugin.createContext()){
            ctx.getLogDAO().insertPrivateMessage(player, target, message);
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
        
    }
    
    private void replyToLastMessage(BytecraftPlayer player, String message)
    {
        BytecraftPlayer target = player.getLastMessager();
        if(target == null){
            player.sendMessage(ChatColor.RED + "No one has messaged you yet.");
            return;
        }
        sendMessage(player, target, message);
    }

}
