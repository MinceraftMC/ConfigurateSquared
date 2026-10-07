package dev.minceraft.configureableconfigurate.holder;

import dev.minceraft.configureableconfigurate.holder.projected.IBasicConfigHolder;
import dev.minceraft.configureableconfigurate.holder.projected.ICachedConfigHolder;
import dev.minceraft.configureableconfigurate.stores.IConfigStore;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public final class ConfigHolders<L extends AbstractConfigurationLoader<?>, B extends AbstractConfigurationLoader.Builder<B, L>> {

    public static final ConfigHolders<YamlConfigurationLoader, ?> YAML = new ConfigHolders<>(YamlConfigurationLoader::builder);
    public static final ConfigHolders<GsonConfigurationLoader, ?> GSON = new ConfigHolders<>(GsonConfigurationLoader::builder);

    private final Supplier<B> loaderSupplier;

    private ConfigHolders(Supplier<B> loaderSupplier) {
        this.loaderSupplier = loaderSupplier;
    }

    public <H extends BasicConfigHolder<H, T, L, B>, T> IBasicConfigHolder<T> basic(Class<T> configClass, Function<H, T> def, IConfigStore store) {
        return new BasicConfigHolder<>(configClass, def, store, this.loaderSupplier);
    }

    public <H extends CachedConfigHolder<H, T, L, B>, T> ICachedConfigHolder<T> cached(Class<T> configClass, Function<H, T> def, IConfigStore store) {
        return new CachedConfigHolder<>(configClass, def, store, this.loaderSupplier);
    }

    public <H extends HookedConfigHolder<H, T, L, B>, T> ICachedConfigHolder<T> hooked(Class<T> configClass, Function<H, T> def, IConfigStore store) {
        return new HookedConfigHolder<>(configClass, def, store, this.loaderSupplier);
    }
}







