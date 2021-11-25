package dev.rosewood.rosedisplays.manager;

import com.google.common.io.Files;
import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.data.FileDataSource;
import dev.rosewood.rosedisplays.data.GifDataSource;
import dev.rosewood.rosedisplays.data.ImageDataSource;
import dev.rosewood.rosedisplays.data.ScreenCaptureDataSource;
import dev.rosewood.rosedisplays.display.ChatDisplay;
import dev.rosewood.rosedisplays.display.Display;
import dev.rosewood.rosedisplays.display.DisplayType;
import dev.rosewood.rosedisplays.display.HologramDisplay;
import dev.rosewood.rosedisplays.display.ParticleDisplay;
import dev.rosewood.rosedisplays.display.ScoreboardDisplay;
import dev.rosewood.rosedisplays.manager.ConfigurationManager.Setting;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class DisplayManager extends Manager implements Listener {

    private final Map<Display, Thread> activeDisplays;
    private final Map<String, Class<? extends FileDataSource>> fileDataSourceMap;
    private final Map<DisplayType, Class<? extends Display>> displayTypeMap;

    private BukkitTask displayTask;

    public DisplayManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.activeDisplays = new ConcurrentHashMap<>();
        this.fileDataSourceMap = new HashMap<>();
        this.displayTypeMap = new HashMap<>();

        // Populate file data source map
        Arrays.asList("png", "jpg", "jpeg", "bmp", "wbmp").forEach(x -> this.fileDataSourceMap.put(x, ImageDataSource.class));
        this.fileDataSourceMap.put("gif", GifDataSource.class);

        // Populate display renderer map
        this.displayTypeMap.put(DisplayType.HOLOGRAM, HologramDisplay.class);
        this.displayTypeMap.put(DisplayType.PARTICLE, ParticleDisplay.class);
        this.displayTypeMap.put(DisplayType.SCOREBOARD, ScoreboardDisplay.class);
        //this.displayTypeMap.put(DisplayType.DEBUG, DebugDisplay.class);
        this.displayTypeMap.put(DisplayType.CHAT, ChatDisplay.class);

        Bukkit.getPluginManager().registerEvents(this, this.rosePlugin);
    }

    @Override
    public void reload() {
        this.disable();

        this.displayTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.rosePlugin, this::checkPlayerDistances, 0L, 10L);
    }

    @Override
    public void disable() {
        if (this.displayTask != null) {
            this.displayTask.cancel();
            this.displayTask = null;
        }

        this.activeDisplays.keySet().forEach(Display::destroy);
        this.activeDisplays.clear();
    }

    // TODO: Track player distance from displays, use a runnable for this since the player move event is absurd
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(this.rosePlugin, () ->
                this.activeDisplays.keySet().forEach(x -> x.addViewer(event.getPlayer())));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTask(this.rosePlugin, () ->
                this.activeDisplays.keySet().forEach(x -> x.removeViewer(event.getPlayer())));
    }

    private void checkPlayerDistances() {

    }

    // TODO: async handling
    public void createFromFile(File file, Location location, DisplayType displayType) {
        try {
            DataSource dataSource = this.createFileDataSource(file);
            this.createDisplay(displayType, dataSource, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFromScreen(Location location, DisplayType displayType) {
        DataSource dataSource = new ScreenCaptureDataSource();
        this.createDisplay(displayType, dataSource, location);
    }

    private FileDataSource createFileDataSource(File file) throws ReflectiveOperationException {
        String extension = Files.getFileExtension(file.getAbsolutePath()).toLowerCase();
        Class<? extends FileDataSource> dataSourceClass = this.fileDataSourceMap.get(extension);
        return dataSourceClass.getConstructor(File.class).newInstance(file);
    }

    private void createDisplay(DisplayType displayType, DataSource dataSource, Location location) {
        Bukkit.getScheduler().runTask(this.rosePlugin, () -> {
            try {
                int maxSize = Setting.MAX_DISPLAY_SIZE.getInt();
                dataSource.loadData(Math.min(displayType.getMaxWidth(), maxSize), Math.min(displayType.getMaxHeight(), maxSize));

                Display display = this.displayTypeMap.get(displayType).getConstructor(DataSource.class, Location.class).newInstance(dataSource, location);
                Thread displayThread = new Thread(display);
                displayThread.start();
                this.activeDisplays.put(display, displayThread);
                display.addViewers(Bukkit.getOnlinePlayers());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
