package info.bytecraft.api;

import java.util.Date;

public class InventoryAccess
{
  private int inventoryId;
  private String playerName;
  private Date timestamp;
  
  public int getInventoryId()
  {
    return this.inventoryId;
  }
  
  public void setInventoryId(int v)
  {
    this.inventoryId = v;
  }
  
  public String getPlayerName()
  {
    return this.playerName;
  }
  
  public void setPlayerName(String name)
  {
    this.playerName = name;
  }
  
  public Date getTimestamp()
  {
    return this.timestamp;
  }
  
  public void setTimestamp(Date v)
  {
    this.timestamp = v;
  }
}
