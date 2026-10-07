package dev.minceraft.configuratesquared.core.serializer;

import dev.minceraft.configuratesquared.core.holder.simplified.IBasicConfigHolder;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.Map;

@NullMarked
@ApiStatus.Internal
public final class InternalSerializer<T> implements TypeSerializer<T> {

    private final Serializer<T> serializer;
    private final IBasicConfigHolder<?> holder;

    InternalSerializer(Serializer<T> serializer, IBasicConfigHolder<?> holder) {
        this.serializer = serializer;
        this.holder = holder;
    }

    @ApiStatus.Internal
    public static void setupSerializers(
            Map<TypeToken<?>, Serializer<?>> serializers,
            IBasicConfigHolder<?> holder,
            AbstractConfigurationLoader.Builder<?, ?> builder
    ) {
        builder.defaultOptions(options -> options.serializers(build ->
                serializers.entrySet().forEach(entry -> registerSerializer(entry, holder, build))));
    }

    @SuppressWarnings("unchecked")
    static <T> void registerSerializer(
            Map.Entry<TypeToken<?>, Serializer<?>> entry,
            IBasicConfigHolder<?> holder,
            TypeSerializerCollection.Builder builder
    ) {
        TypeToken<T> type = (TypeToken<T>) entry.getKey();
        Serializer<T> serializer = (Serializer<T>) entry.getValue();
        builder.register(type, serializer instanceof ConfigurateSerializer<T>
                ? ((ConfigurateSerializer<T>) serializer).delegate()
                : new InternalSerializer<>(serializer, holder));
    }

    @Override
    public @Nullable T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) {
            return null;
        }
        return this.serializer.deserialize(type, node, this.holder.getContext());
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }
        this.serializer.serialize(type, obj, node, this.holder.getContext());
    }
}
