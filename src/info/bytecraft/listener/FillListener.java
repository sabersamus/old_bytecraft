package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FillListener implements Listener
{
    private Bytecraft plugin;
    public FillListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSelect(PlayerInteractEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if(event.isCancelled())return;
        if(player.canFill()){
            if(player.getItemInHand().getType() == Material.WOOD_SPADE && event.getAction() == Action.RIGHT_CLICK_BLOCK){
                event.setCancelled(true);
                Block block = event.getClickedBlock();
                if(player.getFillBlock1() == null && player.getFillBlock2() == null){//neither block selected
                    player.setFillBlock1(block);
                    player.sendMessage(ChatColor.YELLOW + "Fill block 1 selected");
                    event.setCancelled(true);
                    return;
                }
                if(player.getFillBlock1() != null && player.getFillBlock2() == null){//first block selected
                    player.setFillBlock2(block);
                    player.sendMessage(ChatColor.YELLOW + "Fill block 2 selected");
                    event.setCancelled(true);
                    return;
                }
                if(player.getFillBlock1() != null && player.getFillBlock2() != null){//both block selected
                    player.setFillBlock1(block);
                    player.setFillBlock2(null);
                    player.sendMessage(ChatColor.YELLOW + "Fill block 1 selected");
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
