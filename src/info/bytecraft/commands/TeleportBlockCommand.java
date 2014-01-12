package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

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
                boolean block = input.equalsIgnoreCase("on") ? true : false;
                player.setTeleportBlock(block);
                if (block == true) {
                    player.sendMessage(ChatColor.RED
                            + "Teleport block activated");
                }
                else {
                    player.sendMessage(ChatColor.AQUA
                            + "Teleport block de-activated");
                }
            }
            else if (input.equalsIgnoreCase("status")) {
                player.sendMessage(ChatColor.AQUA
                        + "Your teleport block status is: " + ChatColor.RED
                        + (player.isTeleportBlock() ? "active" : "inactive"));
            }
        }
        return true;
    }
}
