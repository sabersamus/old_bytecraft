package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.PlayerReport;

import java.util.List;

public interface IReportDAO
{
    public List<PlayerReport> getReports(BytecraftPlayer player) throws DAOException;
    public void insertReport(PlayerReport report) throws DAOException;
}
