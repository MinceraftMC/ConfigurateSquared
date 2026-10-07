package dev.minceraft.configureableconfigurate.holder.simplified;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface IBasicConfigHolder<T> {

    @Nullable
    T loadConfig(boolean createIfNotExists);

    @Nullable
    default T loadConfig() {
        return this.loadConfig(true);
    }

    void saveConfig(T config);
}
