package info.bytecraft.database;

import java.util.List;
import java.util.Map;

public interface ILoreDAO
{
    public List<String> getSwordNames() throws DAOException;
    public List<String> getArmorNames(String type) throws DAOException;
    public List<String> getGodNames() throws DAOException;
    
    /**
     * Gets all the pages for the selected book name.
     * @param name - The name of the book for the lore.
     * @return A HashMap, K = Page number, V = Page text
     * @throws DAOException if the database is not accessible 
     */
    public Map<Integer, String> getBookPages(String name) throws DAOException;
    
    public List<String> getBookNames() throws DAOException;
   
    public List<String> getBookLore() throws DAOException;
}
