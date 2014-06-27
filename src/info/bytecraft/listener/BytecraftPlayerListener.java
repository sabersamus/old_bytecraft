package info.bytecraft.listener;

import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.WHITE;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import com.google.common.collect.Maps;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.*;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.api.event.PlayerChangeRankEvent;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IBlessDAO;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.ZoneWorld;

public class BytecraftPlayerListener implements Listener
{
    private Bytecraft plugin;

    public BytecraftPlayerListener(Bytecraft bytecraft)
    {
        this.plugin = bytecraft;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null)
            return;
        if (!player.hasPlayedBefore()) {
            player.teleport(plugin.getWorldSpawn("world"));
        }

        if (player.getRank() == Rank.NEWCOMER) {
            player.sendMessage(ChatColor.AQUA
                    + plugin.getConfig().getString("motd.new"));
            if (!player.hasFlag(Flag.HARDWARNED)
                    && !player.hasFlag(Flag.SOFTWARNED)) {
                for (BytecraftPlayer other : plugin.getOnlinePlayers()) {
                    if (other.getRank().canMentor()) {
                        other.sendMessage(player.getTemporaryChatName()
                                + ChatColor.AQUA
                                + " has joined as a newcomer, you should help them out!");
                    }
                }
            }
        }
        else {
            player.sendMessage(ChatColor.AQUA
                    + plugin.getConfig().getString("motd.normal"));
        }

        player.setAllowFlight(player.getRank().canFly());

        if (player.getGameMode() != GameMode.SURVIVAL
                && !player.getRank().canFill()) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        World cWorld = player.getWorld();
        String[] worldNamePortions = cWorld.getName().split("_");

        if (worldNamePortions[0].equalsIgnoreCase("world")) {
            player.loadInventory("survival", false);
        }
        else {
            player.loadInventory(worldNamePortions[0], false);
        }

        List<BytecraftPlayer> players = plugin.getOnlinePlayers();
        if (player.hasFlag(Flag.INVISIBLE)) {
            player.sendMessage(ChatColor.YELLOW + "You have joined invisible");

            // Hide the new player from all existing players
            for (BytecraftPlayer current : players) {
                if (!current.getRank().canVanish()) {
                    current.hidePlayer(player.getDelegate());
                }
                else {
                    current.showPlayer(player.getDelegate());
                }
            }
        }

        for (BytecraftPlayer current : players) {
            if (current.hasFlag(Flag.INVISIBLE)) {
                player.hidePlayer(current.getDelegate());
            }
            else {
                player.showPlayer(current.getDelegate());
            }

            if (player.getRank().canVanish()) {
                player.showPlayer(current.getDelegate());
            }
        }

        if (player.isOnline()) {

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            Objective objective = board.registerNewObjective("1", "2");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.BLUE + "Welcome to Bytecraft!");

