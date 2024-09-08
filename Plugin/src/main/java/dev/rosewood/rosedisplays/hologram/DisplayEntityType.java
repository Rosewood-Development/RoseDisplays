package dev.rosewood.rosedisplays.hologram;

import dev.rosewood.rosedisplays.hologram.property.HologramPropertyTag;

public enum DisplayEntityType {

    TEXT(HologramPropertyTag.TEXT_DISPLAY_ENTITY),
    ITEM(HologramPropertyTag.ITEM_DISPLAY_ENTITY),
    BLOCK(HologramPropertyTag.BLOCK_DISPLAY_ENTITY);

    private final HologramPropertyTag tag;

    DisplayEntityType(HologramPropertyTag tag) {
        this.tag = tag;
    }

    public HologramPropertyTag getTag() {
        return this.tag;
    }

}
