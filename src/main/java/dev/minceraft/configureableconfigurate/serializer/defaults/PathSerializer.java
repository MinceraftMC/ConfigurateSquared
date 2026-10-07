package dev.minceraft.configureableconfigurate.serializer.defaults;

import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.function.Predicate;

@NullMarked
public final class PathSerializer extends ScalarSerializer<Path> {

    public static final TypeSerializer<Path> INSTANCE = new PathSerializer();

    private PathSerializer() {
        super(new TypeToken<Path>() {});
    }

    @Override
    public Path deserialize(Type type, Object obj) throws SerializationException {
        return Path.of(String.valueOf(obj));
    }

    @Override
    protected Object serialize(Path item, Predicate<Class<?>> typeSupported) {
        return item.toString();
    }
}
