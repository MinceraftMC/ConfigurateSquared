package dev.minceraft.configuratesquared.core.holder;

import dev.minceraft.configuratesquared.core.holder.simplified.ICachedConfigHolder;
import dev.minceraft.configuratesquared.core.serializer.Serializer;
import dev.minceraft.configuratesquared.core.serializer.SerializerContext;
import dev.minceraft.configuratesquared.core.stores.IConfigStore;
import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.util.Map;
import java.util.function.Consumer;
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
            Function<H, @Nullable T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder,
            @Nullable Consumer<SerializerContext> contextInitializer,
            Map<TypeToken<?>, Serializer<?>> serializers
    ) {
        super(configClass, def, store, loaderBuilder, contextInitializer, serializers);
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
