package dev.minceraft.configuratesquared.serializer;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@ApiStatus.Internal
@NullMarked
public record ConfigurateSerializer<T>(TypeSerializer<T> delegate) implements Serializer<T> {

    @Override
    public T deserialize(Type type, ConfigurationNode node, SerializerContext ctx) throws SerializationException {
        return this.delegate.deserialize(type, node);
    }

    @Override
    public void serialize(Type type, T obj, ConfigurationNode node, SerializerContext ctx) throws SerializationException {
        this.delegate.serialize(type, obj, node);
    }
}