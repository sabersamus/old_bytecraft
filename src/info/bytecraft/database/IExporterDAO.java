package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.zones.Zone;

public interface IExporterDAO
{
    public void export(BytecraftPlayer player, Zone zone, String warp) throws DAOException, Exception;
}
