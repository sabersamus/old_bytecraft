package info.bytecraft.commands;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class CreateMobCommand extends AbstractCommand
{
    
    public CreateMobCommand(Bytecraft instance)
    {
        super(instance, "cmob");
    }
    
    @Override
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(player.getRank().canSpawnMobs()){
            EntityType mobType;
            try {
                String mobName = args[0];
                mobType = EntityType.fromName(mobName);
            } catch (Exception e) {
                player.sendMessage(RED + "Sorry, that mob doesn't exist.");
                return true;
            }

            int amount = 1;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                amount = 1;
            } catch (ArrayIndexOutOfBoundsException e) {
                amount = 1;
            }

            if (mobType == null) {
                StringBuilder buf = new StringBuilder();

                String delim = "";
                for (EntityType mob : EntityType.values()) {
                    if (mob.isSpawnable() && mob.isAlive()) {
                        buf.append(delim);
                        buf.append(mob.getName());
                        delim = ", ";
                    }
                }

                player.sendMessage("Valid names are: ");
                player.sendMessage(buf.toString());

                return true;
            }

            World world = player.getWorld();
            Location loc = player.getLocation();
            for (int i = 0; i < amount; i++) {

                if (!mobType.isSpawnable()) {
                    continue;
                }
                if (!mobType.isAlive()) {
                    continue;
                }
                
                if(mobType == EntityType.ENDER_DRAGON){
                    break;
                }

                LivingEntity ent = (LivingEntity) world.spawnEntity(loc, mobType);
                if (args.length == 3) {
                    ent.setCustomName(args[2]);
                    ent.setCustomNameVisible(true);
                }
            }

            player.sendMessage(YELLOW + "You created " + amount + " "
                    + mobType.getName() + ".");
            plugin.getLogger().info(player.getName() + " created " + amount + " "
                    + mobType.getName());

            return true;
        }else{
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }
    }
}
