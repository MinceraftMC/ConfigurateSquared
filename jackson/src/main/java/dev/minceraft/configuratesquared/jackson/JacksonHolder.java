package dev.minceraft.configuratesquared.jackson;

import dev.minceraft.configuratesquared.core.builder.ConfigHolders;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;

public final class JacksonHolder {

    private JacksonHolder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConfigHolders.Builder<JacksonConfigurationLoader, BasicConfigurationNode, JacksonConfigurationLoader.Builder, Builder> {

        public Builder() {
            super(JacksonConfigurationLoader.builder());
        }
    }
}