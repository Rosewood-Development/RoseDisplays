package dev.rosewood.rosedisplays;

import dev.rosewood.rosedisplays.config.SettingKey;
import dev.rosewood.rosedisplays.manager.CommandManager;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosedisplays.manager.RegistryManager;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.List;
import org.bukkit.Bukkit;

/**
 * @author Esophose
 */
public class RoseDisplays extends RosePlugin {

    /**
     * The running instance of RoseDisplays on the server
     */
    private static RoseDisplays instance;

    public static RoseDisplays getInstance() {
        return instance;
    }

    public RoseDisplays() {
        super(-1, 11043, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (!NMSAdapter.isValidVersion()) {
            this.getLogger().severe("RoseDisplays does not support your server version. The plugin has been disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                HologramManager.class,
                RegistryManager.class
        );
    }

    @Override
    protected List<RoseSetting<?>> getRoseConfigSettings() {
        return SettingKey.getKeys();
    }

    @Override
    protected String[] getRoseConfigHeader() {
        return new String[] {
                "     __________                    ________   __                __",
                "     \\______   \\ ____  ______ ____ \\______ \\ |__| ____________ |  | _____  ___ __  ______",
                "      |       _//  _ \\/  ___// __ \\ |    |  \\|  |/  ___/\\____ \\|  | \\__  \\<   |  |/  ___/",
                "      |    |   (  <_> )___ \\\\  ___/ |    `   \\  |\\___ \\ |  |_> >  |__/ __ \\\\___  |\\___ \\",
                "      |____|_  /\\____/____  >\\___  >_______  /__/____  >|   __/|____(____  / ____/____  >",
                "             \\/           \\/     \\/        \\/        \\/ |__|             \\/\\/         \\/"
        };
    }

}
