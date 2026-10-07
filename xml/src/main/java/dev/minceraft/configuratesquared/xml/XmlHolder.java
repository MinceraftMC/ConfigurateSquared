package dev.minceraft.configuratesquared.xml;

import dev.minceraft.configuratesquared.core.builder.ConfigHolders;
import org.spongepowered.configurate.AttributedConfigurationNode;
import org.spongepowered.configurate.xml.XmlConfigurationLoader;

public final class XmlHolder {

    private XmlHolder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ConfigHolders.Builder<XmlConfigurationLoader, AttributedConfigurationNode, XmlConfigurationLoader.Builder, Builder> {

        public Builder() {
            super(XmlConfigurationLoader.builder());
        }
    }
}