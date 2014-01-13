package info.bytecraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.blockfill.Fill;
import info.bytecraft.blockfill.Fill.Action;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.ILogDAO;

@SuppressWarnings("deprecation")
public class FillCommand extends AbstractCommand
{

    public FillCommand(Bytecraft instance)
    {
        super(instance, "fill");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (!player.canFill())
            return true;
        if (args.length > 0) {
            // fill <type>
            // fill undo
            // fill <type> <newType>
            // fill <type> <newType> <data>
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("undo")) {
                    if (player.getLastFill() != null) {
                        player.sendMessage(ChatColor.YELLOW
                                + "You undid your last fill: Total volume "
                                + ChatColor.GOLD + player.getLastFill().undo());
                        try (IContext ctx = Bytecraft.createContext()){
                            ILogDAO dao = ctx.getLogDAO();
                            dao.insertFillLog(player, player.getLastFill(), player.getLastFill().getMaterial(), Action.UNDO);
                        }catch(DAOException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
                else {
                    Material mat;
                    try {
                        mat = Material.getMaterial(Integer.parseInt(args[0]));
                    } catch (NumberFormatException e) {
                        mat = Material.getMaterial(args[0].toUpperCase());
                    }
                    if (mat != null) {
                        if (player.getFillBlock1() != null
                                && player.getFillBlock2() != null) {
                            Fill fill =
                                    new Fill(player, player.getFillBlock1(),
                                            player.getFillBlock2(), mat);
                            player.sendMessage(ChatColor.DARK_AQUA
                                    + "You filled "
                                    + ChatColor.GOLD
                                    + fill.fill()
                                    + ChatColor.DARK_AQUA
                                    + " blocks to "
                                    + mat.name().toLowerCase()
                                            .replace("_", " "));
                            try (IContext ctx = Bytecraft.createContext()){
                                ILogDAO dao = ctx.getLogDAO();
                                dao.insertFillLog(player, player.getLastFill(), mat, Action.FILL);
                            }catch(DAOException e){
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
            else if (args.length == 2) {
                Material from, to = null;
                try {
                    from = Material.getMaterial(Integer.parseInt(args[0]));
                } catch (NumberFormatException e) {
                    from = Material.getMaterial(args[0].toUpperCase());
                }
                try {
                    to = Material.getMaterial(Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    to = Material.getMaterial(args[1].toUpperCase());
                }
                if (from != null && to != null) {
                    if (player.getFillBlock1() != null
                            && player.getFillBlock2() != null) {
                        Fill fill =
                                new Fill(player, player.getFillBlock1(),
                                        player.getFillBlock2(), from);
                        player.sendMessage(ChatColor.DARK_AQUA
                                + "You replaced " + fill.replace(to)
                                + " blocks to "
                                + to.name().toLowerCase().replace("_", " "));
                        try (IContext ctx = Bytecraft.createContext()){
                            ILogDAO dao = ctx.getLogDAO();
                            dao.insertFillLog(player, player.getLastFill(), to, Action.REPLACE);
                        }catch(DAOException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return true;
    }

}
