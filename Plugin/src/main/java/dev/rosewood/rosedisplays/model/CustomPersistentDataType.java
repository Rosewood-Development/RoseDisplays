package dev.rosewood.rosedisplays.model;

import dev.rosewood.rosedisplays.hologram.Hologram;
import dev.rosewood.rosedisplays.hologram.UnloadedHologram;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public final class CustomPersistentDataType {

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
                    dataOutput.writeUTF(hologram.getName());
                    dataOutput.writeUTF(hologram.getChunkLocation().world());
                    dataOutput.writeInt(hologram.getChunkLocation().x());
                    dataOutput.writeInt(hologram.getChunkLocation().z());
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
        private static final int VERSION = 1;

        public Class<byte[]> getPrimitiveType() { return byte[].class; }
        public Class<Hologram[]> getComplexType() { return Hologram[].class; }

        @Override
        public byte[] toPrimitive(Hologram[] complex, PersistentDataAdapterContext context) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream)) {
                dataOutput.writeInt(VERSION);
                dataOutput.writeInt(complex.length);
                for (Hologram hologram : complex) {

                    // todo
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
                    case 1 -> {
                        int length = dataInput.readInt();
                        Hologram[] holograms = new Hologram[length];
                        for (int i = 0; i < length; i++) {
                            // todo
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

}
