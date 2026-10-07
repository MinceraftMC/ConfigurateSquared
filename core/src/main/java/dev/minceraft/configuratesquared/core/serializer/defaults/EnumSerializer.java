package dev.minceraft.configuratesquared.core.serializer.defaults;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@NullMarked
public final class EnumSerializer extends ScalarSerializer<Enum<?>> {

    public static final TypeSerializer<Enum<?>> INSTANCE = new EnumSerializer();

    private EnumSerializer() {
        super(new TypeToken<Enum<?>>() {});
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Enum<?> deserialize(Type type, Object obj) throws SerializationException {
        Class<?> erased = GenericTypeReflector.erase(type);
        Class<? extends Enum> enumClass = erased.asSubclass(Enum.class);
        return Enum.valueOf(enumClass, String.valueOf(obj));
    }

    @Override
    protected Object serialize(Enum<?> item, Predicate<Class<?>> typeSupported) {
        return item.name();
    }
}
