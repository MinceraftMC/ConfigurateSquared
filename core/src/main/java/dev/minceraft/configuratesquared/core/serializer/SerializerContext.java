package dev.minceraft.configuratesquared.core.serializer;

import dev.minceraft.configuratesquared.core.holder.simplified.IBasicConfigHolder;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Access to this context is <b>NOT THREAD SAFE!</b>
 */
@NullMarked
public class SerializerContext {

    private final Map<String, Object> context = new HashMap<>();

    @Contract("!null, _ -> !null; null, _ -> null")
    @SuppressWarnings("unchecked") // not unchecked
    private static <T> T cast(@Nullable Object obj, TypeToken<T> token) {
        Class<T> erasedClass = (Class<T>) GenericTypeReflector.erase(token.getType());
        return erasedClass.cast(obj);
    }

    public void put(String key, Object value) {
        this.context.put(key, value);
    }

    public void putIfAbsent(String key, Object value) {
        this.context.putIfAbsent(key, value);
    }

    public <T> Optional<T> getOptional(String key, Class<T> clazz) {
        return this.getOptional(key, TypeToken.get(clazz));
    }

    public <T> Optional<T> getOptional(String key, TypeToken<T> token) {
        T value = this.get(key, token, null);
        return Optional.ofNullable(value);
    }

    public <T> T getOrThrow(String key, Class<T> clazz) {
        return this.getOrThrow(key, TypeToken.get(clazz));
    }

    public <T> T getOrThrow(String key, TypeToken<T> token) {
        T value = this.get(key, token, null);
        if (value == null) {
            throw new IllegalStateException("Encountered null while getting " + key
                    + " with type " + token.getType() + " from serializer context");
        }
        return value;
    }

    public <T> @Nullable T get(String key, Class<T> clazz) {
        return this.get(key, TypeToken.get(clazz));
    }

    @Contract("_, _, !null -> !null")
    public <T> @Nullable T get(String key, Class<T> clazz, @Nullable T defaultValue) {
        return this.get(key, TypeToken.get(clazz), defaultValue);
    }

    public <T> @Nullable T get(String key, TypeToken<T> token) {
        return this.get(key, token, null);
    }

    @Contract("_, _, !null -> !null")
    public <T> @Nullable T get(String key, TypeToken<T> token, @Nullable T defaultValue) {
        T obj = cast(this.context.get(key), token);
        return obj == null ? defaultValue : obj;
    }

    public <T> T getOrCompute(String key, Class<T> clazz, Function<String, T> ctor) {
        return this.getOrCompute(key, TypeToken.get(clazz), ctor);
    }

    public <T> T getOrCompute(String key, TypeToken<T> token, Function<String, T> ctor) {
        return cast(this.context.computeIfAbsent(key, ctor), token);
    }
}
