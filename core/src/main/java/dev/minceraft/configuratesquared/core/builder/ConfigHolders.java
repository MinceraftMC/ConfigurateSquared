package dev.minceraft.configuratesquared.core.builder;

import dev.minceraft.configuratesquared.core.holder.BasicConfigHolder;
import dev.minceraft.configuratesquared.core.holder.CachedConfigHolder;
import dev.minceraft.configuratesquared.core.holder.HookedConfigHolder;
import dev.minceraft.configuratesquared.core.holder.simplified.IBasicConfigHolder;
import dev.minceraft.configuratesquared.core.holder.simplified.ICachedConfigHolder;
import dev.minceraft.configuratesquared.core.holder.simplified.IHookedConfigHolder;
import dev.minceraft.configuratesquared.core.serializer.ConfigurateSerializer;
import dev.minceraft.configuratesquared.core.serializer.Serializer;
import dev.minceraft.configuratesquared.core.serializer.SerializerCollection;
import dev.minceraft.configuratesquared.core.serializer.SerializerContext;
import dev.minceraft.configuratesquared.core.serializer.defaults.AddressSerializer;
import dev.minceraft.configuratesquared.core.serializer.defaults.EnumSerializer;
import dev.minceraft.configuratesquared.core.serializer.defaults.PathSerializer;
import dev.minceraft.configuratesquared.core.stores.IConfigStore;
import dev.minceraft.configuratesquared.core.stores.PathStore;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.ScopedConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@NullMarked
public final class ConfigHolders {

    public static final SerializerCollection DEFAULT_SERIALIZERS = SerializerCollection.builder()
            .with(InetSocketAddress.class, AddressSerializer.INSTANCE)
            .with(new TypeToken<Enum<?>>() {}, EnumSerializer.INSTANCE)
            .with(Path.class, PathSerializer.INSTANCE)
            .build();

    private ConfigHolders() {
    }

    public static <L extends AbstractConfigurationLoader<N>, N extends ScopedConfigurationNode<N>, B extends AbstractConfigurationLoader.Builder<B, L>> Builder<L, N, B, ?> builder(B loaderBuilder) {
        return new Builder<>(loaderBuilder);
    }

    public static class Builder<
            L extends AbstractConfigurationLoader<N>,
            N extends ScopedConfigurationNode<N>,
            B extends AbstractConfigurationLoader.Builder<B, L>,
            C extends Builder<L, N, B, C>
            > {

        protected final B builder;
        protected final Map<TypeToken<?>, Serializer<?>> serializers = new LinkedHashMap<>();
        protected @Nullable Consumer<SerializerContext> contextInitializer;
        protected @MonotonicNonNull IConfigStore configStore;

        public Builder(B builder) {
            this.builder = builder;
        }

        @SuppressWarnings("unchecked")
        protected C self() {
            return (C) this;
        }

        public C withSerializers(SerializerCollection collection) {
            collection.add(this);
            return this.self();
        }

        public <T> C withSerializer(Class<T> clazz, TypeSerializer<T> serializer) {
            return this.withSerializer(TypeToken.get(clazz), serializer);
        }

        public <T> C withSerializer(TypeToken<T> token, TypeSerializer<T> serializer) {
            return this.withSerializer(token, new ConfigurateSerializer<>(serializer));
        }

        public <T> C withSerializer(Class<T> clazz, Serializer<T> serializer) {
            return this.withSerializer(TypeToken.get(clazz), serializer);
        }

        public <T> C withSerializer(TypeToken<T> token, Serializer<T> serializer) {
            this.serializers.put(token, serializer);
            return this.self();
        }

        public C configureBuilder(Consumer<B> consumer) {
            consumer.accept(this.builder);
            return this.self();
        }

        public C configureDefaultSerializers(UnaryOperator<ConfigurationOptions> consumer) {
            this.builder.defaultOptions(consumer);
            return this.self();
        }

        public C withDefaultSerializers(Consumer<DefaultSerializerBuilder<C>> consumer) {
            consumer.accept(new DefaultSerializerBuilder<>(this.self()));
            return this.self();
        }

        public C withAllDefaultSerializers() {
            return this.withSerializers(DEFAULT_SERIALIZERS);
        }

        public C withInitialContext(@Nullable Consumer<SerializerContext> contextInitializer) {
            this.contextInitializer = contextInitializer;
            return this.self();
        }

        public C withConfigStore(IConfigStore store) {
            this.configStore = store;
            return this.self();
        }

        public C withConfigStore(Path path) {
            this.configStore = new PathStore(path, true, false);
            return this.self();
        }

        public C withConfigStore(Path path, boolean createIfNotExists, boolean readOnly) {
            this.configStore = new PathStore(path, createIfNotExists, readOnly);
            return this.self();
        }

        public <H extends BasicConfigHolder<H, T, L, B>, T> IBasicConfigHolder<T> basic(Class<T> configClass, @Nullable Function<H, T> def) {
            return new BasicConfigHolder<H, T, L, B>(
                    configClass,
                    def == null ? h -> null : def,
                    Objects.requireNonNull(this.configStore, "Config store is required"),
                    () -> this.builder
            );
        }

        public <H extends CachedConfigHolder<H, T, L, B>, T> ICachedConfigHolder<T> cached(Class<T> configClass, @Nullable Function<H, T> def) {
            return new CachedConfigHolder<H, T, L, B>(
                    configClass,
                    def == null ? h -> null : def,
                    Objects.requireNonNull(this.configStore, "Config store is required"),
                    () -> this.builder
            );
        }

        public <H extends HookedConfigHolder<H, T, L, B>, T> IHookedConfigHolder<T> hooked(Class<T> configClass, @Nullable Function<H, T> def) {
            return new HookedConfigHolder<H, T, L, B>(
                    configClass,
                    def == null ? h -> null : def,
                    Objects.requireNonNull(this.configStore, "Config store is required"),
                    () -> this.builder);
        }
    }
}







