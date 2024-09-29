package dev.rosewood.rosedisplays.hologram.type;

import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.view.DirtyingHologramPropertyView;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

public class TextDisplayEntityHologram extends DisplayEntityHologram {

    private String lastText;
    private long nextPlaceholderUpdateTime;

    public TextDisplayEntityHologram(HologramType type) {
        super(type);
    }

    public TextDisplayEntityHologram(HologramType type, DirtyingHologramPropertyView properties, PersistentDataContainer container, PersistentDataAdapterContext context) {
        super(type, properties, container, context);
    }

    @Override
    public void preRender(DirtyingHologramPropertyView view, Player player) {
        String textPropertyValue = this.properties.get(HologramProperties.TEXT);
        if (textPropertyValue != null) {
            String textValue = HexUtils.colorify(textPropertyValue);
            if (Objects.equals(this.lastText, textValue))
                return;

            this.lastText = textValue;
        }

        if (textPropertyValue != null) {
            view.set(HologramProperties.TEXT, HexUtils.colorify(PlaceholderAPIHook.applyPlaceholders(player, textPropertyValue)));
        } else {
            view.unset(HologramProperties.TEXT);
        }
    }

}
