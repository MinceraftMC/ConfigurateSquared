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

    public StringBufferStore() {
        this.buffer = null;
    }

    public StringBufferStore(String initialBuffer) {
        this.buffer = initialBuffer;
    }

    @Nullable
    public String getBuffer() {
        return this.buffer;
    }

    public void setBuffer(@Nullable String buffer) {
        this.buffer = buffer;
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
        return new BufferedWriter(new StringWriter() {
            @Override
            public void close() {
                StringBufferStore.this.buffer = this.toString();
            }
        });
    }
}
