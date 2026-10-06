package dev.minceraft.configureableconfigurate.stores;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NullMarked
public class PathStore implements IConfigStore {

    private final Path path;
    private final boolean createIfNotExists;
    private final boolean readOnly;

    public PathStore(Path path, boolean createIfNotExists, boolean readOnly) {
        this.path = path;
        this.createIfNotExists = createIfNotExists;
        this.readOnly = readOnly;
    }

    @Override
    public @Nullable BufferedReader getReader() {
        try {
            return Files.newBufferedReader(this.path);
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    public @Nullable BufferedWriter getWriter() {
        if (this.readOnly) {
            return null; // Return null if the file is read-only
        }
        try {
            return Files.newBufferedWriter(this.path);
        } catch (IOException ignored) {
            if (this.createIfNotExists) {
                try {
                    Files.createDirectories(this.path.getParent());
                    Files.createFile(this.path);
                    return Files.newBufferedWriter(this.path);
                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
