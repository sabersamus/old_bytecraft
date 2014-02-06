package info.bytecraft.commands;

import static org.bukkit.ChatColor.*;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.tregmine.api.math.Distance;

public class NukeCommand extends AbstractCommand
{

    public NukeCommand(Bytecraft instance)
    {
        super(instance, "nuke");
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(!player.getRank().canNuke()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
        
        int distance = 0;
        try{
            distance = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
            distance = 160;
        }catch(ArrayIndexOutOfBoundsException e){
            distance = 160;
        }
        
        Location loc = player.getLocation();
        int i = 0;
        for (Entity ent : player.getWorld().getLivingEntities()) {
            if (Distance.calc2d(loc, ent.getLocation()) > distance) {
                continue;
            }

            if (ent instanceof Monster) {
                Monster mob = (Monster) ent;
                mob.setHealth(0);
            }
            else if (ent instanceof Animals) {
                Animals animal = (Animals) ent;
                animal.setHealth(0);
            }
            else if (ent instanceof Slime) {
                Slime slime = (Slime) ent;
                slime.setHealth(0);
            }
            else if (ent instanceof EnderDragon) {
                EnderDragon dragon = (EnderDragon) ent;
                dragon.setHealth(0);
            }
            i++;
        }
        
        player.sendMessage(YELLOW + "Nuked " + RED + i + YELLOW + " mobs within " + RED + distance + YELLOW + " blocks");
        return true;
    }

}
