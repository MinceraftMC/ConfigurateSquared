package dev.minceraft.configureableconfigurate.holder;

import dev.minceraft.configureableconfigurate.holder.projected.ICachedConfigHolder;
import dev.minceraft.configureableconfigurate.stores.IConfigStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class CachedConfigHolder<
        H extends CachedConfigHolder<H, T, L, B>,
        T,
        L extends AbstractConfigurationLoader<?>,
        B extends AbstractConfigurationLoader.Builder<B, L>
        > extends BasicConfigHolder<H, T, L, B>
        implements ICachedConfigHolder<T> {

    protected volatile @Nullable T config;

    public CachedConfigHolder(
            Class<T> configClass,
            Function<H, T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder
    ) {
        super(configClass, def, store, loaderBuilder);
    }

    @Override
    public T reloadConfig() {
        T config = this.loadConfig(true);
        if (config == null) {
            throw new IllegalStateException("Failed to load config");
        }
        this.config = config;
        return config;
    }

    @Override
    public T getConfigOrLoad() {
        T config = this.config;
        if (config == null) {
            config = this.reloadConfig();
        }
        return config;
    }

    @Override
    public T getConfig() {
        T config = this.config;
        if (config == null) {
            throw new IllegalStateException("Config not loaded yet");
        }
        return config;
    }
}
