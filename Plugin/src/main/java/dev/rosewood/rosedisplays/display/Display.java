package dev.rosewood.rosedisplays.display;

import dev.rosewood.rosedisplays.data.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Display implements Runnable {

    private final DisplayType displayType;
    private final Set<Player> viewers;
    protected final DataSource dataSource;

    private final boolean continuous;
    private boolean destroyed;
    private int[] nextFrameData;

    protected final Location location;
    protected int width, height;

    public Display(DisplayType displayType, boolean continuous, DataSource dataSource, Location location) {
        this.displayType = displayType;
        this.dataSource = dataSource;
        this.location = location;
        this.continuous = continuous;
        this.viewers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
        this.destroyed = false;
    }

    protected abstract void create(Set<Player> players, int[] frameData);

    protected abstract void render(Set<Player> players, int[] frameData);

    protected abstract void destroy(Set<Player> players);

    @Override
    public final void run() {
        this.nextFrameData = this.dataSource.nextFrame();
        this.width = this.dataSource.getWidth();
        this.height = this.dataSource.getHeight();

        if (!this.viewers.isEmpty())
            this.create(this.viewers, this.nextFrameData);

        while (!this.destroyed) {
            // If empty, don't waste time processing the next frame
            boolean process = !this.viewers.isEmpty() && (this.continuous || !this.dataSource.isStatic());
            if (process)
                this.render(this.viewers, this.nextFrameData);

            try {
                Thread.sleep(this.dataSource.getFrameDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (process)
                this.nextFrameData = this.dataSource.nextFrame();
        }
    }

    public final void addViewer(Player player) {
        this.addViewers(Collections.singleton(player));
    }

    public final void addViewers(Collection<? extends Player> players) {
        this.checkDestroyed();
        Set<Player> added = new HashSet<>(players);
        added.removeIf(this.viewers::contains);
        if (added.isEmpty())
            return;

        this.viewers.addAll(added);

        if (this.nextFrameData != null)
            this.create(added, this.nextFrameData);
    }

    public final void removeViewer(Player player) {
        this.checkDestroyed();
        if (!this.viewers.contains(player))
            return;

        this.viewers.remove(player);
        this.destroy(Collections.singleton(player));
    }

    public final void destroy() {
        this.checkDestroyed();
        this.destroy(this.viewers);
        this.viewers.clear();
        this.destroyed = true;
    }

    public final Set<Player> getViewers() {
        this.checkDestroyed();
        return Collections.unmodifiableSet(this.viewers);
    }

    private void checkDestroyed() {
        if (this.destroyed)
            throw new IllegalStateException("Display is destroyed");
    }

    public final DisplayType getDisplayType() {
        return this.displayType;
    }

}
