package dev.minceraft.configuratesquared.serializer;

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

@NullMarked
public interface Serializer<T> {

    T deserialize(Type type, ConfigurationNode node, SerializerContext ctx) throws SerializationException;

    void serialize(Type type, T obj, ConfigurationNode node, SerializerContext ctx) throws SerializationException;
}
