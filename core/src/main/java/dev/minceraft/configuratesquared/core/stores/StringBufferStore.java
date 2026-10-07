package dev.minceraft.configuratesquared.core.stores;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;

@NullMarked
public class StringBufferStore implements IConfigStore {

    private @Nullable String buffer;
    private boolean readOnly;

    public StringBufferStore() {
        this.readOnly = false;
        this.buffer = null;
    }

    public StringBufferStore(boolean readOnly) {
        this.readOnly = readOnly;
        this.buffer = null;
    }

    public StringBufferStore(String initialBuffer) {
        this.buffer = initialBuffer;
        this.readOnly = false;
    }

    public StringBufferStore(String initialBuffer, boolean readOnly) {
        this.buffer = initialBuffer;
        this.readOnly = readOnly;
    }

    @Nullable
    public String getBuffer() {
        return this.buffer;
    }

    public void setBuffer(@Nullable String buffer) {
        this.buffer = buffer;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public @Nullable BufferedReader getReader() {
        if (this.buffer == null) {
            return null;
        }
        return new BufferedReader(new StringReader(this.buffer));
    }

    @Override
    public @Nullable BufferedWriter getWriter() {
        if (this.readOnly) {
            return null; // Return null if the store is read-only
        }
        return new BufferedWriter(new StringWriter() {
            @Override
            public void close() {
                StringBufferStore.this.buffer = this.toString();
            }
        });
    }
}
