package dev.rosewood.rosedisplays.nms.v1_18_R1;

import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.nms.v1_18_R1.object.SynchedEntityDataWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandlerImpl implements NMSHandler {

    public static final String BLOCK = "\u2588";
    public static final String SPACE = "\u3000";

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
                EntityType.ARMOR_STAND,
                1,
                Vec3.ZERO
        );

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramMetadataPacket(Collection<Player> players, int entityId, int[] frameData) {
        List<SynchedEntityData.DataItem<?>> dataItems = new ArrayList<>();
        Optional<Component> nameComponent = Optional.of(this.getDataAsChatComponent(frameData));
        dataItems.add(new SynchedEntityData.DataItem<>(EntityDataSerializers.BYTE.createAccessor(0), (byte) 0x20));
        dataItems.add(new SynchedEntityData.DataItem<>(EntityDataSerializers.OPTIONAL_COMPONENT.createAccessor(2), nameComponent));
        dataItems.add(new SynchedEntityData.DataItem<>(EntityDataSerializers.BOOLEAN.createAccessor(3), true));
        dataItems.add(new SynchedEntityData.DataItem<>(EntityDataSerializers.BOOLEAN.createAccessor(5), true));

        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(entityId, new SynchedEntityDataWrapper(dataItems), false);

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public void sendHologramDespawnPacket(Collection<Player> players, int entityId) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entityId);

        for (Player player : players)
            ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private BaseComponent getDataAsChatComponent(int[] data) {
        BaseComponent component = new TextComponent("");
        TextComponent lastComponent = null;
        int lastColor = 0xFFFFFF;
        for (int color : data) {
            lastComponent = this.appendComponent(component, color, lastColor, lastComponent);
            lastColor = color;
        }

        return component;
    }

    private TextComponent appendComponent(BaseComponent parent, int color, int lastColor, TextComponent lastComponent) {
        TextComponent component;

        // Handle transparency
        int alpha = color >>> 24;
        if (alpha <= 127) {
            component = new TextComponent(SPACE);
            // Try to hide the full block space character the best we can by coloring it black
            component.setStyle(Style.EMPTY.withColor(ChatFormatting.BLACK));
            parent.append(component);
            return component;
        } else {
            color &= 0x00FFFFFF;
        }

        if (lastComponent != null && color == lastColor) {
            // Add the character to the last component (with the same color)
            component = new TextComponent(lastComponent.getContents() + BLOCK);
            component.setStyle(lastComponent.getStyle());

            // Replace old component
            parent.getSiblings().set(parent.getSiblings().size() - 1, component);
        } else {
            // Add a new component
            component = new TextComponent(BLOCK);
            component.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            parent.append(component);
        }
        return component;
    }

}
