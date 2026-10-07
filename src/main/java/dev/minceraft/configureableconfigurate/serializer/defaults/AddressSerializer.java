package dev.minceraft.configureableconfigurate.serializer.defaults;

import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@NullMarked
public final class AddressSerializer extends ScalarSerializer<InetSocketAddress> {

    public static final TypeSerializer<InetSocketAddress> INSTANCE = new AddressSerializer();
    private static final String PORT_REGEX = "(:(\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]))";
    private static final Pattern IPV6_REGEX = Pattern.compile("\\[(([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|([\\da-fA-F]{1,4}:){1,7}:|([\\da-fA-F]{1,4}:){1,6}:[\\da-fA-F]{1,4}|([\\da-fA-F]{1,4}:){1,5}(:[\\da-fA-F]{1,4}){1,2}|([\\da-fA-F]{1,4}:){1,4}(:[\\da-fA-F]{1,4}){1,3}|([\\da-fA-F]{1,4}:){1,3}(:[\\da-fA-F]{1,4}){1,4}|([\\da-fA-F]{1,4}:){1,2}(:[\\da-fA-F]{1,4}){1,5}|[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,6})|:((:[\\da-fA-F]{1,4}){1,7}|:)|fe80:(:[\\da-fA-F]{0,4}){0,4}%[\\da-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?\\d)?\\d)\\.){3}(25[0-5]|(2[0-4]|1?\\d)?\\d)|([\\da-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?\\d)?\\d)\\.){3}(25[0-5]|(2[0-4]|1?\\d)?\\d))]" + PORT_REGEX);

    private static final Map<InetSocketAddress, String> STRINGIFY_CACHE = new IdentityHashMap<>();

    private AddressSerializer() {
        super(new TypeToken<InetSocketAddress>() {});
    }

    @Override
    public InetSocketAddress deserialize(Type type, Object obj) throws SerializationException {
        String address = String.valueOf(obj);
        InetSocketAddress socketAddress;
        if (IPV6_REGEX.matcher(address).matches()) {
            int adrStart = address.indexOf('[') + 1, adrEnd = address.indexOf(']');
            int port = Integer.parseInt(address.substring(adrEnd + 1 /* ] */ + 1 /* : */));
            socketAddress = new InetSocketAddress(address.substring(adrStart, adrEnd), port);
        } else {
            int portIndex = address.indexOf(':');
            int port = Integer.parseInt(address.substring(portIndex + 1));
            socketAddress = new InetSocketAddress(address.substring(0, portIndex), port);
        }

        synchronized (STRINGIFY_CACHE) {
            STRINGIFY_CACHE.put(socketAddress, address);
        }
        return socketAddress;
    }

    @Override
    protected Object serialize(InetSocketAddress item, Predicate<Class<?>> typeSupported) {
        String cachedAddress;
        synchronized (STRINGIFY_CACHE) {
            cachedAddress = STRINGIFY_CACHE.get(item);
        }
        if (cachedAddress != null) {
            return cachedAddress;
        }

        String hostName = item.getHostName();
        if (hostName == null) {
            hostName = item.getAddress().getHostAddress();
            if (item.getAddress() instanceof Inet6Address) {
                hostName = "[" + hostName + "]";
            }
        }
        return hostName + ":" + item.getPort();
    }
}
