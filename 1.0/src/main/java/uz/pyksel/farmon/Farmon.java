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

        sendStartupMessage();
        loadMessagesFromConfig();
        startMessageBroadcaster();
    }

    @Override
    public void onDisable() {
        if (broadcastTask != null) broadcastTask.cancel();
        Bukkit.getConsoleSender().sendMessage("§c[Farmon] Plugin o‘chirildi.");
    }

    public static Farmon getInstance() {
        return instance;
    }

    private void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage("§8----------------------------------------");
        Bukkit.getConsoleSender().sendMessage("§aFarmon Plugin Yoqildi!");
        Bukkit.getConsoleSender().sendMessage("§7Muallif: §bPyksel");
        Bukkit.getConsoleSender().sendMessage("§7Versiya: §e" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§7Sayt: §dhttps://pyksel.uz");
        Bukkit.getConsoleSender().sendMessage("§8----------------------------------------");
    }

    private void loadMessagesFromConfig() {
        FileConfiguration config = getConfig();

        // Xabarlar
        messages = config.getStringList("messages");
        if (messages == null || messages.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§c[Farmon] config.yml da 'messages' topilmadi yoki bo‘sh.");
        }

        // Interval
        String intervalStr = config.getString("interval");
        try {
            interval = Integer.parseInt(intervalStr);
            if (interval <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c[Farmon] 'interval' noto‘g‘ri yoki musbat raqam emas. Default: 300s");
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

    // /farmon reload komandasi
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("farmon")) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadMessagesFromConfig();
            startMessageBroadcaster();
            sender.sendMessage("§a[Farmon] Konfiguratsiya qayta yuklandi!");
        } else {
            sender.sendMessage("§eFarmon plugin komandasi:");
            sender.sendMessage("§6/farmon reload §7- config.yml ni qayta yuklaydi");
        }
        return true;
    }
}
