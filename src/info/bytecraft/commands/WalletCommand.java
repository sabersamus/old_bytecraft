package info.bytecraft.commands;

import java.text.NumberFormat;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.BytecraftPlayer.Flag;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IPlayerDAO;

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
                Bukkit.broadcastMessage(player.getTemporaryChatName()
                        + ChatColor.AQUA + " has "
                        + player.getFormattedBalance());
            }
        }
        else if (args.length == 3) {
            if ("give".equalsIgnoreCase(args[0])) {
                long amount = 0;
                List<BytecraftPlayer> cantidates = plugin.matchPlayer(args[1]);
                if(cantidates.size() != 1){
                    return true;
                }
                
                BytecraftPlayer target = cantidates.get(0);
                    try {
                        amount = Long.parseLong(args[2]);
                    } catch (NumberFormatException e) {
                        return true;
                    }
                    try (IContext ctx = plugin.createContext()){
                        IPlayerDAO dbPlayer = ctx.getPlayerDAO();
                        ILogDAO dbLog = ctx.getLogDAO();
                        if (amount > 0) {
                            if (dbPlayer.take(player, amount)) {
                                dbPlayer.give(target, amount);
                                if (!target.hasFlag(Flag.INVISIBLE)) {
                                    player.sendMessage(ChatColor.AQUA
                                            + "You gave "
                                            + target.getTemporaryChatName() + " "
                                            + formatCurrency(amount));
                                }
                                target.sendMessage(player.getTemporaryChatName()
                                        + ChatColor.AQUA + " gave you "
                                        + formatCurrency(amount));
                                dbLog.insertTransactionLog(player.getName(), target,
                                        amount);
                            }
                        }
                    } catch (DAOException e) {
                        throw new RuntimeException(e);
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
