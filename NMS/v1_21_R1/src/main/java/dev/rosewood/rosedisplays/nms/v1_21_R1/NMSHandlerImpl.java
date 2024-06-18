package dev.rosewood.rosedisplays.nms.v1_21_R1;

import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.VersionAvailabilityProvider;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.nms.util.ReflectionUtils;
import dev.rosewood.rosedisplays.nms.v1_21_R1.mapping.HologramPropertyMappings;
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
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
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
    public void sendHologramSpawnPacket(HologramLine hologramLine, Collection<Player> players) {
        EntityType<?> entityType = switch (hologramLine.getType()) {
            case TEXT -> EntityType.TEXT_DISPLAY;
            case ITEM -> EntityType.ITEM_DISPLAY;
            case BLOCK -> EntityType.BLOCK_DISPLAY;
        };

        Location location = hologramLine.getLocation();
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                hologramLine.getEntityId(),
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

        List<SynchedEntityData.DataValue<?>> dataValues = HologramPropertyMappings.getInstance().createFreshDataValues(hologramLine);
        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(hologramLine.getEntityId(), dataValues);
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(metadataPacket);
    }

    @Override
    public void sendHologramMetadataPacket(HologramLine hologramLine, Collection<Player> players) {
        List<SynchedEntityData.DataValue<?>> dataValues = HologramPropertyMappings.getInstance().createDataValues(hologramLine);
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(hologramLine.getEntityId(), dataValues);
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramDespawnPacket(HologramLine hologramLine, Collection<Player> players) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(hologramLine.getEntityId());
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public int getNextAvailableEntityId() {
        return entityIdIncrementer.incrementAndGet();
    }

    @Override
    public VersionAvailabilityProvider getVersionAvailabilityProvider() {
        return HologramPropertyMappings.getInstance();
    }

}
