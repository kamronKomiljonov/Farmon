package uz.pyksel.farmon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Farmon extends JavaPlugin {

    private static Farmon instance;
    private List<String> messages;
    private int interval;
    private int currentIndex = 0;
    private BukkitTask broadcastTask;
    private String language;

    private final List<Sound> soundList = Arrays.asList(
            Sound.BLOCK_NOTE_BLOCK_PLING,
            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
            Sound.BLOCK_ANVIL_LAND,
            Sound.ENTITY_PLAYER_LEVELUP
    );

    private final Random random = new Random();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadLanguage();
        sendStartupMessage();
        loadMessagesFromConfig();
        startMessageBroadcaster();
    }

    @Override
    public void onDisable() {
        if (broadcastTask != null) broadcastTask.cancel();
        if (language.equalsIgnoreCase("uzbek")) {
            Bukkit.getConsoleSender().sendMessage("§c[Farmon] Plugin o‘chirildi.");
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[Farmon] Plugin disabled.");
        }
    }

    public static Farmon getInstance() {
        return instance;
    }

    private void loadLanguage() {
        language = getConfig().getString("language", "english").toLowerCase();
    }

    private void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage("§8----------------------------------------");
        if (language.equalsIgnoreCase("uzbek")) {
            Bukkit.getConsoleSender().sendMessage("§aFarmon plagin yoqildi!");
            Bukkit.getConsoleSender().sendMessage("§7Muallif: §bPyksel");
            Bukkit.getConsoleSender().sendMessage("§7Versiya: §e" + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("§7Sayt: §dhttps://pyksel.uz");
        } else {
            Bukkit.getConsoleSender().sendMessage("§aFarmon plugin enabled!");
            Bukkit.getConsoleSender().sendMessage("§7Author: §bPyksel");
            Bukkit.getConsoleSender().sendMessage("§7Version: §e" + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("§7Website: §dhttps://pyksel.uz");
        }
        Bukkit.getConsoleSender().sendMessage("§8----------------------------------------");
    }

    private void loadMessagesFromConfig() {
        FileConfiguration config = getConfig();
        messages = config.getStringList("messages");
        if (messages == null || messages.isEmpty()) {
            if (language.equalsIgnoreCase("uzbek")) {
                Bukkit.getConsoleSender().sendMessage("§c[Farmon] config.yml da 'messages' topilmadi yoki bo‘sh.");
            } else {
                Bukkit.getConsoleSender().sendMessage("§c[Farmon] 'messages' not found or empty in config.yml.");
            }
        }

        String intervalStr = config.getString("interval");
        try {
            interval = Integer.parseInt(intervalStr);
            if (interval <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            if (language.equalsIgnoreCase("uzbek")) {
                Bukkit.getConsoleSender().sendMessage("§c[Farmon] 'interval' noto‘g‘ri yoki musbat raqam emas. Default: 300s");
            } else {
                Bukkit.getConsoleSender().sendMessage("§c[Farmon] 'interval' is invalid or not positive. Default: 300s");
            }
            interval = 300;
        }
    }

    private void startMessageBroadcaster() {
        if (messages == null || messages.isEmpty()) return;
        if (broadcastTask != null) broadcastTask.cancel();

        broadcastTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (currentIndex >= messages.size()) currentIndex = 0;

            String rawMessage = messages.get(currentIndex++);
            String formatted = ChatColor.translateAlternateColorCodes('&', rawMessage);

            Sound sound = soundList.get(random.nextInt(soundList.size()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(formatted);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }

        }, 0L, interval * 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("farmon")) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadLanguage();
            loadMessagesFromConfig();
            startMessageBroadcaster();
            if (language.equalsIgnoreCase("uzbek")) {
                sender.sendMessage("§a[Farmon] Konfiguratsiya qayta yuklandi!");
            } else {
                sender.sendMessage("§a[Farmon] Configuration reloaded!");
            }
        } else {
            if (language.equalsIgnoreCase("uzbek")) {
                sender.sendMessage("§eFarmon plugin komandasi:");
                sender.sendMessage("§6/farmon reload §7- config.yml ni qayta yuklaydi");
            } else {
                sender.sendMessage("§eFarmon plugin commands:");
                sender.sendMessage("§6/farmon reload §7- reloads config.yml");
            }
        }
        return true;
    }
}
