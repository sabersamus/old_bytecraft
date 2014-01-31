package info.bytecraft.api;

import java.util.Date;

public class InventoryAccess
{
    private int inventoryId;
    private String playerName;
    private Date timestamp;

    public InventoryAccess()
    {
    }

    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int v) { this.inventoryId = v; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date v) { this.timestamp = v; }
}
