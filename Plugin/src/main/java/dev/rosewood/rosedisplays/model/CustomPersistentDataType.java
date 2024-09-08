package dev.rosewood.rosedisplays.model;

import dev.rosewood.rosedisplays.RoseDisplays;
import dev.rosewood.rosedisplays.hologram.DisplayEntityType;
import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.UnloadedHologram;
import dev.rosewood.rosedisplays.hologram.property.HologramProperties;
import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import dev.rosewood.rosedisplays.hologram.property.HologramPropertyContainer;
import dev.rosewood.rosedisplays.hologram.property.Stringifier;
import dev.rosewood.rosedisplays.hologram.type.DisplayEntityHologram;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public final class CustomPersistentDataType {

    public final static NamespacedKey HOLOGRAM_KEY = new NamespacedKey("rosedisplays", "holograms");

    public static final PersistentDataType<byte[], UnloadedHologram[]> UNLOADED_HOLOGRAM_ARRAY = new PersistentDataType<>() {
        private static final int VERSION = 1;

        public Class<byte[]> getPrimitiveType() { return byte[].class; }
        public Class<UnloadedHologram[]> getComplexType() { return UnloadedHologram[].class; }

        @Override
        public byte[] toPrimitive(UnloadedHologram[] complex, PersistentDataAdapterContext context) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream)) {
                dataOutput.writeInt(VERSION);
                dataOutput.writeInt(complex.length);
                for (UnloadedHologram hologram : complex) {
                    dataOutput.writeUTF(hologram.name());
                    dataOutput.writeUTF(hologram.chunkLocation().world());
                    dataOutput.writeInt(hologram.chunkLocation().x());
                    dataOutput.writeInt(hologram.chunkLocation().z());
                }
                dataOutput.close();
                return outputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public UnloadedHologram[] fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(primitive);
                 ObjectInputStream dataInput = new ObjectInputStream(inputStream)) {
                int version = dataInput.readInt();
                return switch (version) {
                    case 1 -> {
                        int length = dataInput.readInt();
                        UnloadedHologram[] holograms = new UnloadedHologram[length];
                        for (int i = 0; i < length; i++) {
                            String id = dataInput.readUTF();
                            String world = dataInput.readUTF();
                            int x = dataInput.readInt();
                            int z = dataInput.readInt();
                            holograms[i] = new UnloadedHologram(id, new ChunkLocation(world, x, z));
                        }
                        yield holograms;
                    }
                    default -> throw new IllegalStateException("Tried to load UnloadedHologram[] from an unsupported version: " + version);
                };
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    };

    public static final PersistentDataType<byte[], Hologram[]> HOLOGRAM_ARRAY = new PersistentDataType<>() {
        private static final int VERSION = 2;

        public Class<byte[]> getPrimitiveType() { return byte[].class; }
        public Class<Hologram[]> getComplexType() { return Hologram[].class; }

        @Override
        public byte[] toPrimitive(Hologram[] complex, PersistentDataAdapterContext context) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream)) {
                dataOutput.writeInt(VERSION);
                dataOutput.writeInt(complex.length);
                for (Hologram hologram : complex) {
                    dataOutput.writeUTF(hologram.getName());
                    dataOutput.writeUTF(((DisplayEntityHologram) hologram).getType().name());
                    dataOutput.writeUTF(hologram.getChunkLocation().world());
                    dataOutput.writeDouble(hologram.getLocation().getX());
                    dataOutput.writeDouble(hologram.getLocation().getY());
                    dataOutput.writeDouble(hologram.getLocation().getZ());
                    HologramPropertyContainer properties = hologram.getProperties();
                    dataOutput.writeInt(properties.size());
                    for (Map.Entry<HologramProperty<?>, Object> entry : properties.entrySet()) {
                        dataOutput.writeUTF(entry.getKey().getName());
                        dataOutput.writeUTF(Stringifier.stringify(entry.getValue()));
                    }
                }
                dataOutput.close();
                return outputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Hologram[] fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(primitive);
                 ObjectInputStream dataInput = new ObjectInputStream(inputStream)) {
                int version = dataInput.readInt();
                return switch (version) {
                    case 2 -> {
                        int length = dataInput.readInt();
                        Hologram[] holograms = new Hologram[length];
                        for (int i = 0; i < length; i++) {
                            String name = dataInput.readUTF();
                            String typeString = dataInput.readUTF();
                            String worldName = dataInput.readUTF();
                            double x = dataInput.readDouble();
                            double y = dataInput.readDouble();
                            double z = dataInput.readDouble();
                            World world = Bukkit.getWorld(worldName);
                            if (world == null)
                                throw new IllegalStateException("Could not find world while loading hologram for world: " + worldName);

                            Location location = new Location(world, x, y, z);
                            DisplayEntityType type = DisplayEntityType.valueOf(typeString);

                            Map<HologramProperty<?>, Object> properties = new HashMap<>();
                            int propertyCount = dataInput.readInt();
                            for (int j = 0; j < propertyCount; j++) {
                                String propertyName = dataInput.readUTF();
                                String value = dataInput.readUTF();
                                HologramProperty<?> property = HologramProperties.valueOf(propertyName);
                                if (property == null) {
                                    RoseDisplays.getInstance().getLogger().warning("Unknown HologramProperty: " + propertyName);
                                    continue;
                                }
                                properties.put(property, Stringifier.unstringify(property.getType(), value));
                            }

                            holograms[i] = new DisplayEntityHologram(name, type, location);
                        }
                        yield holograms;
                    }
                    default -> {
                        RoseDisplays.getInstance().getLogger().warning("Discarded old Hologram PDC data with an invalid version");
                        yield new Hologram[0];
                    }
                };
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    };

}
