package info.bytecraft.database.db;

import java.sql.*;

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
    public long getValue(ItemStack stack) throws DAOException
    {
        String sql = "SELECT * FROM item WHERE item_type = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, stack.getType().name().toLowerCase());
            stm.execute();
            try(ResultSet rs = stm.getResultSet()){
                if(rs.next()){
                    return rs.getLong("item_value");
                }
            }
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return 0;
    }

}
