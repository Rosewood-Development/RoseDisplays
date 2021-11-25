package dev.rosewood.rosedisplays.manager;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        MAX_DISPLAY_SIZE("max-display-size", 128, "The max width or height a display is allowed to have", "Sizes exceeding this value will be automatically downscaled"),
        TIME_BETWEEN_UPDATES("time-between-updates", 200, "The time in milliseconds between display updates", "This is used for particle displays for static images"),
        SCREEN_CAPTURE_REFRESH_RATE("screen-capture-refresh-rate", 1000 / 20, "The refresh rate to be used for screen capturing"),
        USE_INTERLACING("use-interlacing", true, "Should interlacing be used?", "If enabled, animated displays will run much faster, but at a loss of quality");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return RoseDisplays.getInstance().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
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
