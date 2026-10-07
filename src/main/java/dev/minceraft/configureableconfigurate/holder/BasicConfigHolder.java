package dev.minceraft.configureableconfigurate.holder;

import dev.minceraft.configureableconfigurate.holder.projected.IBasicConfigHolder;
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
public class BasicConfigHolder<
        H extends BasicConfigHolder<H, T, L, B>,
        T,
        L extends AbstractConfigurationLoader<?>,
        B extends AbstractConfigurationLoader.Builder<B, L>>
        implements IBasicConfigHolder<T> {

    private final Class<T> configClass;
    private final Function<H, T> def;
    private final IConfigStore store;
    private final Supplier<L> loader;

    public BasicConfigHolder(
            Class<T> configClass,
            Function<H, T> def,
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

    @Override
    public @Nullable T loadConfig(boolean saveAfterLoad) {
        T config;
        if (this.store.getReader() == null) {
            config = this.def.apply(this.self());
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

    @Override
    public void saveConfig(T config) {
        L loader = this.loader.get();
        try {
            loader.save(loader.createNode().set(this.configClass, config));
        } catch (ConfigurateException exception) {
            throw new RuntimeException("Failed to save config", exception);
        }
    }

    @SuppressWarnings("unchecked")
    protected H self() {
        return (H) this;
    }

}
