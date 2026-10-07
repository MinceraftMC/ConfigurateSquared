package dev.minceraft.configureableconfigurate.holder.projected;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ICachedConfigHolder<T> extends IBasicConfigHolder<T> {

    T reloadConfig();

    T getConfigOrLoad();

    T getConfig();
}
