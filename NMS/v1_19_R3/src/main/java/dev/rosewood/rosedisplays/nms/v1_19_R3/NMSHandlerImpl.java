package dev.rosewood.rosedisplays.nms.v1_19_R3;

import dev.rosewood.rosedisplays.hologram.HologramType;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.type.DisplayEntityHologram;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.nms.util.ReflectionUtils;
import dev.rosewood.rosedisplays.nms.v1_19_R3.mapping.HologramPropertyMappings;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandlerImpl implements NMSHandler {

    private static AtomicInteger entityIdIncrementer;
    static {
        try {
            entityIdIncrementer = (AtomicInteger) ReflectionUtils.getFieldByPositionAndType(Entity.class, 0, AtomicInteger.class).get(null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEntitySpawnPacket(Object hologramArg, Location location, Collection<Player> players) {
        DisplayEntityHologram hologram = (DisplayEntityHologram) hologramArg;
        EntityType<?> entityType = this.getEntityType(hologram.getType());
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                hologram.getEntityId(),
                UUID.randomUUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0,
                0,
                entityType,
                1,
                Vec3.ZERO,
                0
        );

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);

        List<SynchedEntityData.DataValue<?>> dataValues = HologramPropertyMappings.getInstance().createFreshDataValues(hologram.getProperties());
        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(hologram.getEntityId(), dataValues);
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(metadataPacket);
    }

    @Override
    public void sendEntityMetadataPacket(Object hologramArg, Collection<Player> players) {
        DisplayEntityHologram hologram = (DisplayEntityHologram) hologramArg;
        List<SynchedEntityData.DataValue<?>> dataValues = HologramPropertyMappings.getInstance().createDataValues(hologram.getProperties());
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(hologram.getEntityId(), dataValues);
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendEntityDespawnPacket(Object hologramArg, Collection<Player> players) {
        DisplayEntityHologram hologram = (DisplayEntityHologram) hologramArg;
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(hologram.getEntityId());
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public int getNextAvailableEntityId() {
        return entityIdIncrementer.incrementAndGet();
    }

    @Override
    public boolean isPropertyAvailable(Object propertyArg) {
        HologramProperty<?> property = (HologramProperty<?>) propertyArg;
        return HologramPropertyMappings.getInstance().isAvailable(property);
    }

    private EntityType<?> getEntityType(HologramType type) {
        if (type == HologramType.TEXT_DISPLAY_ENTITY) {
            return EntityType.TEXT_DISPLAY;
        } else if (type == HologramType.ITEM_DISPLAY_ENTITY) {
            return EntityType.ITEM_DISPLAY;
        } else if (type == HologramType.BLOCK_DISPLAY_ENTITY) {
            return EntityType.BLOCK_DISPLAY;
        } else throw new IllegalStateException("Invalid DisplayEntity type");
    }

}
