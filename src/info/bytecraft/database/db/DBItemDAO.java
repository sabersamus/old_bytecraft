package info.bytecraft.database.db;

import java.sql.*;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.bytecraft.database.DAOException;
import info.bytecraft.database.IItemDAO;

public class DBItemDAO implements IItemDAO
{
    private Connection conn;
    
    public DBItemDAO(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public int getValue(ItemStack stack) throws DAOException
    {
        
        ItemMeta meta = stack.getItemMeta();
        if (meta.hasLore()) {
            for (String string : meta.getLore()) {
                if (ChatColor.stripColor(string).equalsIgnoreCase("spawned") ||
                        ChatColor.stripColor(string).equalsIgnoreCase("creative")) {
                    return 0;
                }
            }
        }
        
        String sql = "SELECT * FROM item WHERE item_type = ? AND item_data = ?";
        int data = stack.getData().getData();
        if(stack.getDurability() < stack.getType().getMaxDurability()){
            data = 0;
        }
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, stack.getType().name().toLowerCase());
            stm.setInt(2, data);
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                if(!rs.next()){
                    return 0;
                }
                return rs.getInt("item_value");
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
    }

    @Override
    public int getEnchantValue(int id, int level) throws DAOException
    {
        String sql = "SELECT * FROM enchantment_value WHERE enchant_id = ? AND enchant_level = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, id);
            stm.setInt(2, level);
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                if(!rs.next()){
                    return 0;
                }
                return rs.getInt("enchant_value");
            }
            
        }catch(SQLException ex){
            throw new RuntimeException(sql, ex);
        }
    }

}
