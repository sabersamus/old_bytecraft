package info.bytecraft.database;

import java.util.List;

public interface IMessageDAO
{
    public List<String> loadDeathMessages() throws DAOException;
    public List<String> loadQuitMessages() throws DAOException;
}
