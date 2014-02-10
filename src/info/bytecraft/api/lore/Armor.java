package info.bytecraft.api.lore;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public enum Armor
{
    BASSINET(AQUA + "Bassinet"),
    BOOTS(AQUA + "Boots of Awe"),
    BRASSARD(AQUA + "Brassard"),
    CHESTPLATE(GOLD + "Chestplate of " + God.getRandom()),
    CUIRASS(AQUA + "Cuirass"),
    COIF(AQUA + "Coif"),
    COWL(AQUA + "Cowl"),
    FULLHELM(AQUA + "Fullhelm"),
    GREAVES(AQUA + "Greaves"),
    GUTBUKKIT(AQUA + "Gut Bukkit"),
    HELM_OF_NOTCH(AQUA + "Helm of Notch"),
    MOCCASINS(AQUA + "Moccasins"),
    PAULDRONS(AQUA + " Pauldrons of Valiance"),
    SABATONS(AQUA + "Sabatons"),
    SKULLSHIELD(AQUA + "Skull Shield"),
    STOMPERS(AQUA + "Stompers"),
    TASSETS(GOLD + "Tassets"),
    PLATELEGS(AQUA + "Platelegs");
    
    private final String name;
    private static HashMap<Integer, Armor> armors = Maps.newHashMap();
    private static HashMap<Armor, String> byPiece = Maps.newHashMap();
    
    static{
        for(Armor armor: values()){
            armors.put(armor.ordinal(), armor);
        }
        
        //Chest peice
        byPiece.put(CHESTPLATE, "CHESTPLATE");
        byPiece.put(CUIRASS, "CHESTPLATE");
        byPiece.put(GUTBUKKIT, "CHESTPLATE");
        byPiece.put(PAULDRONS, "CHESTPLATE");
        
        //leggings
        byPiece.put(BRASSARD, "LEGGINGS");
        byPiece.put(TASSETS, "LEGGINGS");
        byPiece.put(PLATELEGS, "LEGGINGS");
        byPiece.put(GREAVES, "LEGGINGS");
        
        //helmet
        byPiece.put(COIF, "HELMET");
        byPiece.put(COWL, "HELMET");
        byPiece.put(BASSINET, "HELEMET");
        byPiece.put(FULLHELM, "HELMET");
        byPiece.put(HELM_OF_NOTCH, "HELMET");
        byPiece.put(SKULLSHIELD, "HELMET");

        //boots
        byPiece.put(BOOTS, "BOOTS");
        byPiece.put(MOCCASINS, "BOOTS");
        byPiece.put(SABATONS, "BOOTS");
        byPiece.put(STOMPERS, "BOOTS");
    }

    private Armor(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
    
    public static Armor getRandomByType(String name)
    {
        List<Armor> armors = Lists.newArrayList();
        for(Armor armor: values()){
            if(byPiece.get(armor).equalsIgnoreCase(name)){
                armors.add(armor);
            }
        }
        return armors.get(new Random().nextInt(armors.size() - 1));
    }

}
