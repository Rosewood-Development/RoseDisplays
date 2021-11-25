package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.manager.ConfigurationManager.Setting;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
import dev.rosewood.rosedisplays.util.EntityUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramDisplay extends Display {

    private static final double HOLOGRAM_OFFSET = 0.225D;

    private List<HologramLine> hologramLines;
    private boolean oddFrame = false;

    public HologramDisplay(DataSource dataSource, Location location) {
        super(DisplayType.HOLOGRAM, false, dataSource, location);
    }

    @Override
    protected void create(Set<Player> players, int[] frameData) {
        if (this.hologramLines == null) {
            this.hologramLines = new ArrayList<>();
            for (int i = 0; i < this.height; i++)
                this.hologramLines.add(new HologramLine());
        }

        NMSHandler nmsHandler = NMSAdapter.getHandler();
        Location baseLocation = this.location.clone().add(0, HOLOGRAM_OFFSET * this.height, 0);
        for (int i = 0; i < this.height; i++) {
            HologramLine line = this.hologramLines.get(i);
            nmsHandler.sendHologramSpawnPacket(players, line.getEntityId(), baseLocation.clone().add(0, -HOLOGRAM_OFFSET * i, 0));
            nmsHandler.sendHologramMetadataPacket(players, line.getEntityId(), this.getLineData(frameData, i));
        }
    }

    @Override
    protected void render(Set<Player> players, int[] frameData) {
        if (this.hologramLines == null)
            return;

        NMSHandler nmsHandler = NMSAdapter.getHandler();

        if (Setting.USE_INTERLACING.getBoolean()) {
            int x = this.oddFrame ? 1 : 0;
            for (int i = 0; i < this.hologramLines.size() / 2; i++) {
                int index = x + (i * 2);
                HologramLine line = this.hologramLines.get(index);
                int[] lineData = this.getLineData(frameData, index);
                if (!line.requiresUpdate(lineData))
                    continue;

                nmsHandler.sendHologramMetadataPacket(players, line.getEntityId(), lineData);
            }
            this.oddFrame = !this.oddFrame;
        } else {
            for (int i = 0; i < this.hologramLines.size(); i++) {
                HologramLine line = this.hologramLines.get(i);
                int[] lineData = this.getLineData(frameData, i);
                if (!line.requiresUpdate(lineData))
                    continue;

                nmsHandler.sendHologramMetadataPacket(players, line.getEntityId(), lineData);
            }
        }
    }

    @Override
    protected void destroy(Set<Player> players) {
        if (this.hologramLines == null)
            return;

        NMSHandler nmsHandler = NMSAdapter.getHandler();

        for (HologramLine line : this.hologramLines)
            nmsHandler.sendHologramDespawnPacket(players, line.getEntityId());
    }

    private int[] getLineData(int[] frameData, int lineNumber) {
        int[] lineData = new int[this.width];
        int start = lineNumber * this.width;
        for (int n = start, x = 0; n < start + this.width; n++, x++)
            lineData[x] = frameData[n];
        return lineData;
    }

    private static class HologramLine {

        private final int entityId;
        private int[] lastFrameData;

        public HologramLine() {
            this.entityId = EntityUtil.getNewEntityId();
            this.lastFrameData = null;
        }

        public boolean requiresUpdate(int[] lineData) {
            boolean requiresUpdate = !Arrays.equals(this.lastFrameData, lineData);
            if (requiresUpdate)
                this.lastFrameData = lineData;
            return requiresUpdate;
        }

//        /**
//         * Sends a metadata update with the given text.
//         *
//         * @param component text component
//         */
//        public PacketContainer createUpdateTextMetadataPacket(Object component) {
//            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
//            packetContainer.getIntegers().write(0, this.entityId);
//
//            List<WrappedWatchableObject> object = Collections.singletonList(
//                    new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, chatSerializer), Optional.ofNullable(component)));
//            packetContainer.getWatchableCollectionModifier().write(0, object);
//            return packetContainer;
//        }
//
//        public PacketContainer createSpawnPackets(Location location) {
//            final PacketContainer spawnEntityLiving = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
//            spawnEntityLiving.getIntegers().write(0, this.entityId);
//            spawnEntityLiving.getUUIDs().write(0, UUID.randomUUID());
//            spawnEntityLiving.getIntegers().write(1, 1);
//            spawnEntityLiving.getDoubles().write(0, location.getX());
//            spawnEntityLiving.getDoubles().write(1, location.getY());
//            spawnEntityLiving.getDoubles().write(2, location.getZ());
//            return spawnEntityLiving;
//        }
//
//        public PacketContainer createDespawnPacket() {
//            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
//            packetContainer.getIntegerArrays().write(0, new int[]{this.entityId});
//            return packetContainer;
//        }
//
//        public PacketContainer createMetadataPacket(Object component) {
//            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
//            packetContainer.getIntegers().write(0, this.entityId);
//            packetContainer.getWatchableCollectionModifier().write(0, this.buildMetadata(component));
//            return packetContainer;
//        }
//
//        private List<WrappedWatchableObject> buildMetadata(Object component) {
//            return Arrays.asList(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20),
//                    new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.ofNullable(component)),
//                    new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true),
//                    new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true));
//        }

        public int getEntityId() {
            return this.entityId;
        }

    }

}
