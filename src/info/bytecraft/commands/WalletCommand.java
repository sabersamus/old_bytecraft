package info.bytecraft.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBLogDAO;
import info.bytecraft.database.DBPlayerDAO;

public class WalletCommand extends AbstractCommand
{

    public WalletCommand(Bytecraft instance)
    {
        super(instance, "wallet");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("balance")) {
                player.sendMessage(ChatColor.AQUA + "You have "
                        + player.getFormattedBalance());
            }
            else if (args[0].equalsIgnoreCase("tell")) {
                Bukkit.broadcastMessage(player.getDisplayName()
                        + ChatColor.AQUA + " has "
                        + player.getFormattedBalance());
            }
        }
        else if (args.length == 3) {
            if ("give".equalsIgnoreCase(args[0])) {
                long amount = 0;
                Player delegate = Bukkit.getPlayer(args[1]);
                if (delegate != null) {
                    BytecraftPlayer target = plugin.getPlayer(delegate);
                    try {
                        amount = Long.parseLong(args[2]);
                    } catch (NumberFormatException e) {
                        return true;
                    }
                    Connection conn = null;
                    try {
                        conn = ConnectionPool.getConnection();
                        DBPlayerDAO dbPlayer = new DBPlayerDAO(conn);
                        DBLogDAO dbLog = new DBLogDAO(conn);
                        if (amount > 0) {
                            if (dbPlayer.take(player, amount)) {
                                dbPlayer.give(target, amount);
                                if (!target.isInvisible()) {
                                    player.sendMessage(ChatColor.AQUA
                                            + "You gave "
                                            + target.getDisplayName() + " "
                                            + formatCurrency(amount));
                                }
                                target.sendMessage(player.getDisplayName()
                                        + ChatColor.AQUA + " gave you "
                                        + formatCurrency(amount));
                                dbLog.insertTransactionLog(player, target,
                                        amount);
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {}
                        }
                    }
                }
            }
        }
        return true;
    }

    private String formatCurrency(long amount)
    {
        NumberFormat nf = NumberFormat.getInstance();
        return ChatColor.GOLD + nf.format(amount) + ChatColor.AQUA + " bytes";
    }

}
