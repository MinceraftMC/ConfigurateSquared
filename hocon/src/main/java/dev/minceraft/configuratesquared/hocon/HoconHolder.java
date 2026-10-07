package dev.minceraft.configuratesquared.hocon;

import dev.minceraft.configuratesquared.core.builder.ConfigHolders;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class HoconHolder {

    private HoconHolder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConfigHolders.Builder<HoconConfigurationLoader, CommentedConfigurationNode, HoconConfigurationLoader.Builder, Builder> {

        public Builder() {
            super(HoconConfigurationLoader.builder());
        }
    }
}
