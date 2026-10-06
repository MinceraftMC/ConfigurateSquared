package dev.minceraft.configureableconfigurate.holder;

import dev.minceraft.configureableconfigurate.stores.IConfigStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class CachedConfigHolder<T, L extends AbstractConfigurationLoader<?>> extends BasicConfigHolder<T, L> {

    protected volatile @Nullable T config;

    public <B extends AbstractConfigurationLoader.Builder<B, L>> CachedConfigHolder(
            Class<T> configClass,
            Function<BasicConfigHolder<T, L>, T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder) {
        super(configClass, def, store, loaderBuilder);
    }

    public T reloadConfig() {
        T config = this.loadConfig(true);
        if (config == null) {
            throw new IllegalStateException("Failed to load config");
        }
        this.config = config;
        return config;
    }

    public T getConfigOrLoad() {
        T config = this.config;
        if (config == null) {
            config = this.reloadConfig();
        }
        return config;
    }

    public T getConfig() {
        T config = this.config;
        if (config == null) {
            throw new IllegalStateException("Config not loaded yet");
        }
        return config;
    }
}
