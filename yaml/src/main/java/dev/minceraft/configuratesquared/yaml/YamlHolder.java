package dev.minceraft.configuratesquared.yaml;

import dev.minceraft.configuratesquared.core.builder.ConfigHolders;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class YamlHolder {

    private YamlHolder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConfigHolders.Builder<YamlConfigurationLoader, CommentedConfigurationNode, YamlConfigurationLoader.Builder, Builder> {

        public Builder() {
            super(YamlConfigurationLoader.builder());
        }
    }
}