            Objective health =
                    board.registerNewObjective("showhealth", "health");
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            health.setDisplayName("/ 20");
            String desc = ChatColor.AQUA + "Your balance:";
            Score score = objective.getScore(Bukkit.getOfflinePlayer(desc));
            Score healthScore =
                    health.getScore(Bukkit.getOfflinePlayer(player.getName()));
            score.setScore((int) player.getBalance());
            healthScore.setScore((int) player.getHealth());
            try {
                player.setScoreboard(board);
                player.setHealth(player.getHealth() - 0.001);
                ScoreboardClearTask.start(plugin, player);
            } catch (IllegalStateException e) {
                // ignore
            }
        }

    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        World cWorld = player.getWorld();

        if (cWorld.getName().equalsIgnoreCase("albion")) {
            player.loadInventory("albion", true);
            return;
        }

        String[] worldNamePortions = cWorld.getName().split("_");

        if (worldNamePortions[0].equalsIgnoreCase("world")) {
            player.loadInventory("survival", true);
        }
        else {
            player.loadInventory(worldNamePortions[0], true);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        try {
            plugin.addPlayer(event.getPlayer(), event.getAddress());
        } catch (PlayerBannedException e) {
            event.disallow(Result.KICK_BANNED, e.getMessage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());

        if (player == null) {
            event.setQuitMessage(null);
            return;
        }

        player.saveInventory(player.getCurrentInventory());

        if (player.getRank().canVanish() && player.hasFlag(Flag.INVISIBLE)) {
            event.setQuitMessage(null);
            plugin.removePlayer(player);
            return;
        }

        List<String> messages = plugin.getQuitMessages();
        String mess =
                ChatColor.AQUA
                        + "- quit - "
                        + messages
                                .get(new Random().nextInt(messages.size() - 1))
                                .replace(
                                        "%s",
                                        player.getTemporaryChatName()
                                                + ChatColor.GRAY)
                                .replaceAll("(?i)&([a-f0-9])", "\u00A7$1");

        event.setQuitMessage(mess);
        plugin.removePlayer(player);
    }

    @EventHandler
    public void onFly(PlayerMoveEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null) {
            event.getPlayer().kickPlayer("Something went wrong!");
        }
        if (player.isFlying()) {
            if (player.isSprinting()) {
                if (player.getRank().canFlyFast()) {
                    player.setFlySpeed(0.4F);
                }
                else {
                    player.setFlySpeed(0.2F);
                }
            }
            else {
                player.setFlySpeed(0.2F);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        BytecraftPlayer player = plugin.getPlayer((Player) event.getEntity());

        if (event.getCause() == DamageCause.FALL) {
            event.setCancelled(false);
            return;
        }

        if (player.getRank().isImmortal()) {
            if (!player.hasFlag(Flag.IMMORTAL)) {
                return;
            }
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player) {
            BytecraftPlayer player =
                    plugin.getPlayer((Player) event.getDamager());
            if (event.getEntity() instanceof Player) {
                BytecraftPlayer victim =
                        plugin.getPlayer((Player) event.getEntity());
                Location loc = victim.getLocation();
                ZoneWorld world = plugin.getWorld(loc.getWorld());
                Zone zone = world.findZone(loc);
                if (zone == null || !zone.hasFlag(Zone.Flag.PVP)) {
                    event.setCancelled(true);
                    event.setDamage(0);
                    player.sendMessage(ChatColor.RED
                            + "You are not in a pvp zone.");
                    return;
                }
                else {
                    if (player.getRank() == Rank.NEWCOMER
                            || victim.getRank() == Rank.NEWCOMER) {
                        event.setCancelled(true);
                        event.setDamage(0);
                        player.sendMessage(ChatColor.RED
                                + "Newcomers can't use pvp!");
                        return;
                    }
                }
            }
            else {
                event.setCancelled(player.getRank() == Rank.NEWCOMER);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event)
    {
        event.setLeaveMessage(null);
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        plugin.removePlayer(player);
    }

    private HashMap<Item, BytecraftPlayer> droppedItems = Maps.newHashMap();

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        droppedItems.put(event.getItemDrop(), player);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event)
    {
        if (!droppedItems.containsKey(event.getItem()))
            return;
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        BytecraftPlayer from = droppedItems.get(event.getItem());
        ItemStack stack = event.getItem().getItemStack();

        if (from != null && (from != player)) {
            player.sendMessage(ChatColor.YELLOW + "You got " + ChatColor.GOLD
                    + stack.getAmount() + " "
                    + stack.getType().toString().toLowerCase()
                    + ChatColor.YELLOW + " from " + from.getTemporaryChatName()
                    + ".");
            from.sendMessage(ChatColor.YELLOW + "You gave "
                    + player.getTemporaryChatName() + ChatColor.GOLD + " "
                    + stack.getAmount() + " "
                    + stack.getType().name().toLowerCase().replace("_", " "));
            droppedItems.remove(event.getItem());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getEntity());
        if (player == null)
            return;
        List<String> deathMessages = plugin.getDeathMessages();
        String message = "";
        if (deathMessages.isEmpty()) {
            message =
                    ChatColor.GRAY + "- death - "
                            + player.getTemporaryChatName() + ChatColor.GRAY
                            + " has died!";
        }
        else {
            message =
                    ChatColor.GRAY
                            + "- death - "
                            + deathMessages
                                    .get(new Random().nextInt(deathMessages
                                            .size() - 1))
                                    .replace(
                                            "%s",
                                            player.getTemporaryChatName()
                                                    + ChatColor.GRAY)
                                    .replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
        }
        event.setDeathMessage(message);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        player.saveInventory(player.getCurrentInventory());
        event.setRespawnLocation(plugin.getWorldSpawn(event.getPlayer()
                .getWorld().getName()));
    }

    @EventHandler
    public void onCheck(PlayerInteractEvent event)
    {
        if (event.isCancelled())
            return;
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getItemInHand().getType() != Material.PAPER)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();
        try (IContext ctx = plugin.createContext()) {
            ILogDAO dao = ctx.getLogDAO();
            for (PaperLog log : dao.getLogs(block)) {
                BytecraftPlayer other =
                        plugin.getPlayer(log.getPlayerName());
                String name = other.getNameColor() + other.getName();
                player.sendMessage(name + " " + ChatColor.AQUA
                        + log.getAction() + " " + ChatColor.GOLD
                        + log.getMaterial() + ChatColor.GREEN + " at "
                        + log.getDate());
            }
            event.setCancelled(true);
            return;
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onCheckBlock(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getItemInHand().getType() != Material.BOOK)
            return;

        Block block = event.getClickedBlock();

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        String world = block.getWorld().getName();
        String biome =
                WordUtils.capitalize(block.getBiome().name()
                        .replaceAll("_", " "));
        BytecraftPlayer owner = null;
        int blessId = 0;
        try (IContext ctx = plugin.createContext()) {
            IBlessDAO dao = ctx.getBlessDAO();
            if (dao.isBlessed(block)) {
                owner = plugin.getPlayer(dao.getOwner(block));
                blessId = dao.getBlessId(block);
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(DARK_AQUA + "========= Block Information =========");
        player.sendMessage(DARK_AQUA + "X: " + WHITE + x);
        player.sendMessage(DARK_AQUA + "Y: " + WHITE + y);
        player.sendMessage(DARK_AQUA + "Z: " + WHITE + z);
        player.sendMessage(DARK_AQUA + "World: " + WHITE + world);
        player.sendMessage(DARK_AQUA + "Checksum: " + WHITE
                + Bytecraft.locationChecksum(block.getLocation()));

        player.sendMessage(DARK_AQUA + "Biome: " + WHITE + biome);
        if (owner != null) {
            String name = owner.getRank().getColor() + owner.getName() + WHITE;
            player.sendMessage(DARK_AQUA + "Bless ID: " + WHITE + blessId);
            player.sendMessage(DARK_AQUA + "Owner: " + name);
        }
        player.sendMessage(DARK_AQUA + "=====================");
        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        return;
    }

    @EventHandler
    public void onFlight(PlayerToggleFlightEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null)
            return;
        if (!event.isFlying()) {
            return;
        }
        if (player.getRank().canFly()) {
            if (player.hasFlag(Flag.CAN_FLY)) {
                event.setCancelled(false);
            }
            else {
                event.setCancelled(true);
            }
        }
        else {
            event.setCancelled(true);
        }

        if (player.hasFlag(Flag.SOFTWARNED) || player.hasFlag(Flag.HARDWARNED)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onCompass(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        ItemStack heldItem = player.getItemInHand();
        if (heldItem.getType() != Material.COMPASS) {
            return;
        }

        World world = player.getWorld();

        if (player.getRank().ordinal() >= Rank.LORD.ordinal()) {

            float pitch = event.getPlayer().getLocation().getPitch();
            float yaw = event.getPlayer().getLocation().getYaw();

            TargetBlock targetCalc = new TargetBlock(event.getPlayer());
            Block target = targetCalc.getTargetBlock();

            if (target != null) {

                for (int i = 0; i < 100; i++) {

                    int landingType =
                            world.getBlockAt(target.getX(), target.getY() + i,
                                    target.getZ()).getTypeId();

                    int landingAbove =
                            world.getBlockAt(target.getX(),
                                    target.getY() + i + 1, target.getZ())
                                    .getTypeId();

                    if (landingType == 0 && landingAbove == 0) {
                        Location loc = target.getLocation();

                        loc.setX(loc.getX() + .5);
                        loc.setZ(loc.getZ() + .5);
                        loc.setY(loc.getY() + i);
                        loc.setPitch(pitch);
                        loc.setYaw(yaw);
                        if (loc.getY() < 255) {
                            player.teleport(loc);
                        }
                        break;
                    }
                }
            }
        }
        else if (player.getRank() == Rank.NOBLE) {// this is very bad practice

            Block target = player.getDelegate().getTargetBlock(null, 300);
            int top = world.getHighestBlockYAt(target.getLocation());
            Location loc =
                    new Location(player.getWorld(), target.getX() + 0.5, top,
                            target.getZ() + 0.5, player.getLocation().getYaw(),
                            player.getLocation().getPitch());
            player.teleport(loc);
        }
    }

    @EventHandler
    public void onChange(PlayerGameModeChangeEvent event)
    {
        if (event.getNewGameMode() == GameMode.SURVIVAL) {
            BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
            if (player.getRank().canFly()) {
                if (player.hasFlag(Flag.CAN_FLY)) {
                    player.setAllowFlight(true);
                }
                else {
                    player.setAllowFlight(false);
                }
            }
            else {
                player.setAllowFlight(false);
            }

            if (player.hasFlag(Flag.SOFTWARNED)
                    || player.hasFlag(Flag.HARDWARNED)) {
                player.setAllowFlight(false);
            }

        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        BlockState block = event.getClickedBlock().getState();
        if (block instanceof Skull) {
            Skull skull = (Skull) block;
            if (!skull.getSkullType().equals(SkullType.PLAYER)) {
                return;
            }
            String owner = skull.getOwner();
            BytecraftPlayer skullowner = plugin.getPlayerOffline(owner);
            if (skullowner != null) {
                ChatColor C = skullowner.getNameColor();
                player.sendMessage(ChatColor.AQUA + "This is " + C + owner
                        + "'s " + ChatColor.AQUA + "head!");
            }
            else {
                player.sendMessage(ChatColor.AQUA + "This is "
                        + ChatColor.WHITE + owner + ChatColor.AQUA + "'s head!");

            }
        }
    }

    @EventHandler
    public void onRankChange(PlayerChangeRankEvent event)
    {
        BytecraftPlayer player = event.getPlayer();
        player.setTemporaryChatName(event.getNewRank().getColor()
                + player.getName());
    }

}