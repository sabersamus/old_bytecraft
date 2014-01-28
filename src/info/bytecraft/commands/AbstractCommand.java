package info.bytecraft.commands;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand implements CommandExecutor
{
    protected Bytecraft plugin;
    protected String command;
    
    public AbstractCommand(Bytecraft instance){
        this.plugin = instance;
    }
    
    public AbstractCommand(Bytecraft instance, String command)
    {
        this(instance);
        this.command = command;
    }
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        return true;
    }
    
    public boolean handleOther(Server server, String[] args)
    {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
    {
        if(cs instanceof Player){
            return handlePlayer(plugin.getPlayer((Player)cs), args);
        }else{
            return handleOther(plugin.getServer(), args);
        }
    }

    public String getCommand()
    {
        return command;
    }
}
