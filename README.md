# Configurate²

[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/MinceraftMC/ConfigurateSquared?style=flat-square)](https://github.com/MinceraftMC/PlayerCulling)
[![Apache 2.0 License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg?style=flat-square)](https://opensource.org/license/apache-2-0/)
[![Discord](https://img.shields.io/discord/1094193723191070793?style=flat-square&label=Discord&link=https%3A%2F%2Fdiscord.gg%2FzC8xjtSPKC)](https://discord.gg/zC8xjtSPKC)

## Description

Configurate² is a configuration library for Java based
on [SpongePowered/Configurate](https://github.com/SpongePowered/Configurate).
It provides simple methods for creating configuration environments, while still supporting all formats.
These include quality-of-life features and make setup easier.

## Features

- Fully support all formats supported by Configurate, including GSON, HOCON, JACKSON, XML and YAML.
- Reloadable configuration files with reload hooks.
- Easy to use configuration environment builders.
- Simple Serializers with SerializationContext, which allows storing additional information about the serialization
  process or pass initial values to the deserialization process.
- Still, full access to the configurate configuration builders and features, while using the simplified API.
- Multiple configuration store types, with different modes, e.g. read-only, in memory, etc.

## Usage

Configurate² is very easy to use. First add the dependency to your build system, then create a configuration environment
and load your configuration file.

### API setup

Add the repository and dependency to your build system. Replace `{your-format}` with the format you want to use:
`gson`, `hocon`, `jackson`, `xml` or `yaml`. If you want to use all formats, use `aio` instead.

<details>
<summary><strong>Maven</strong></summary>

```xml

<repositories>
    <repository>
        <id>minceraft</id>
        <url>https://repo.minceraft.dev/releases/</url>
    </repository>
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>dev.minceraft.configuratesquared</groupId>
        <artifactId>configuratesquared-{your-format}</artifactId>
        <version>1.0.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

</details>

<details>
<summary><strong>Gradle (groovy)</strong></summary>

```groovy
repositories {
    maven {
        url = 'https://repo.minceraft.dev/releases/'
        name = 'minceraft'
    }
}

dependencies {
    compileOnly 'dev.minceraft.configuratesquared:configuratesquared-{your-format}:1.0.1'
}
```

</details>

<details>
<summary><strong>Gradle (kotlin)</strong></summary>

```kotlin
repositories {
    maven("https://repo.minceraft.dev/releases/") {
        name = "minceraft"
    }
}

dependencies {
    compileOnly("dev.minceraft.configuratesquared:configuratesquared-{you-format}:1.0.1")
}
```

</details>

### API usage

Here are three examples of how to use Configurate²:

<details>
<summary><strong>Create a configuration environment and load your configuration file</strong></summary>

```java

@ConfigSerializable
public static class SimpleConfig { // Standard configurate config class

    public int configValue = 42;
}

public void foo(Path path) {
    IHookedConfigHolder<SimpleConfig> hooked = YamlHolder.builder() // Create a new config holder for YAML files
            .configureBuilder(builder -> builder.nodeStyle(NodeStyle.BLOCK).indent(2)) // Access to the vanilla builder
            .withAllDefaultSerializers() // Registers all default serializers shipped with Configurate²
            .withConfigStore(path) // Use a file as the config store
            .hooked(SimpleConfig.class, null);

    hooked.reloadConfig(); // Load the config from the file

    // Hook that runs when the config is reloaded
    hooked.addReloadHook(config -> {
        System.out.println("Config reloaded! New value: " + config.configValue);
    });

    // Hook that runs when the config is reloaded and also runs immediately with the current config
    hooked.addReloadHookAndRun(config -> {
        System.out.println("Config loaded! Current value: " + config.configValue);
    });

    // Reload the config from the file to trigger the hooks
    hooked.reloadConfig();

    int value = hooked.getConfig().configValue; // Access the current config value

    hooked.saveConfig(); // Save the current config to the file
}
```

</details>

<details>
<summary><strong>In memory config stores readonly</strong></summary>

```java

@ConfigSerializable
public static class ParsedData {

    public int parsedValue;
}

public void bar() {
    StringBufferStore store = new StringBufferStore(true);

    ICachedConfigHolder<ParsedData> parser = GsonHolder.builder()
            .withConfigStore(store)
            .cached(ParsedData.class, null);

    store.setBuffer("{\"parsedValue\": 42}"); // Set buffer

    ParsedData data = parser.loadConfig(); // Load config from memory buffer
    System.out.println(data.parsedValue); // Should print 42

    data.parsedValue = 100; // Modify the data

    parser.saveConfig(); // Nothing happens because the store is read-only

    store.setReadOnly(false); // Make the store writable
    parser.saveConfig(); // Now it saves the modified data back to the buffer
    System.out.println(store.getBuffer()); // Should print the updated JSON with parsedValue 100
}
```

</details>

<details>
<summary><strong>Use custom serializers with SerializationContext</strong></summary>

```java
public static class Config {

    private int value;
    private final String programmArg;

    public Config(String programmArg) {
        this.programmArg = programmArg;
    }
}

@NullMarked
public static class ConfigSerializer implements Serializer<Config> {

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
```

</details>

## Building

1. Clone the project (`git clone https://github.com/MinceraftMC/ConfigurateSquared.git`)
2. Go to the cloned directory (`cd ConfigurateSquared`)
3. Build the jar (`./gradlew build` on Linux/MacOS, `gradlew build` on Windows)

The ConfigurateSquared jars can be found in the `build/libs` directories of the modules named after the format they
support. `aio` contains all formats.

### Contributing

If you want to contribute to ConfigurateSquared, feel free to fork the repository and create a pull request.
Please make sure to follow the code style and conventions used in the project. If you have any questions or need help,
feel free to ask in our [Discord](https://discord.gg/zC8xjtSPKC).

