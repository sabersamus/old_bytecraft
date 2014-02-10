package info.bytecraft.api.lore;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

public enum Sword {
    BOOMSTICK(AQUA + "The Boomstick"),
    BROADSWORD(AQUA + "Broadsword"),
    CLAYMORE(AQUA + "Claymore"),
    CUTLASS(AQUA + "Cutlass"),
    DARK_RAGE(AQUA + "Dark Rage"),
    EXCALIBUR(GOLD + "Excalibur"),
    EPEE(AQUA + "Epee"),
    EQUALIZER(AQUA + "Equalizer"),
    GODSWORD(GOLD + "Godsword of " + God.getRandom()),
    GLAMDRING(AQUA + "Glamdring"),
    KATANA(AQUA + "Katana"),
    LONGSWORD(AQUA + "Longsword"),
    NIGHTFURY(AQUA + "Night Fury"),
    NIGHTSLICER(AQUA + "Night Slicer"),
    NIGHTMARE_SABER(AQUA + "Nightmare Sabre"),
    PAINMAKER(AQUA + "Pain Maker"),
    PERSUADER(AQUA + "The Persuader"),
    RAPIER(AQUA + "Rapier"),
    SAW(AQUA + "Saw"),
    SCIMITAR(AQUA + "Scimitar"),
    SHORTSWORD(AQUA + "Short sword"),
    STING(AQUA + "Sting"),
    TEAR_OF_LUCIFER(RED + "Tear of Lucifer"),
    VORPAL(RED + "Nether Vorpal");



    private final String name;
    private static Map<Integer, Sword> swords = Maps.newHashMap();

    static{
        for(Sword sword: values()){
            swords.put(sword.ordinal(), sword);
        }
    }

    private Sword(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static Sword getRandom()
    {
        return swords.get(new Random().nextInt(Sword.values().length - 1));
    }
}