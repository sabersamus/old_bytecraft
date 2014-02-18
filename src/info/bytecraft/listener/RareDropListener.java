package info.bytecraft.listener;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.lore.Creature;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILoreDAO;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.*;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

public class RareDropListener implements Listener
{

    private Bytecraft plugin;
    private final List<BytecraftPlayer> players;
    private final Random random = new Random();
    
    public RareDropListener(Bytecraft plugin)
    {
        this.plugin = plugin;
        players = Lists.newArrayList();
    }
    
    private class DropDelayTask extends BukkitRunnable
    {
        
        private BytecraftPlayer player;
        
        public DropDelayTask(BytecraftPlayer player)
        {
            this.player = player;
        }

        @Override
        public void run()
        {
            players.remove(player);
        }
        
    }
    
    private Set<EntityType> entityTypes = EnumSet.of(SKELETON, 
            SPIDER, ZOMBIE, ENDERMAN, CREEPER, GHAST, WITCH, CAVE_SPIDER,
            WITHER, ENDER_DRAGON);
    
    private Material[] leather_armor = { LEATHER_HELMET, LEATHER_CHESTPLATE,
            LEATHER_LEGGINGS, LEATHER_BOOTS };

    private Material[] iron_armor = { IRON_HELMET, IRON_CHESTPLATE,
            IRON_LEGGINGS, IRON_BOOTS };

    private Material[] gold_armor = { GOLD_HELMET, GOLD_CHESTPLATE,
            GOLD_LEGGINGS, GOLD_BOOTS };

    private Material[] diamond_armor = { DIAMOND_HELMET, DIAMOND_CHESTPLATE,
            DIAMOND_LEGGINGS, DIAMOND_BOOTS };
    
    private Material[] getRandomArmorSet()
    {
        int i = random.nextInt(99);// 0-99 -> 100%
        if (i < 50) { // 50% (0-49)
            return leather_armor;
        }
        if (i >= 50 && i <= 74) { // 25% (50-74)
            return iron_armor;
        }
        if (i >= 75 && i <= 94) {// 20% (75 - 94)
            return gold_armor;
        }
        if (i >= 95 && i <= 99) {// 5% (95 - 99)
            return diamond_armor;
        }
        return leather_armor;
    }

    private Material getRandomArmor()
    {
        int i = random.nextInt(99);
        Material[] armor = getRandomArmorSet();

        if (i < 50) {
            return armor[0]; // returns helmet
        }
        if (i >= 50 && i <= 74) {
            return armor[3]; // returns boots
        }
        if (i >= 75 && i <= 94) {
            return armor[2]; // returns leggings
        }
        if (i >= 95 && i <= 99) {
            return armor[1]; // returns chest_plate
        }
        return armor[0];
    }

    private Material getRandomSword()
    {
        int i = random.nextInt(99);
        if (i < 50) { // 50%
            return WOOD_SWORD;
        }
        if (i >= 50 && i <= 74) { // 25%
            return STONE_SWORD;
        }
        if (i >= 75 && i <= 94) { // 20%
            return IRON_SWORD;
        }
        if (i >= 95 && i <= 99) { // 5%
            return DIAMOND_SWORD;
        }

        return WOOD_SWORD;
    }
    
    @EventHandler
    public void onKill(EntityDeathEvent event)
    {
        if(!entityTypes.contains(event.getEntityType())){
            return;
        }
        
        LivingEntity ent = event.getEntity();
        EntityDamageEvent cause = ent.getLastDamageCause();
        if (!(cause instanceof EntityDamageByEntityEvent)){
            return;
        }
        
        Entity damager = ((EntityDamageByEntityEvent) cause).getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        
        if(ent.hasMetadata("spawned")){
            for(MetadataValue value: ent.getMetadata("spawned")){
                if(value.asBoolean()){
                    return;
                }
            }
        }
        
        BytecraftPlayer player = plugin.getPlayer((Player)damager);
        
        if(players.contains(player)){
            return;
        }
        
        int i = random.nextInt(99) + 1; //1 - 100
        
        Creature creature = Creature.getByType(ent.getType());
        
        int chance = creature.getChance();
        
        /*
         * Admin = chance + 25
         * Lord = chance + 10
         * Noble = chance + 5
         * everyone else = chance + 0 
         */
        chance += player.getRank().getRareDropIncrease();
        
        if(i <= chance){//if x <= y, ex: i is 1 and chance is 3, drop
            //do drop
            drop(player);
        }
    }


