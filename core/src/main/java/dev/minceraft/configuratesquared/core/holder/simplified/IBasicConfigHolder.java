package dev.minceraft.configuratesquared.core.holder.simplified;

import dev.minceraft.configuratesquared.core.serializer.SerializerContext;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface IBasicConfigHolder<T> {

    @Nullable
    T loadConfig(boolean saveAfterLoad);

    @Nullable
    default T loadConfig() {
        return this.loadConfig(true);
    }

    void saveConfig(T config);

    @ApiStatus.Internal
    SerializerContext getContext();
}
