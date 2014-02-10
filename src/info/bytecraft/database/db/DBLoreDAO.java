package info.bytecraft.database.db;

import info.bytecraft.database.DAOException;
import info.bytecraft.database.ILoreDAO;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DBLoreDAO implements ILoreDAO
{
    
    private Connection conn;
    
    public DBLoreDAO(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public List<String> getSwordNames() throws DAOException
    {
        String sql = "SELECT * FROM lore_sword";
        List<String> swords = Lists.newArrayList();
        
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    String sword = ChatColor.translateAlternateColorCodes('&', rs.getString("name"));
                    List<String> gods = this.getGodNames();
                    sword = String.format(sword, gods.get(new Random().nextInt(gods.size() - 1)));
                    swords.add(sword);
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return swords;
    }

    @Override
    public List<String> getArmorNames(String type) throws DAOException
    {
        String sql = "SELECT * FROM lore_armor WHERE type = ?";
        List<String> armors = Lists.newArrayList();
        
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, type);
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    String armor = ChatColor.translateAlternateColorCodes('&', rs.getString("name"));
                    List<String> gods = this.getGodNames();
                    armor = String.format(armor, gods.get(new Random().nextInt(gods.size() - 1)));
                    armors.add(armor);
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return armors;
    }

    @Override
    public List<String> getGodNames() throws DAOException
    {
        String sql = "SELECT * FROM lore_god";
        List<String> gods = Lists.newArrayList();
        
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    gods.add(ChatColor.translateAlternateColorCodes('&', rs.getString("name")));
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return gods;
    }

    @Override
    public Map<Integer, String> getBookPages(String name) throws DAOException
    {
        Map<Integer, String> pages = Maps.newHashMap();
        String sql = "SELECT * FROM book_pages WHERE book_name = ?";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, name);
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    int pageIndex = rs.getInt("page_index");
                    String pageText = rs.getString("page_text");
                    
                    pages.put(pageIndex, pageText);
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return pages;
    }

    @Override
    public List<String> getBookNames() throws DAOException
    {
        String sql = "SELECT * FROM book";
        List<String> books = Lists.newArrayList();
        
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    books.add(ChatColor.translateAlternateColorCodes('&', rs.getString("name")));
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return books;
    }

    @Override
    public List<String> getBookLore() throws DAOException
    {
        String sql = "SELECT * FROM lore_book";
        List<String> books = Lists.newArrayList();
        
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.execute();
            
            try(ResultSet rs = stm.getResultSet()){
                while(rs.next()){
                    books.add(ChatColor.translateAlternateColorCodes('&', rs.getString("lore")));
                }
            }
            
        }catch(SQLException e){
            throw new DAOException(sql, e);
        }
        
        return books;
    }

}
