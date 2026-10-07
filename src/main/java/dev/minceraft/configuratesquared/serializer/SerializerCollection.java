package dev.minceraft.configuratesquared.serializer;

import dev.minceraft.configuratesquared.builder.ConfigHolders;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public final class SerializerCollection {

    private final List<SerializerEntry<?>> serializers;

    private SerializerCollection(List<SerializerEntry<?>> serializers) {
        this.serializers = List.copyOf(serializers);
    }

    public static SerializerCollection join(SerializerCollection... collections) {
        return join(List.of(collections));
    }

    public static SerializerCollection join(Iterable<SerializerCollection> collections) {
        Builder builder = builder();
        for (SerializerCollection collection : collections) {
            builder.withAll(collection);
        }
        return builder.build();
    }

    public static Builder builder() {
        return new Builder(null);
    }

    public Builder asBuilder() {
        return new Builder(this.serializers);
    }

    public void add(ConfigHolders.Builder<?, ?, ?, ?> builder) {
        for (SerializerEntry<?> serializer : this.serializers) {
            serializer.add(builder);
        }
    }

    TypeSerializerCollection asConfigurate() {
        TypeSerializerCollection.Builder builder = TypeSerializerCollection.builder();
        for (SerializerEntry<?> entry : this.serializers) {
            entry.asConfigurate(builder);
        }
        return builder.build();
    }

    @NullMarked
    public static final class Builder {

        private final List<SerializerEntry<?>> serializers = new ArrayList<>();

        private Builder(@Nullable List<SerializerEntry<?>> serializers) {
            if (serializers != null) {
                this.serializers.addAll(serializers);
            }
        }

        public SerializerCollection build() {
            return new SerializerCollection(this.serializers);
        }

        @Contract("_ -> this")
        public Builder withAll(Builder builder) {
            this.serializers.addAll(builder.serializers);
            return this;
        }

        @Contract("_ -> this")
        public Builder withAll(SerializerCollection serializers) {
            this.serializers.addAll(serializers.serializers);
            return this;
        }

        @Contract("_ -> this")
        public Builder withAll(Iterable<ScalarSerializer<?>> serializers) {
            for (ScalarSerializer<?> serializer : serializers) {
                this.with(serializer);
            }
            return this;
        }

        @Contract("_ -> this")
        public <T> Builder with(ScalarSerializer<T> serializer) {
            return this.with(serializer.type(), serializer);
        }

        @Contract("_, _ -> this")
        public <T> Builder with(Class<T> clazz, TypeSerializer<T> serializer) {
            return this.with(TypeToken.get(clazz), serializer);
        }

        @Contract("_, _ -> this")
        public <T> Builder with(TypeToken<T> type, TypeSerializer<T> serializer) {
            return this.with(type, new ConfigurateSerializer<>(serializer));
        }

        @Contract("_, _ -> this")
        public <T> Builder with(Class<T> clazz, Serializer<T> serializer) {
            return this.with(TypeToken.get(clazz), serializer);
        }

        @Contract("_, _ -> this")
        public <T> Builder with(TypeToken<T> type, Serializer<T> serializer) {
            this.serializers.add(new SerializerEntry<>(type, serializer));
            return this;
        }
    }

    @NullMarked
    private record SerializerEntry<T>(TypeToken<T> type, Serializer<T> serializer) {

        public void add(ConfigHolders.Builder<?, ?, ?, ?> builder) {
            builder.withSerializer(this.type, this.serializer);
        }

        private void asConfigurate(TypeSerializerCollection.Builder builder) {
            if (this.serializer instanceof ConfigurateSerializer<T>(TypeSerializer<T> delegate)) {
                builder.register(this.type, delegate);
            }
        }
    }
}
