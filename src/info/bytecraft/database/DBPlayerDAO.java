package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Rank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DBPlayerDAO
{
    private Connection conn;

    public DBPlayerDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    @SuppressWarnings("serial")
    private static final Map<String, ChatColor> GOD_COLORS = 
        new HashMap<String, ChatColor>(){
            {
                put("red", ChatColor.RED);
                put("aqua", ChatColor.AQUA);
                put("gold", ChatColor.GOLD);
                put("yellow", ChatColor.YELLOW);
                put("dark_aqua", ChatColor.DARK_AQUA);
                put("pink", ChatColor.LIGHT_PURPLE);
                put("purple", ChatColor.DARK_PURPLE);
                put("green", ChatColor.GREEN);
                put("dark_green", ChatColor.DARK_GREEN);
                put("dark_red", ChatColor.DARK_RED);
                put("gray", ChatColor.GRAY);
            }
    };
    

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/YY hh:mm:ss a");
    
    public BytecraftPlayer getPlayer(Player player)
    {
        return getPlayer(player.getName(), player);
    }

    public BytecraftPlayer getPlayer(String name)
    {
        return getPlayer(name, null);
    }

    public BytecraftPlayer getPlayer(String name, Player wrap)
    {
        BytecraftPlayer player;
        if (wrap != null) {
            player = new BytecraftPlayer(wrap);
        }
        else {
            player = new BytecraftPlayer(name);
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt =
                    conn.prepareStatement("SELECT * FROM player "
                            + "WHERE player_name = ?");
            stmt.setString(1, name);
            stmt.execute();

            rs = stmt.getResultSet();
            if (!rs.next()) {
                return null;
            }

            player.setId(rs.getInt("player_id"));
            player.setRank(Rank.getRank(rs.getString("player_rank")));
        } catch(SQLException e){
            throw new RuntimeException(e);
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }

        loadSettings(player);
        return player;
    }

    private void loadSettings(BytecraftPlayer player)
    {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt =
                    conn.prepareStatement("SELECT * FROM player_property "
                            + "WHERE player_id = ?");
            stmt.setInt(1, player.getId());
            stmt.execute();
            
            rs = stmt.getResultSet();
            while (rs.next()) {
                String key = rs.getString("property_key");
                String value = rs.getString("property_value");
                if("tpblock".equalsIgnoreCase(key)){
                    player.setTeleportBlock(Boolean.valueOf(value));
                }else if("invisible".equalsIgnoreCase(key)){
                    player.setInvisible(Boolean.valueOf(value));
                }
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public BytecraftPlayer createPlayer(Player wrap) throws SQLException
    {
        BytecraftPlayer player = new BytecraftPlayer(wrap);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO player (player_name) VALUE (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, player.getName());
            stmt.execute();

            stmt.executeQuery("SELECT LAST_INSERT_ID()");

            rs = stmt.getResultSet();
            if (!rs.next()) {
                throw new SQLException("Failed to get player id");
            }

            player.setId(rs.getInt(1));
            player.setRank(Rank.NEWCOMER);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        updateProperty(player, "tpblock", false);
        updateProperty(player, "invisible", false);
        updateProperty(player, "god_color", "red");
        return player;
    }

    public void updatePlayerPermissions(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("UPDATE player SET player_rank = ? WHERE player_id = ?");
            stm.setString(1, player.getRank().toString().toLowerCase());
            stm.setInt(2, player.getId());
            stm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updatePlayerInfo(BytecraftPlayer player)
    {
        updateProperty(player, "invisible", player.isInvisible());
        updateProperty(player, "tpblock", player.isTeleportBlock());
    }

    public void updateProperty(BytecraftPlayer player, String key, boolean value)
    {
        updateProperty(player, key, String.valueOf(value));
    }

    public void updateProperty(BytecraftPlayer player, String key, String value)
    {
        if (value == null) {
            return;
        }

        PreparedStatement stmt = null;
        try {
            String sqlInsert =
                    "REPLACE INTO player_property (player_id, "
                            + "property_key, property_value) VALUE (?, ?, ?)";
            stmt = conn.prepareStatement(sqlInsert);
            stmt.setInt(1, player.getId());
            stmt.setString(2, key);
            stmt.setString(3, value);
            stmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public long balance(BytecraftPlayer player)
    {
        if (player == null) {
            throw new RuntimeException("Player can not be null");
        }

        PreparedStatement stm = null;
        try {
            stm =
                    conn.prepareStatement("SELECT * FROM player WHERE `player_id`=?");
            stm.setInt(1, player.getId());

            stm.execute();

            ResultSet rs = stm.getResultSet();

            if (rs.next()) {
                return rs.getInt("player_wallet");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public boolean give(BytecraftPlayer player, long toAdd)
    {
        PreparedStatement stm = null;
        try {
            stm =
                    conn.prepareStatement("UPDATE player SET player_wallet = player_wallet + ? "
                            + "WHERE player_id = ?");
            stm.setLong(1, toAdd);
            stm.setInt(2, player.getId());

            stm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean take(BytecraftPlayer player, long toTake)
    {
        if((balance(player) - toTake) < 0)return false;
        PreparedStatement stm = null;
        try {
            stm =
                    conn.prepareStatement("UPDATE player SET player_wallet = player_wallet - ? WHERE player_id = ?");
            stm.setLong(1, toTake);
            stm.setInt(2, player.getId());
            stm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public String formattedBalance(BytecraftPlayer player)
    {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return ChatColor.GOLD + nf.format(balance(player)) + ChatColor.AQUA
                + " bytes";
    }
    
    public boolean isBanned(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM player WHERE player_id = ?");
            stm.setInt(1, player.getId());
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
                return Boolean.valueOf(rs.getString("player_banned"));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
        return false;
    }
    
    public void ban(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("UPDATE player SET player_banned = true WHERE player_id = ?");
            stm.setInt(1, player.getId());
            stm.execute();
        }catch(SQLException e){
            
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
    }
    
    public ChatColor getGodColor(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM player_property WHERE player_id = ? AND property_key = ?");
            stm.setInt(1, player.getId());
            stm.setString(2, "god_color");
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
                return GOD_COLORS.get(rs.getString("property_value"));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
        return ChatColor.RED;
    }
    
    public void promoteToSettler(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("UPDATE player SET player_promoted = unix_timestamp() WHERE player_name = ?");
            stm.setString(1, player.getName());
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
    }
    
    public long getPromotedLong(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM player WHERE player_name = ?");
            stm.setString(1, player.getName());
            stm.execute();
            
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
               return rs.getLong("player_promoted");
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
        return 0;
    }
    
    public String formattedPropmotedTime(BytecraftPlayer player)
    {
        Date date = new Date(getPromotedLong(player) * 1000L);
        return format.format(date);
    }
    
    public int getPlayTime(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM player WHERE player_name = ?");
            stm.setString(1, player.getName());
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
                return rs.getInt("player_playtime");
            }else{
                return 0;
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
    }
    
    public void updatePlayTime(BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        int playTime = player.getPlayTime() + player.getOnlineTime();
        try{
            stm = conn.prepareStatement("UPDATE player SET player_playtime = ? WHERE player_name = ?");
            stm.setInt(1, playTime);
            stm.setString(2, player.getName());
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try{
                    stm.close();
                }catch(SQLException e){}
            }
        }
    }
}
