package info.bytecraft.commands;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class SupportCommand extends AbstractCommand
{

    public SupportCommand(Bytecraft instance)
    {
        super(instance, "support");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length == 0) {
            return true;
        }

        String text =
                Arrays.toString(args).replace("[", "").replaceAll("[],,]", "");
        email(player, text);
        return true;
    }

    public void email(BytecraftPlayer player, String text)
    {
        final String username = plugin.getConfig().getString("support.user");
        final String password = plugin.getConfig().getString("support.pass");
        final List<?> to = plugin.getConfig().getList("support.to");
        String[] recipients = to.toArray(new String[to.size()]);

        String time = String.format("[%tm/%td/%ty - %tl:%tM:%tS] ", new Date(), new Date(), 
                        new Date(), new Date(), new Date(), new Date());
        
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session = Session.getInstance(props, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try{
            InternetAddress[] addressTo = new InternetAddress[recipients.length];
            for(int i = 0; i < recipients.length; i++)
            {
                addressTo[i] = new InternetAddress(recipients[i]);
            }
            Message message = new MimeMessage(session);
            message.setRecipients(RecipientType.TO, addressTo);
            message.setSubject("[Help Request] from " + player.getName());
            message.setText("Help request from: " + player.getName() + " at " + time + "\n\n"
                    + "Message: " + text + "\n");
            Transport.send(message);
            player.sendMessage(ChatColor.AQUA + "Help request sent, someone will be with you as soon as possible.");
        }catch(MessagingException e){
            player.sendMessage(ChatColor.RED + "Error sending message, please try again.");
            throw new RuntimeException(e);
        }
    }

}
