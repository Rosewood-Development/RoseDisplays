package dev.rosewood.rosedisplays.hologram.type;

import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.view.DirtyingHologramPropertyView;
import dev.rosewood.rosedisplays.hologram.view.HologramPropertyView;
import dev.rosewood.rosedisplays.hologram.view.OverrideHologramPropertyView;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Location;
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
    public void update(Location location, Set<Player> players) {
        String textPropertyValue = this.properties.get(HologramProperties.TEXT);
        boolean changed = false;
        if (textPropertyValue != null) {
            String textValue = HexUtils.colorify(textPropertyValue);
            if (!Objects.equals(this.lastText, textValue)) {
                changed = true;
                this.lastText = textValue;
            }
        }

        // Update dirty line properties and clean
        if (this.properties.isDirty() || changed) {
            if (!players.isEmpty()) {
                for (Player player : players) {
                    HologramPropertyView properties;
                    if (textPropertyValue != null) {
                        String textValue = HexUtils.colorify(PlaceholderAPIHook.applyPlaceholders(player, textPropertyValue));
                        properties = new OverrideHologramPropertyView(this.properties.getDirty());
                        properties.set(HologramProperties.TEXT, textValue);
                    } else {
                        properties = this.properties.getDirty();
                    }
                    this.nmsHandler.sendEntityMetadataPacket(this.entityId, properties, List.of(player));
                }
            }
            this.properties.clean();
        }
    }

}
