package dev.rosewood.rosedisplays;

import dev.rosewood.rosedisplays.display.DisplayType;
import dev.rosewood.rosedisplays.manager.ConfigurationManager;
import dev.rosewood.rosedisplays.manager.DataManager;
import dev.rosewood.rosedisplays.manager.DisplayManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosedisplays.util.TextureToColorUtil;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

/**
 * @author Esophose
 */
public class RoseDisplays extends RosePlugin {

    /**
     * The running instance of RoseDisplays on the server
     */
    private static RoseDisplays instance;

    private File fileDir;

    public static RoseDisplays getInstance() {
        return instance;
    }

    public RoseDisplays() {
        super(-1, 11043, ConfigurationManager.class, DataManager.class, LocaleManager.class, null);

        instance = this;
    }

    @Override
    public void enable() {
        this.fileDir = new File(this.getDataFolder(), "files/");
        if (!this.fileDir.exists())
            this.fileDir.mkdirs();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        super.reload();
        TextureToColorUtil.clearCache();
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                //CommandManager.class,
                DisplayManager.class
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("rd") || !(sender instanceof Player player))
            return true;

        if (args.length == 1 && args[0].equalsIgnoreCase("export")) {
            try {
                File exportFile = new File(this.getDataFolder(), "colors.txt");
                if (exportFile.exists())
                    exportFile.delete();

                exportFile.createNewFile();

                TextureToColorUtil.getClosestMaterial(0);
                sender.sendMessage("Exporting " + TextureToColorUtil.COLOR_TO_TEXTURE_MAP.size() + " color mappings");

                try (FileWriter writer = new FileWriter(exportFile)) {
                    for (int color : TextureToColorUtil.COLOR_TO_TEXTURE_MAP.keySet()) {
                        Material material = TextureToColorUtil.COLOR_TO_TEXTURE_MAP.get(color);
                        writer.write(material.name().toLowerCase() + ": #" + String.format("%06X", color) + '\n');
                    }
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            this.reload();
            sender.sendMessage("reloaded");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("must provide at least 2 arguments");
            return true;
        }

        Location location = player.getLocation();

        if (Arrays.stream(DisplayType.values()).map(Enum::name).noneMatch(x -> x.equalsIgnoreCase(args[1]))) {
            sender.sendMessage("invalid display type");
            return true;
        }

        DisplayType displayType = DisplayType.valueOf(args[1].toUpperCase());

        if (args[0].equalsIgnoreCase("screen")) {
            this.getManager(DisplayManager.class).createFromScreen(location, displayType);
            return true;
        }

        File file = new File(this.fileDir, args[0]);
        if (!file.exists()) {
            sender.sendMessage("file does not exist");
            return true;
        }

        this.getManager(DisplayManager.class).createFromFile(file, location, displayType);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("rd") || !(sender instanceof Player) || args.length > 2)
            return List.of();

        List<String> completions = new ArrayList<>();

        if (args.length < 2) {
            completions.add("reload");
            File[] filesArray = this.fileDir.listFiles();
            if (filesArray != null)
                completions.addAll(Arrays.stream(filesArray).filter(File::isFile).map(File::getName).toList());
        } else if (!args[0].equalsIgnoreCase("reload")) {
            completions.addAll(Arrays.stream(DisplayType.values()).map(Enum::name).map(String::toLowerCase).toList());
        }

        List<String> suggestions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], completions, suggestions);
        return suggestions;
    }

}
