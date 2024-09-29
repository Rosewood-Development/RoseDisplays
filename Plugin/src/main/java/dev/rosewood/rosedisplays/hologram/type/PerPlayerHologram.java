package dev.rosewood.rosedisplays.hologram.type;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.view.DirtyingHologramPropertyView;
import dev.rosewood.rosedisplays.hologram.view.HologramPropertyView;
import dev.rosewood.rosedisplays.hologram.view.OverrideHologramPropertyView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

public abstract class PerPlayerHologram extends Hologram {

    private final Map<UUID, OverrideHologramPropertyView> perPlayerProperties;
    private Location lastLocation;

    public PerPlayerHologram(HologramType type) {
        super(type);
        this.perPlayerProperties = new HashMap<>();
    }

    public PerPlayerHologram(HologramType type, DirtyingHologramPropertyView properties, PersistentDataContainer container, PersistentDataAdapterContext context) {
        super(type, properties, container, context);
        this.perPlayerProperties = new HashMap<>();
    }

    @Override
    public final void update(Location location, Set<Player> players) {
        if (!location.equals(this.lastLocation))
            this.onLocationChanged(this.lastLocation, location);

        this.lastLocation = location;

        for (Player player : players) {
            DirtyingHologramPropertyView view = this.perPlayerProperties.get(player.getUniqueId());
            if (view == null) {
                view = this.properties;
            } else {
                this.preRender(view, player);
            }

            if (view.isDirty()) {
                this.render(view.getDirty(), List.of(player));
                view.clean();
            }
        }

        this.properties.clean();
    }

    public void preRender(DirtyingHologramPropertyView view, Player player) {

    }

    public abstract void render(HologramPropertyView properties, List<Player> players);

    public abstract void onLocationChanged(Location oldLocation, Location newLocation);

    @Override
    public void onWatcherAdded(Location location, Player player) {
        this.perPlayerProperties.put(player.getUniqueId(), new OverrideHologramPropertyView(this.properties));
    }

    @Override
    public void onWatcherRemoved(Player player) {
        this.perPlayerProperties.remove(player.getUniqueId());
    }

}
