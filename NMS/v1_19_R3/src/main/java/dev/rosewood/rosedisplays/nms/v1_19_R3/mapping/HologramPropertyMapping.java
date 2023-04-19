package dev.rosewood.rosedisplays.nms.v1_19_R3.mapping;

import dev.rosewood.rosedisplays.hologram.property.HologramProperty;
import java.util.function.Function;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public record HologramPropertyMapping<T, R>(HologramProperty<T> property,
                                            EntityDataAccessor<R> entityDataAccessor,
                                            Function<T, R> transformer,
                                            R defaultValue) {

    public SynchedEntityData.DataValue<R> createDataValue(T value) {
        if (value == null)
            return SynchedEntityData.DataValue.create(this.entityDataAccessor, this.defaultValue);
        R transformed = this.transformer.apply(value);
        return SynchedEntityData.DataValue.create(this.entityDataAccessor, transformed);
    }

}
