package dev.rosewood.rosedisplays.manager;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {

        HOLOGRAM_UPDATE_FREQUENCY("hologram-update-frequency", 2, "The number of ticks between hologram updates", "Min value of 1"),
        DEFAULT_PROPERTY_VALUES("default-property-values", null, "The default values for hologram line properties"),
        DEFAULT_PROPERTY_VALUE_TEXT_UPDATE_INTERVAL("default-property-values.text_update_interval", "200ms", "The amount of time between text updates", "Useful for animations, disable with -1"),
        DEFAULT_PROPERTY_VALUE_PLACEHOLDER_UPDATE_INTERVAL("default-property-values.placeholder_update_interval", "500ms", "The amount of time between placeholder updates", "Useful for animated colors, disable with -1");

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
