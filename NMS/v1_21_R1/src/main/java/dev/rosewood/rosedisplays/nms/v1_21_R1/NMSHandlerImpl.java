package dev.rosewood.rosedisplays.nms.v1_21_R1;

import dev.rosewood.rosedisplays.hologram.HologramLine;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.nms.util.ReflectionUtils;
import dev.rosewood.rosedisplays.nms.v1_21_R1.mapping.HologramPropertyMappings;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandlerImpl implements NMSHandler {

    private static final Map<Class<?>, Function<FriendlyByteBuf, ?>> PACKET_CONSTRUCTOR_MAP = new HashMap<>();

    private static AtomicInteger entityIdIncrementer;
    static {
        try {
            entityIdIncrementer = (AtomicInteger) ReflectionUtils.getFieldByPositionAndType(Entity.class, 0, AtomicInteger.class).get(null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendHologramSpawnPacket(Object hologramLineArg, Location location, Collection<Player> players) {
        HologramLine hologramLine = (HologramLine) hologramLineArg;
        EntityType<?> entityType = switch (hologramLine.getType()) {
            case TEXT -> EntityType.TEXT_DISPLAY;
            case ITEM -> EntityType.ITEM_DISPLAY;
            case BLOCK -> EntityType.BLOCK_DISPLAY;
            default -> throw new IllegalArgumentException("Unsupported hologram line type: " + hologramLine.getType());
        };

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
    public void sendHologramMetadataPacket(Object hologramLineArg, Collection<Player> players) {
        HologramLine hologramLine = (HologramLine) hologramLineArg;
        List<SynchedEntityData.DataValue<?>> dataValues = HologramPropertyMappings.getInstance().createDataValues(hologramLine);
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(hologramLine.getEntityId(), dataValues);
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramDespawnPacket(Object hologramLineArg, Collection<Player> players) {
        HologramLine hologramLine = (HologramLine) hologramLineArg;
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(hologramLine.getEntityId());
        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramSetVehiclePacket(Object hologramLineArg, org.bukkit.entity.Entity vehicle, Collection<Player> players) {
        HologramLine hologramLine = (HologramLine) hologramLineArg;
        Level world = ((CraftWorld) vehicle.getWorld()).getHandle();
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(vehicle.getEntityId());
        byteBuf.writeVarIntArray(new int[]{hologramLine.getEntityId()});
        ClientboundSetPassengersPacket packet = this.constructPacket(ClientboundSetPassengersPacket.class, byteBuf);
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

    @SuppressWarnings("unchecked")
    private <T extends Packet<?>> T constructPacket(Class<T> packetClass, FriendlyByteBuf friendlyByteBuf) {
        return (T) PACKET_CONSTRUCTOR_MAP.computeIfAbsent(packetClass, key -> {
            try {
                Constructor<T> constructor = packetClass.getDeclaredConstructor(FriendlyByteBuf.class);
                constructor.setAccessible(true);
                return byteBuf -> {
                    try {
                        return constructor.newInstance(byteBuf);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Failed to construct packet: " + packetClass.getName(), e);
                    } finally {
                        byteBuf.release();
                    }
                };
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("FriendlyByteBuf constructor not found for class: " + packetClass.getName(), e);
            }
        }).apply(friendlyByteBuf);
    }

}
