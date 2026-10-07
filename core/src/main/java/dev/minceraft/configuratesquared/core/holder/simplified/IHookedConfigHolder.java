package dev.minceraft.configuratesquared.core.holder.simplified;

import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public interface IHookedConfigHolder<T> extends ICachedConfigHolder<T> {

    void addReloadHook(Consumer<T> consumer);

    void addReloadHookAndRun(Consumer<T> consumer);
}
