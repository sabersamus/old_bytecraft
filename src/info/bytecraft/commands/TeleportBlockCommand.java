package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IPlayerDAO;

public class TeleportBlockCommand extends AbstractCommand
{

    public TeleportBlockCommand(Bytecraft instance)
    {
        super(instance, "tpblock");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.isDonator())
            return true;
        if (args.length == 1) {
            String input = args[0];
            if (input.equalsIgnoreCase("on") || input.equalsIgnoreCase("off")) {
                try(IContext ctx = Bytecraft.createContext()){
                    IPlayerDAO dao = ctx.getPlayerDAO();
                    boolean block = input.equalsIgnoreCase("on") ? true : false;
                    if (block == true) {
                        player.setFlag(Flag.TPBLOCK);
                        player.sendMessage(ChatColor.RED
                                + "Teleport block activated");
                    }
                    else {
                        player.removeFlag(Flag.TPBLOCK);
                        player.sendMessage(ChatColor.AQUA
                                + "Teleport block de-activated");
                    }
                    dao.updateProperties(player);
                }catch(DAOException e){
                    throw new RuntimeException(e);
                }
            }
            else if (input.equalsIgnoreCase("status")) {
                player.sendMessage(ChatColor.AQUA
                        + "Your teleport block status is: " + ChatColor.RED
                        + (player.hasFlag(Flag.TPBLOCK) ? "active" : "inactive"));
            }
        }
        return true;
    }
}
