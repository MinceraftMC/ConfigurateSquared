package dev.minceraft.configuratesquared.core.holder;

import dev.minceraft.configuratesquared.core.holder.simplified.IBasicConfigHolder;
import dev.minceraft.configuratesquared.core.serializer.InternalSerializer;
import dev.minceraft.configuratesquared.core.serializer.Serializer;
import dev.minceraft.configuratesquared.core.serializer.SerializerContext;
import dev.minceraft.configuratesquared.core.stores.IConfigStore;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
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
    private final Function<H, @Nullable T> def;
    private final Supplier<L> loader;
    private final @Nullable Consumer<SerializerContext> contextInitializer;
    private final ThreadLocal<@MonotonicNonNull SerializerContext> context = new ThreadLocal<>();


    public BasicConfigHolder(
            Class<T> configClass,
            Function<H, @Nullable T> def,
            IConfigStore store,
            Supplier<B> loaderBuilder,
            @Nullable Consumer<SerializerContext> contextInitializer,
            Map<TypeToken<?>, Serializer<?>> serializers
    ) {
        this.configClass = configClass;
        this.def = def;
        this.contextInitializer = contextInitializer;
        this.loader = () -> {
            B builder = loaderBuilder.get();

            InternalSerializer.setupSerializers(serializers, this, builder);

            if (store.hasReader()) {
                builder.source(() -> Objects.requireNonNull(store.getReader()));
            }
            if (store.hasWriter()) {
                builder.sink(() -> Objects.requireNonNull(store.getWriter()));
            }
            return builder.build();
        };
    }

    private void setupContext() {
        SerializerContext ctx = new SerializerContext();
        if (this.contextInitializer != null) {
            this.contextInitializer.accept(ctx);
        }
        this.context.set(ctx);
    }

    @Override
    public @Nullable T loadConfig(boolean saveAfterLoad) {
        L loader = this.loader.get();
        T config;

        if (!loader.canLoad()) {
            config = this.def.apply(this.self());
        } else {
            try {
                setupContext();
                config = loader.load().get(this.configClass);
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
        if (!loader.canSave()) {
            return;
        }
        try {
            setupContext();
            loader.save(loader.createNode().set(this.configClass, config));
        } catch (ConfigurateException exception) {
            throw new RuntimeException("Failed to save config", exception);
        }
    }

    @Override
    @ApiStatus.Internal
    public SerializerContext getContext() {
        return this.context.get();
    }

    @SuppressWarnings("unchecked")
    protected H self() {
        return (H) this;
    }
}
