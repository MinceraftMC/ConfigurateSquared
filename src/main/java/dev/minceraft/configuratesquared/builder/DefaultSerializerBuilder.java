package dev.minceraft.configuratesquared.builder;

import dev.minceraft.configuratesquared.serializer.defaults.AddressSerializer;
import dev.minceraft.configuratesquared.serializer.defaults.EnumSerializer;
import dev.minceraft.configuratesquared.serializer.defaults.PathSerializer;
import io.leangen.geantyref.TypeToken;

import java.net.InetSocketAddress;
import java.nio.file.Path;

public final class DefaultSerializerBuilder<B extends ConfigHolders.Builder<?, ?, ?, ?>> {

    private final B builder;

    DefaultSerializerBuilder(B builder) {
        this.builder = builder;
    }

    public DefaultSerializerBuilder<B> withAddress() {
        this.builder.withSerializer(InetSocketAddress.class, AddressSerializer.INSTANCE);
        return this;
    }

    public DefaultSerializerBuilder<B> withEnum() {
        this.builder.withSerializer(new TypeToken<Enum<?>>() {}, EnumSerializer.INSTANCE);
        return this;
    }

    public DefaultSerializerBuilder<B> withPath() {
        this.builder.withSerializer(Path.class, PathSerializer.INSTANCE);
        return this;
    }

    public DefaultSerializerBuilder<B> withAll() {
        this.builder.withAllDefaults();
        return this;
    }
}
