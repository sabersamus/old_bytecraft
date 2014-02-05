package info.bytecraft.database.db;

import java.sql.*;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

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
        
        if(stack.hasItemMeta()){
            List<String> lore = stack.getItemMeta().getLore();
            if(!lore.isEmpty()){
                for(String string: lore){
                    if(ChatColor.stripColor(string).equalsIgnoreCase("spawned")){
                        return 0;
                    }
                }
            }
        }
        
        String sql = "SELECT * FROM item WHERE item_type = ? AND item_data = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, stack.getType().name().toLowerCase());
            stm.setInt(2, stack.getData().getData());
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

}
