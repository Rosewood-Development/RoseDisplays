package dev.rosewood.rosedisplays.nms.v1_19_R3;

import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.nms.util.ReflectionUtils;
import dev.rosewood.rosedisplays.nms.v1_19_R3.mapping.HologramPropertyProviderImpl;
import dev.rosewood.rosedisplays.property.HologramPropertyProvider;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandlerImpl implements NMSHandler {

    private static final List<SynchedEntityData.DataValue<?>> DATA_VALUES = List.of(
            SynchedEntityData.DataValue.create(EntityDataSerializers.BYTE.createAccessor(14), (byte) 1), // Billboard Constraint (Fixed)
            SynchedEntityData.DataValue.create(EntityDataSerializers.FLOAT.createAccessor(16), 5.0F), // Visibility, always visible since these are hidden behind walls
            SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(23), Integer.MAX_VALUE), // Line width
            SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(24), new Color(0, 0, 0, 0).getRGB()) // Background color
    );

    private static AtomicInteger entityIdIncrementer;
    static {
        try {
            entityIdIncrementer = (AtomicInteger) ReflectionUtils.getFieldByPositionAndType(Entity.class, 0, AtomicInteger.class).get(null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendHologramSpawnPacket(Collection<Player> players, int entityId, Location location) {
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                entityId,
                UUID.randomUUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0,
                0,
                EntityType.TEXT_DISPLAY,
                1,
                Vec3.ZERO,
                0
        );

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramMetadataPacket(Collection<Player> players, int entityId, String text) {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>(DATA_VALUES);
        dataValues.add(HologramPropertyProviderImpl.getInstance().createDataValue("text", text));

        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(entityId, dataValues);

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramDespawnPacket(Collection<Player> players, int entityId) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entityId);

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public int getNextAvailableEntityId() {
        return entityIdIncrementer.incrementAndGet();
    }

    @Override
    public HologramPropertyProvider getHologramPropertyProvider() {
        return HologramPropertyProviderImpl.getInstance();
    }

}
