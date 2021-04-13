package dev.rosewood.rosedisplays.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Happily stolen from <a href="https://github.com/ArtFect/BlockHighlight">ArtFect's BlockHighlight</a>
 */
public final class DebugPayloadUtil {

    public static void sendStopToAll() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            sendStop(pl);
        }
    }

    public static void sendHideBehindBlocks(Player pl, int time) {
        sendBlockHighlight(pl, BlockHighlight.getHideBehindBlocks(time));
    }

    public static void sendHideBehindBlocksAlways(Player pl) {
        sendHideBehindBlocks(pl, 1000000000);
    }

    public static void sendHideBehindBlocksAlwaysToAll() {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            sendHideBehindBlocksAlways(pl);
        }
    }

    // TODO: Currently causes memory leaks due to the ByteBuf not being closed
    // TODO: Closing the ByteBuf causes the packet to fail to send properly
    public static void sendBlockHighlight(Player pl, BlockHighlight highlight) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeLong(blockPosToLong(highlight.getX(), highlight.getY(), highlight.getZ()));
        packet.writeInt(highlight.getColor());

        String text = ChatColor.translateAlternateColorCodes('&', highlight.getText());
        writeString(packet, PlaceholderAPIHook.applyPlaceholders(pl, text));
        packet.writeInt(highlight.getTime());

        sendPayload(pl, "debug/game_test_add_marker", packet);
    }

    // TODO: Currently causes memory leaks due to the ByteBuf not being closed
    // TODO: Closing the ByteBuf causes the packet to fail to send properly
    public static void sendBlockHighlights(Collection<Player> players, BlockHighlight highlight) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeLong(blockPosToLong(highlight.getX(), highlight.getY(), highlight.getZ()));
        packet.writeInt(highlight.getColor());

        writeString(packet, HexUtils.colorify(highlight.getText()));
        packet.writeInt(highlight.getTime());

        for (Player player : players)
            sendPayload(player, "debug/game_test_add_marker", packet);
    }

    public static void sendStop(Player pl) {
        sendPayload(pl, "debug/game_test_clear", Unpooled.wrappedBuffer(new byte[0]));
    }

    private static void sendPayload(Player receiver, String channel, ByteBuf bytes) {
        PacketContainer handle = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        handle.getMinecraftKeys().write(0, new MinecraftKey(channel));

        Object serializer = MinecraftReflection.getPacketDataSerializer(bytes);
        handle.getModifier().withType(ByteBuf.class).write(0, serializer);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, handle);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to send the packet", e);
        }
    }

    private static long blockPosToLong(int x, int y, int z) {
        return ((long) x & 67108863L) << 38 | (long) y & 4095L | ((long) z & 67108863L) << 12;
    }

    private static void writeVarInt(ByteBuf packet, int i) {
        while ((i & -128) != 0) {
            packet.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        packet.writeByte(i);
    }

    private static void writeString(ByteBuf packet, String s) {
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
        writeVarInt(packet, abyte.length);
        packet.writeBytes(abyte);
    }

}
