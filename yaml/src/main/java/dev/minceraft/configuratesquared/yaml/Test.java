package dev.minceraft.configuratesquared.yaml;

import dev.minceraft.configuratesquared.core.holder.simplified.IHookedConfigHolder;
import dev.minceraft.configuratesquared.core.serializer.Serializer;
import dev.minceraft.configuratesquared.core.serializer.SerializerContext;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.nio.file.Path;

public class Test {

    public static class Config {

        private int value;
        private final String programmArg;

        public Config(String programmArg) {
            this.programmArg = programmArg;
        }
    }

    @NullMarked
    public static class ConfigSerializer implements Serializer<Config>{

        @Override
        public Config deserialize(Type type, ConfigurationNode node, SerializerContext ctx) throws SerializationException {
            String programmArg = ctx.getOrThrow("programmArg", String.class);
            Config config = new Config(programmArg);
            config.value = node.node("value").getInt(0);

            ctx.putIfAbsent("for.future.use", config.value); // Store the value in the context for other serializers to use if needed

            return config;
        }

        @Override
        public void serialize(Type type, Config obj, ConfigurationNode node, SerializerContext ctx) throws SerializationException {
            node.node("value").set(obj.value);
        }
    }

    public void foo(Path path) {
        IHookedConfigHolder<Config> hooked = YamlHolder.builder() // Create a new config holder for YAML files
                .withConfigStore(path) // Use a file as the config store
                .withSerializer(Config.class, new ConfigSerializer()) // Register the custom serializer for the Config class
                .withInitialContext(ctx -> ctx.put("programmArg", "Hello World!")) // Add an initial value to the context
                .hooked(Config.class, null);

        hooked.reloadConfig(); // Load the config from the file
    }
}
