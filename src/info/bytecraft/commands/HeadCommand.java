package info.bytecraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class HeadCommand extends AbstractCommand
{

    public HeadCommand(Bytecraft instance)
    {
        super(instance, "head");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canSpawnHeads()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        
        
        if (args.length == 1) {
            ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
            itemMeta.setOwner(args[0]);
            itemMeta.setDisplayName(ChatColor.YELLOW + args[0] + "'s head");
            item.setItemMeta(itemMeta);
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(item);
            player.sendMessage(ChatColor.YELLOW + "You received the head of "
                    + args[0]);
        }
        else {
            player.sendMessage(ChatColor.RED + "Type /head <player>");
        }
        return true;
    }

}
