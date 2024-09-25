package dev.rosewood.rosedisplays.config;

import dev.rosewood.rosedisplays.util.TimeUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;

public final class DisplaysRoseSettingSerializers {

    public static final RoseSettingSerializer<Long> DURATION = new RoseSettingSerializer<>() {

        @Override
        public Long read(CommentedConfigurationSection config, String key) {
            String timeString = config.getString(key);
            return TimeUtils.getDuration(timeString);
        }

        @Override
        public void write(CommentedConfigurationSection config, String key, Long value, String... comments) {
            String timeString = TimeUtils.getTimeString(value);
            config.set(key, timeString, comments);
        }

    };

}
