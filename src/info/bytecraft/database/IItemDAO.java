package info.bytecraft.database;

import org.bukkit.inventory.ItemStack;

public interface IItemDAO
{
    public long getValue(ItemStack stack) throws DAOException;
}
