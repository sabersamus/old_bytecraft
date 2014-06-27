package info.bytecraft.database;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.InventoryAccess;

public interface IInventoryDAO
{
    public enum InventoryType
    {
        BLOCK,
        PLAYER,
        PLAYER_ARMOR;
    };

    public enum ChangeType
    {
        ADD,
        REMOVE;
    };

    public int getInventoryId(int playerId, InventoryType type) throws DAOException;

    public int getInventoryId(Location loc) throws DAOException;

    public int insertInventory(BytecraftPlayer player,
                               Location loc,
                               InventoryType type) throws DAOException;

    public void insertAccessLog(BytecraftPlayer player,
                                int inventoryId) throws DAOException;

    public void insertChangeLog(BytecraftPlayer player,
                                int inventoryId,
                                int slot,
                                ItemStack slotContent,
                                ChangeType type) throws DAOException;

    public void insertStacks(int inventoryId,
                             ItemStack[] contents) throws DAOException;

    public ItemStack[] getStacks(int inventoryId,
                                 int size) throws DAOException;

    public List<InventoryAccess> getAccessLog(int inventoryId,
                                              int count) throws DAOException;
    
    
    public void saveInventory(BytecraftPlayer player,
                                int inventoryID,
                                String type) throws DAOException;
    
    public void loadInventory(BytecraftPlayer player,
                                int inventoryID,
                                String type) throws DAOException;
    
    public int fetchInventory(BytecraftPlayer player,
                                String inventoryName,
                                String type) throws DAOException;
    
    public void createInventory(BytecraftPlayer player,
                                String inventoryName,
                                String type) throws DAOException;

    
}
