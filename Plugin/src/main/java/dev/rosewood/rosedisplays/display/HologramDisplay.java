package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import dev.rosewood.rosedisplays.manager.ConfigurationManager.Setting;
import dev.rosewood.rosedisplays.nms.NMSAdapter;
import dev.rosewood.rosedisplays.nms.NMSHandler;
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
            this.entityId = NMSAdapter.getHandler().getNextAvailableEntityId();
            this.lastFrameData = null;
        }

        public boolean requiresUpdate(int[] lineData) {
            boolean requiresUpdate = !Arrays.equals(this.lastFrameData, lineData);
            if (requiresUpdate)
                this.lastFrameData = lineData;
            return requiresUpdate;
        }

        public int getEntityId() {
            return this.entityId;
        }

    }

}
