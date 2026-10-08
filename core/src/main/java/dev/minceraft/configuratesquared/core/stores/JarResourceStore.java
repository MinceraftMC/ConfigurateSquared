package dev.minceraft.configuratesquared.core.stores;

import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class JarResourceStore implements IConfigStore {

    private final String resourcePath;

    public JarResourceStore(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public boolean hasReader() {
        return true;
    }

    @Override
    public @Nullable BufferedReader getReader() {
        InputStream resourceStream = Objects.requireNonNull(this.getClass().getResourceAsStream(this.resourcePath),
                "Resource not found: " + this.resourcePath);
        return new BufferedReader(new InputStreamReader(resourceStream));
    }

    @Override
    public @Nullable BufferedWriter getWriter() {
        return null; // Writing to a JAR resource is not supported
    }
}
