package dev.rosewood.rosedisplays.config;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.rosewood.rosegarden.config.RoseSettingSerializers.*;

public final class SettingKey {

    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<Integer> HOLOGRAM_UPDATE_FREQUENCY = create("hologram-update-frequency", INTEGER, 2, "The number of ticks between hologram updates", "Min value of 1");
    public static final RoseSetting<Integer> HOLOGRAM_RENDER_DISTANCE = create("hologram-render-distance", INTEGER, 64, "The maximum distance away any hologram can be viewed from");
    public static final RoseSetting<CommentedConfigurationSection> DEFAULT_PROPERTY_VALUES = create("default-property-values", "The default values for hologram line properties");
    public static final RoseSetting<String> DEFAULT_PROPERTY_VALUE_TEXT_UPDATE_INTERVAL = create("default-property-values.text_update_interval", STRING, "200ms", "The amount of time between text updates", "Useful for animations, disable with -1");
    public static final RoseSetting<String> DEFAULT_PROPERTY_VALUE_PLACEHOLDER_UPDATE_INTERVAL = create("default-property-values.placeholder_update_interval", STRING, "500ms", "The amount of time between placeholder updates", "Useful for animated colors, disable with -1");

    private static <T> RoseSetting<T> create(String key, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.backed(RoseDisplays.getInstance(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<CommentedConfigurationSection> create(String key, String... comments) {
        RoseSetting<CommentedConfigurationSection> setting = RoseSetting.backedSection(RoseDisplays.getInstance(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return Collections.unmodifiableList(KEYS);
    }

    private SettingKey() {}

}
