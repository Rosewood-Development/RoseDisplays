package dev.rosewood.rosedisplays.util;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public final class EntityUtil {

    private static final AtomicInteger ENTITY_ID;

    static {
        try {
            Field entityCount = Class.forName("net.minecraft.world.entity.Entity").getDeclaredField("b");
            entityCount.setAccessible(true);
            ENTITY_ID = (AtomicInteger) entityCount.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private EntityUtil() {

    }

    public static int getNewEntityId() {
        return ENTITY_ID.incrementAndGet();
    }

}
