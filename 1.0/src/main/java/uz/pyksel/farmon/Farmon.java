package uz.pyksel.farmon;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Farmon extends JavaPlugin {
    List<String> m;
    int i=0, interval;
    BukkitTask t;
    String lang;
    List<Sound> s=Arrays.asList(Sound.BLOCK_NOTE_BLOCK_PLING,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,Sound.BLOCK_ANVIL_LAND,Sound.ENTITY_PLAYER_LEVELUP);
    Random r=new Random();

    public void onEnable(){
        saveDefaultConfig();
        lang=getConfig().getString("language","english").toLowerCase();
        log(lang.equals("uzbek")?"Farmon yoqildi!":"Farmon enabled!");
        load();
        start();
    }
    public void onDisable(){
        if(t!=null)t.cancel();
        log(lang.equals("uzbek")?"Plugin o‘chirildi.":"Plugin disabled.");
    }
    void log(String str){
        Bukkit.getConsoleSender().sendMessage("§a"+str+" §7v"+getDescription().getVersion());
    }
    void load(){
        FileConfiguration c=getConfig();
        m=c.getStringList("messages");
        try{
            interval=Integer.parseInt(c.getString("interval"));
            if(interval<=0)interval=300;
        }catch(Exception e){interval=300;}
    }
    void start(){
        if(m==null||m.isEmpty())return;
        if(t!=null)t.cancel();
        t=Bukkit.getScheduler().runTaskTimer(this,()->{
            if(i>=m.size())i=0;
            String msg=ChatColor.translateAlternateColorCodes('&',m.get(i++));
            Sound snd=s.get(r.nextInt(s.size()));
            for(Player p:Bukkit.getOnlinePlayers()){
                p.sendMessage(msg);
                p.playSound(p.getLocation(),snd,1,1);
            }
        },0,interval*20L);
    }
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
        if(!label.equalsIgnoreCase("farmon"))return false;
        if(args.length==1&&args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            lang=getConfig().getString("language","english").toLowerCase();
            load();
            start();
            sender.sendMessage(lang.equals("uzbek")?"§a[Farmon] Qayta yuklandi!":"§a[Farmon] Reloaded!");
        } else sender.sendMessage(lang.equals("uzbek")?"§e/farmon reload":"§e/farmon reload");
        return true;
    }
}
