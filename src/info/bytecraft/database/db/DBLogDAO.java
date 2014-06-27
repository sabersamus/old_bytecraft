package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.google.common.collect.Lists;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PaperLog;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.ILogDAO;

public class DBLogDAO implements ILogDAO
{

    private Connection conn;
    private final SimpleDateFormat format = new SimpleDateFormat(
            "MM/dd/YY hh:mm:ss a");

    public DBLogDAO(Connection conn)
    {
        this.conn = conn;
    }

    public void insertChatMessage(BytecraftPlayer player, String channel, String message) throws DAOException
    {
        String sql =
                "INSERT INTO player_chatlog (player_name, chatlog_channel, chatlog_message) VALUES (?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, player.getName());
            stm.setString(2, channel);
            stm.setString(3, message);
            stm.execute();
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }
    
    @Override
    public void insertPrivateMessage(BytecraftPlayer player, BytecraftPlayer recipient, String message)
    throws DAOException
    {
        String sql = "INSERT INTO player_messages (player_name, recipient_name, message) VALUES (?, ?, ?)";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.setString(2, recipient.getName());
            stm.setString(3, message);
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }

    public void insertTransactionLog(String giver,
            BytecraftPlayer reciever, long amount) throws DAOException
    {
        String sql =
                "INSERT INTO transaction_log (sender_name, reciever_name, amount) VALUES (?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, giver);
            stm.setString(2, reciever.getName());
            stm.setLong(3, amount);
            stm.execute();
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    public void insertPaperLog(BytecraftPlayer player, Location loc,
            Material mat, String action) throws DAOException
    {
        String sql =
                "INSERT INTO paper_log (player_name, block_x, block_y, block_z, "
                        + "block_type, paper_time, block_world, action) "
                        + "VALUES (?, ?, ?, ?, ?, unix_timestamp(), ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, player.getName());
            stm.setInt(2, loc.getBlockX());
            stm.setInt(3, loc.getBlockY());
            stm.setInt(4, loc.getBlockZ());
            stm.setString(5, mat.name().toLowerCase());
            stm.setString(6, loc.getWorld().getName().toLowerCase());
            stm.setString(7, action);

            stm.execute();

        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }
    
    public boolean isLegal(Block block) throws DAOException
    {
        String sql =
                "SELECT * FROM paper_log WHERE block_x = ? AND block_y = ? AND block_z = ? AND block_world = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                return !rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    public List<PaperLog> getLogs(Block block) throws DAOException
    {
        List<PaperLog> logs = Lists.newArrayList();
        String sql =
                "SELECT * FROM paper_log WHERE block_x = ? AND block_y = ? AND block_z = ? AND block_world = ? "
                        + "ORDER BY paper_time DESC LIMIT 5";
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, block.getX());
            stm.setInt(2, block.getY());
            stm.setInt(3, block.getZ());
            stm.setString(4, block.getWorld().getName());
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while (rs.next()) {
                    String name = rs.getString("player_name");
                    Date date = new Date(rs.getInt("paper_time") * 1000L);
                    String action = rs.getString("action");
                    String material = rs.getString("block_type");
                    PaperLog log =
                            new PaperLog(name, format.format(date), action,
                                    material);
                    logs.add(log);
                }
            }
            Collections.reverse(logs);
            return logs;
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }
    

    @Override
    public void insertLogin(BytecraftPlayer player, String action)
            throws DAOException
    {
        String sql = "INSERT INTO player_login (player_name, login_timestamp, login_ip, action) VALUES"
                + " (?, unix_timestamp(), ?, ?)";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.setString(2, player.getIp());
            stm.setString(3, action.toLowerCase());
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
    
    @Override
    public Set<String> getAliases(BytecraftPlayer player)
    throws DAOException
    {
        String sql = "SELECT DISTINCT player_name FROM player " +
            "INNER JOIN player_login USING (player_name) " +
            "WHERE login_ip = ?";

        Set<String> aliases = new HashSet<String>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getIp());
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                while (rs.next()) {
                    aliases.add(rs.getString("player_name"));
                }
            }
        }
        catch (SQLException e) {
            throw new DAOException(sql, e);
        }

        return aliases;
    }

    @Override
    public void insertSellLog(BytecraftPlayer player, int value) throws DAOException
    {
        String sql = "INSERT INTO sell_log (player_name, sell_value) VALUES (?, ?)";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, player.getName());
            stm.setInt(2, value);
            stm.execute();
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }
}
