package dev.minceraft.configuratesquared.core.holder;

import dev.minceraft.configuratesquared.core.holder.simplified.IHookedConfigHolder;
import dev.minceraft.configuratesquared.core.stores.IConfigStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class HookedConfigHolder<
        H extends CachedConfigHolder<H, T, L, B>,
        T,
        L extends AbstractConfigurationLoader<?>,
        B extends AbstractConfigurationLoader.Builder<B, L>
        > extends CachedConfigHolder<H, T, L, B>
        implements IHookedConfigHolder<T> {

    private final Set<Consumer<T>> reloadHooks = new CopyOnWriteArraySet<>();

    public HookedConfigHolder(
            Class<T> configClass,
            Function<H, @Nullable T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder) {
        super(configClass, def, store, loaderBuilder);
    }

    @Override
    public void addReloadHook(Consumer<T> consumer) {
        this.reloadHooks.add(consumer);
    }

    @Override
    public void addReloadHookAndRun(Consumer<T> consumer) {
        this.addReloadHook(consumer);

        T config = this.config;
        if (config != null) {
            consumer.accept(config);
        }
    }

    @Override
    public T reloadConfig() {
        T config = super.reloadConfig();
        for (Consumer<T> reloadHook : this.reloadHooks) {
            reloadHook.accept(config);
        }
        return config;
    }
}
