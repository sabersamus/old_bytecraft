package info.bytecraft.api;

public class PaperLog
{
  private String playerName;
  private String date;
  private String action;
  private String material;
  
  public PaperLog(String player, String date, String action, String material)
  {
    setPlayerName(player);
    setDate(date);
    setAction(action);
    setMaterial(material);
  }
  
  public String getPlayerName()
  {
    return this.playerName;
  }
  
  public void setPlayerName(String playerName)
  {
    this.playerName = playerName;
  }
  
  public String getDate()
  {
    return this.date;
  }
  
  public void setDate(String date)
  {
    this.date = date;
  }
  
  public String getAction()
  {
    return this.action;
  }
  
  public void setAction(String action)
  {
    this.action = action;
  }
  
  public String getMaterial()
  {
    return this.material;
  }
  
  public void setMaterial(String material)
  {
    this.material = material;
  }
}
