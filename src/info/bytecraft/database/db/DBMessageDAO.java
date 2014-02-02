package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;

import info.bytecraft.database.DAOException;
import info.bytecraft.database.IMessageDAO;


public class DBMessageDAO implements IMessageDAO
{
    private Connection conn;

    public DBMessageDAO(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public List<String> loadDeathMessages() throws DAOException
    {
        String sql = "SELECT * FROM messages WHERE type = ?";
        List<String> messages = Lists.newArrayList();
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, "death");
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    messages.add(rs.getString("message"));
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return messages;
    }

    @Override
    public List<String> loadQuitMessages() throws DAOException
    {
        String sql = "SELECT * FROM messages WHERE type = ?";
        List<String> messages = Lists.newArrayList();
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, "quit");
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    messages.add(rs.getString("message"));
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        return messages;
    }
    

}
