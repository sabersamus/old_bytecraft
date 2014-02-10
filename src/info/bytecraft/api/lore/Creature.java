package info.bytecraft.api.lore;

import java.util.Map;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Maps;

public enum Creature
{
    SKELETON(EntityType.SKELETON, 1),
    BAT(EntityType.BAT, 1),
    SPIDER(EntityType.SPIDER, 1),
    ZOMBIE(EntityType.ZOMBIE, 1),
    ENDERMAN(EntityType.ENDERMAN, 2),
    CREEPER(EntityType.CREEPER, 2),
    GHAST(EntityType.GHAST, 2),
    WITCH(EntityType.WITCH, 2),
    BLAZE(EntityType.BLAZE, 2),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, 2),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 3),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, 3),
    WITHER(EntityType.WITHER, 10),
    ENDER_DRAGON(EntityType.ENDER_DRAGON, 15);//hell yeah
    
    
    private final EntityType type;
    private final int chance;
    private static final Map<EntityType, Creature> byType = Maps.newHashMap();
    
    static{
        for(Creature creature: values()){
            byType.put(creature.getType(), creature);
        }
    }
    
    private Creature(EntityType type, int chancetoDrop)
    {
        this.type = type;
        this.chance = chancetoDrop;
    }

    public EntityType getType()
    {
        return type;
    }

    public int getChance()
    {
        return chance;
    }
    
    public static Creature getByType(EntityType type)
    {
        return byType.get(type);
    }
}
