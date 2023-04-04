package dev.rosewood.rosedisplays.nms.v1_19_R3.mapping;

import dev.rosewood.rosedisplays.property.HologramProperty;
import java.util.function.Function;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public record HologramPropertyMapping<T, R>(String property,
                                            Class<T> propertyType,
                                            EntityDataAccessor<R> entityDataAccessor,
                                            Function<T, R> transformer) implements HologramProperty<T> {

    public SynchedEntityData.DataValue<R> createDataValue(T value) {
        R transformed = this.transformer.apply(value);
        return SynchedEntityData.DataValue.create(this.entityDataAccessor, transformed);
    }

}
