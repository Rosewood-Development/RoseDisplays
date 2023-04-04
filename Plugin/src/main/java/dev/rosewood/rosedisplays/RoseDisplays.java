package dev.rosewood.rosedisplays;

import dev.rosewood.rosedisplays.manager.CommandManager;
import dev.rosewood.rosedisplays.manager.ConfigurationManager;
import dev.rosewood.rosedisplays.manager.HologramManager;
import dev.rosewood.rosedisplays.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import java.util.List;

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
        super(-1, 11043, ConfigurationManager.class, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                HologramManager.class
        );
    }

}
