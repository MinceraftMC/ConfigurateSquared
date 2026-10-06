package dev.minceraft.configureableconfigurate.stores;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;

@NullMarked
public interface IConfigStore {

    @Nullable
    BufferedReader getReader();

    @Nullable
    BufferedWriter getWriter();
}
