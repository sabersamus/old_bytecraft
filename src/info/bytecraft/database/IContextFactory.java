package info.bytecraft.database;

public interface IContextFactory
{
    public IContext createContext() throws DAOException;
}