    private void drop(BytecraftPlayer player)
    {
        int i = random.nextInt(99) + 1;
        Material material;
        List<String> lore = Lists.newArrayList();
        if(i <= 75){
          //do sword stuff
            material = this.getRandomSword();
        }else if(i > 75 && i < 100){
          //25 percent chance
            material = this.getRandomArmor();
        }else{
            material = Material.WRITTEN_BOOK;
        }
        
        ItemStack stack = new ItemStack(material, 1);   
        ItemMeta meta = stack.getItemMeta();
        
        if(material == Material.WRITTEN_BOOK){
            BookMeta book = (BookMeta)meta;
            book.setTitle(ChatColor.RED + "A magic book!");
            book.setAuthor(ChatColor.BLUE + "GOD");
            book.addPage(ChatColor.DARK_GREEN + "This book is worth: " +
            ChatColor.RED + (random.nextInt(100000) + 1) + ChatColor.DARK_GREEN + " bytes!\n"
                    + "You can only redem this from an admin!");
            
            List<String> bookLore = this.getBookLore();
            for(int j = 0; j < 3; j++){
               lore.add(bookLore.get(j));
            }
            
            book.setLore(lore);
            stack.setItemMeta(book);
            
            player.getInventory().addItem(stack);
            player.updateInventory();
            player.sendMessage(ChatColor.GOLD + "Congratulations, you've received an item from the rare drop table!");
            player.sendMessage(ChatColor.GOLD + "You must now wait " + ChatColor.RED +
                    player.getRank().getRareDropTimeout() + ChatColor.GOLD + " minutes before receiving another!");
            players.add(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, 
                    new DropDelayTask(player), player.getRank().getRareDropTimeout() * 20 * 60L);
            return;
        }
        
        if((random.nextInt(99) + 1) <= 5){
            //5% chance
            int durability = random.nextInt(200) + 1;
            
            lore.add(ChatColor.GOLD + "Durability: " + ChatColor.WHITE + durability);
            stack.setDurability((short) (material.getMaxDurability() +  -durability));
        }
       
        
        boolean sword = material.name().contains("SWORD");
        
        if(sword){
            //meta.setDisplayName(material(stack) + this.randomSwordName());
            
            int damage = random.nextInt(50) + 1;
            //Rare sword always has bonus damage.
            lore.add(ChatColor.GOLD + "Damage: " + ChatColor.WHITE + damage);
            
            if(random.nextInt(99) + 1 <= 6){
                //6% chance
                int critical = random.nextInt(15) + 1;
                
                lore.add(ChatColor.GOLD + "Critical: " + ChatColor.WHITE + critical);
            }
            
        }else{
            //meta.setDisplayName(material(stack) + this.randomArmorName(material));
            //do armor stuff
            
            int armor = random.nextInt(30) + 1;
            //rare armor always has armor bonus.
            lore.add(ChatColor.GOLD + "Armor: " + ChatColor.WHITE + armor);
        }
        
        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        player.getInventory().addItem(stack);
        player.updateInventory();
        player.sendMessage(ChatColor.GOLD + "Congratulations, you've received an item from the rare drop table!");
        player.sendMessage(ChatColor.GOLD + "You must now wait " + ChatColor.RED +
                player.getRank().getRareDropTimeout() + ChatColor.GOLD + " minutes before receiving another!");
        players.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new DropDelayTask(player), player.getRank().getRareDropTimeout() * 20 * 60L);
    }
    
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event)
    {
        if(event.getSpawnReason() != SpawnReason.SPAWNER 
                && event.getSpawnReason() != SpawnReason.SPAWNER_EGG){
            return;
        }
        
        if(!this.entityTypes.contains(event.getEntityType())){
            return;
        }
        
        LivingEntity ent = event.getEntity();
        ent.setMetadata("spawned", new FixedMetadataValue(plugin, true));
    }
    
    private String material(ItemStack stack)
    {
        String[] args = stack.getType().name().split("_");
        return ChatColor.AQUA + WordUtils.capitalizeFully(args[0]) + " ";
    }
    
    /*public String randomSwordName()
    {

        List<String> names = plugin.getSwordNames();

        int size = names.size();

        return names.get(new Random().nextInt(size - 1));
    }
    
    public String randomArmorName(Material material)
    {
        List<String> names =
                plugin.getArmorNames(material.name().split("_")[1]);
        int size = names.size();

        return names.get(new Random().nextInt(size - 1));
    }*/

    public List<String> getBookLore()
    {
        try(IContext ctx = plugin.createContext()){
            ILoreDAO dao = ctx.getLoreDAO();
            
            return dao.getBookLore();
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }

}