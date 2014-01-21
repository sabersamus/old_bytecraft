package info.bytecraft.api;

import org.bukkit.Sound;

/**
 * Represents a notification to a player
 * Sent at different occasions
 * @author Robert Catron
 */
public enum Notification {
    NONE(null),
    MESSAGE(Sound.LEVEL_UP),
    BLESS(Sound.ENDERDRAGON_WINGS),
    SERVER_MESSAGE(Sound.AMBIENCE_THUNDER),
    SUMMONED(Sound.ENDERMAN_TELEPORT),
    RARE_DROP(Sound.ITEM_PICKUP),
    COMMAND_FAIL(Sound.CLICK);
    
    private final Sound sound;
    
    private Notification(Sound sound)
    {
        this.sound = sound;
    }

    /**
     * @return The sound of the notification
     */
    public Sound getSound()
    {
        return sound;
    }
}
