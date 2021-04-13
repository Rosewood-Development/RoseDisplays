package dev.rosewood.rosedisplays.data;

import java.io.File;

public abstract class FileDataSource implements DataSource {

    protected final File file;

    public FileDataSource(File file) {
        this.file = file;
    }

    public final File getFile() {
        return this.file;
    }

}
