package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;
import dev.rosewood.rosedisplays.hologram.type.DisplayEntityHologram;
import dev.rosewood.rosedisplays.model.BillboardConstraint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

public record HologramType(String name, HologramPropertyTag tag, HologramCreator creator, HologramDeserializer deserializer) {

    private static final Map<String, HologramType> REGISTRY = new HashMap<>();

    public static final HologramType TEXT_DISPLAY_ENTITY = create("text_display_entity", HologramPropertyTag.TEXT_DISPLAY_ENTITY, type -> {
        Hologram hologram = new DisplayEntityHologram(type);
        hologram.getProperties().set(HologramProperties.TEXT, "New Hologram");
        hologram.getProperties().set(HologramProperties.BILLBOARD_CONSTRAINT, BillboardConstraint.CENTER);
        return hologram;
    }, DisplayEntityHologram::new);
    public static final HologramType ITEM_DISPLAY_ENTITY = create("item_display_entity", HologramPropertyTag.ITEM_DISPLAY_ENTITY, type -> {
        Hologram hologram = new DisplayEntityHologram(type);
        hologram.getProperties().set(HologramProperties.ITEM, new ItemStack(Material.DIAMOND));
        hologram.getProperties().set(HologramProperties.BILLBOARD_CONSTRAINT, BillboardConstraint.CENTER);
        return hologram;
    }, DisplayEntityHologram::new);
    public static final HologramType BLOCK_DISPLAY_ENTITY = create("block_display_entity", HologramPropertyTag.BLOCK_DISPLAY_ENTITY, type -> {
        Hologram hologram = new DisplayEntityHologram(type);
        hologram.getProperties().set(HologramProperties.BLOCK_DATA, Material.GRASS_BLOCK.createBlockData());
        hologram.getProperties().set(HologramProperties.BILLBOARD_CONSTRAINT, BillboardConstraint.CENTER);
        return hologram;
    }, DisplayEntityHologram::new);

    private static HologramType create(String name, HologramPropertyTag tag, HologramCreator creator, HologramDeserializer deserializer) {
        HologramType type = new HologramType(name, tag, creator, deserializer);
        REGISTRY.put(name, type);
        return type;
    }

    public static Map<String, HologramType> getRegistry() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    public Hologram create() {
        return this.creator().create(this);
    }

    public Hologram deserialize(HologramType type, HologramPropertyContainer properties, PersistentDataContainer container, PersistentDataAdapterContext context) {
        return this.deserializer().deserialize(type, properties, container, context);
    }

    @FunctionalInterface
    public interface HologramCreator {
        Hologram create(HologramType type);
    }

    @FunctionalInterface
    public interface HologramDeserializer {
        Hologram deserialize(HologramType type, HologramPropertyContainer properties, PersistentDataContainer container, PersistentDataAdapterContext context);
    }

}
