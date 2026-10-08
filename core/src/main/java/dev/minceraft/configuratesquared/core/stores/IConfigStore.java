package dev.minceraft.configuratesquared.core.stores;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;

@NullMarked
public interface IConfigStore {

    default boolean hasReader() {
        return false;
    }

    default boolean hasWriter() {
        return false;
    }

    @Nullable
    default BufferedReader getReader() {
        return null;
    }

    @Nullable
    default BufferedWriter getWriter() {
        return null;
    }
}

