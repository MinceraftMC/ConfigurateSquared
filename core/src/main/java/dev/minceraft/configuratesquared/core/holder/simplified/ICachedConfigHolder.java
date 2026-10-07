package dev.minceraft.configuratesquared.core.holder.simplified;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ICachedConfigHolder<T> extends IBasicConfigHolder<T> {

    T reloadConfig();

    T getConfigOrLoad();

    T getConfig();

    default void saveConfig() {
        this.saveConfig(this.getConfig());
    }
}
