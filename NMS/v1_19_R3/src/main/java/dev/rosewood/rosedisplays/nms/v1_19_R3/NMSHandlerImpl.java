package dev.rosewood.rosedisplays.nms.v1_19_R3;

import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.nms.util.ReflectionUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.LiteralContents;
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

    private static final String BLOCK = "\u2588\uF801";
    private static final String SPACE = "\u3000\uF801";

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
    public void sendHologramMetadataPacket(Collection<Player> players, int entityId, int[] frameData) {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>(DATA_VALUES);
        Component nameComponent = this.getDataAsChatComponent(frameData);
        dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.COMPONENT.createAccessor(22), nameComponent));

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

    private MutableComponent getDataAsChatComponent(int[] data) {
        MutableComponent component = Component.empty();
        MutableComponent lastComponent = null;
        int lastColor = 0xFFFFFF;
        for (int color : data) {
            lastComponent = this.appendComponent(component, color, lastColor, lastComponent);
            lastColor = color;
        }

        return component;
    }

    private MutableComponent appendComponent(MutableComponent parent, int color, int lastColor, MutableComponent lastComponent) {
        MutableComponent component;

        // Handle transparency
        int alpha = color >>> 24;
        if (alpha <= 127) {
            component = Component.literal(SPACE);
            // Try to hide the full block space character the best we can by coloring it black
            component.setStyle(Style.EMPTY.withColor(ChatFormatting.BLACK));
            parent.append(component);
            return component;
        } else {
            color &= 0x00FFFFFF;
        }

        if (color == lastColor && lastComponent != null && lastComponent.getContents() instanceof LiteralContents literalContents) {
            // Add the character to the last component (with the same color)
            component = Component.literal(literalContents.text() + BLOCK);
            component.setStyle(lastComponent.getStyle());

            // Replace old component
            parent.getSiblings().set(parent.getSiblings().size() - 1, component);
        } else {
            // Add a new component
            component = Component.literal(BLOCK);
            component.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            parent.append(component);
        }
        return component;
    }

}
