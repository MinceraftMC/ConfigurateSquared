package dev.minceraft.configuratesquared.gson;

import dev.minceraft.configuratesquared.core.builder.ConfigHolders;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

public final class GsonHolder {

    private GsonHolder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConfigHolders.Builder<GsonConfigurationLoader, BasicConfigurationNode, GsonConfigurationLoader.Builder, Builder> {

        public Builder() {
            super(GsonConfigurationLoader.builder());
        }
    }
}
