package dev.minceraft.configureableconfigurate.holder;

import dev.minceraft.configureableconfigurate.stores.IConfigStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class BasicConfigHolder<T, L extends AbstractConfigurationLoader<?>> {

    private final Class<T> configClass;
    private final Function<BasicConfigHolder<T, L>, T> def;
    private final IConfigStore store;
    private final Supplier<L> loader;

    public <B extends AbstractConfigurationLoader.Builder<B, L>> BasicConfigHolder(
            Class<T> configClass,
            Function<BasicConfigHolder<T, L>, T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder
    ) {
        this.configClass = configClass;
        this.def = def;
        this.store = store;
        this.loader = () -> {
            B builder = loaderBuilder.get();
            BufferedReader reader = store.getReader();
            if (reader != null) {
                builder.source(() -> reader);
            }
            BufferedWriter writer = store.getWriter();
            if (writer != null) {
                builder.sink(() -> writer);
            }
            return builder.build();
        };
    }

    @Nullable
    public T loadConfig(boolean saveAfterLoad) {
        T config;
        if (this.store.getReader() == null) {
            config = this.def.apply(this);
        } else {
            try {
                config = this.loader.get().load().get(this.configClass);
            } catch (ConfigurateException exception) {
                throw new RuntimeException("Failed to load config", exception);
            }
        }
        if (saveAfterLoad && config != null) {
            this.saveConfig(config);
        }
        return config;
    }

    @Nullable
    public T loadConfig() {
        return this.loadConfig(true);
    }

    public void saveConfig(T config) {
        L loader = this.loader.get();
        try {
            loader.save(loader.createNode().set(this.configClass, config));
        } catch (ConfigurateException exception) {
            throw new RuntimeException("Failed to save config", exception);
        }
    }

}
