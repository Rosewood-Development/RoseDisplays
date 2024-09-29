package dev.rosewood.rosedisplays.config;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.*;
import static dev.rosewood.rosedisplays.config.DisplaysRoseSettingSerializers.*;

public final class SettingKey {

    private static final String TIME_STRING_EXAMPLE = "Example: '200ms'. Supported time units: 'ms', 's', 'm', 'h', 'd'";
    private static final String DEFAULT_KEY = "default-property-values";
    private static final Map<HologramProperty<?>, RoseSetting<?>> DEFAULT_PROPERTY_VALUES_MAP = new HashMap<>();
    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<Long> HOLOGRAM_UPDATE_FREQUENCY = create("hologram-update-frequency", LONG, 2L, "The number of ticks between hologram updates", "Min value of 1");
    public static final RoseSetting<CommentedConfigurationSection> DEFAULT_PROPERTY_VALUES = create(DEFAULT_KEY, "The default values for hologram line properties");
    public static final RoseSetting<Long> DEFAULT_PROPERTY_VALUE_UPDATE_INTERVAL = create(HologramProperties.UPDATE_INTERVAL, DURATION, 50L, "The amount of time to wait before updating the hologram again, formatted as a time string", TIME_STRING_EXAMPLE);
    public static final RoseSetting<Integer> DEFAULT_PROPERTY_VALUE_RENDER_DISTANCE = create(HologramProperties.RENDER_DISTANCE, INTEGER, 64, "The maximum distance away any hologram in the group can be viewed from the group origin");
    public static final RoseSetting<Long> DEFAULT_PROPERTY_VALUE_PLACEHOLDER_UPDATE_INTERVAL = create(HologramProperties.PLACEHOLDER_UPDATE_INTERVAL, DURATION, 500L, "The amount of time between placeholder updates", "Useful for animated colors, formatted as a time string", TIME_STRING_EXAMPLE);

    private static <T> RoseSetting<T> create(HologramProperty<T> property, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        String key = DEFAULT_KEY + "." + property.key();
        RoseSetting<T> setting = RoseSetting.backed(RoseDisplays.getInstance(), key, serializer, defaultValue, comments);
        DEFAULT_PROPERTY_VALUES_MAP.put(property, setting);
        KEYS.add(setting);
        return setting;
    }

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

    @SuppressWarnings("unchecked")
    public static <T> T getDefault(HologramProperty<T> property) {
        RoseSetting<T> setting = (RoseSetting<T>) DEFAULT_PROPERTY_VALUES_MAP.get(property);
        if (setting == null)
            return null;
        return setting.get();
    }

    private SettingKey() {}

}
