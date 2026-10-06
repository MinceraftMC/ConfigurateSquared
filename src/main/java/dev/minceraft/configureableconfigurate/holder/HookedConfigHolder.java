package dev.minceraft.configureableconfigurate.holder;

import dev.minceraft.configureableconfigurate.stores.IConfigStore;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class HookedConfigHolder<T, L extends AbstractConfigurationLoader<?>> extends CachedConfigHolder<T, L> {

    private final Set<Consumer<T>> reloadHooks = new CopyOnWriteArraySet<>();

    public <B extends AbstractConfigurationLoader.Builder<B, L>> HookedConfigHolder(
            Class<T> configClass,
            Function<BasicConfigHolder<T, L>, T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder) {
        super(configClass, def, store, loaderBuilder);
    }

    public void addReloadHook(Consumer<T> consumer) {
        this.reloadHooks.add(consumer);
    }

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
