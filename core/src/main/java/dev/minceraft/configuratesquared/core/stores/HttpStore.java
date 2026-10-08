package dev.minceraft.configuratesquared.core.stores;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@NullMarked
public class HttpStore implements IConfigStore {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final @Nullable String getUrl;
    private final @Nullable String postUrl;
    private final @Nullable IHttpStoreAuth auth;

    public HttpStore(@Nullable String getUrl, @Nullable String postUrl, @Nullable IHttpStoreAuth auth) {
        this.getUrl = getUrl;
        this.postUrl = postUrl;
        this.auth = auth;
    }

    private HttpRequest.Builder createRequestBuilder() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        if (this.auth != null) {
            this.auth.applyAuth(builder);
        }
        return builder;
    }

    @Override
    public boolean hasReader() {
        return this.getUrl != null;
    }

    @Override
    public boolean hasWriter() {
        return this.postUrl != null;
    }

    @Override
    public @Nullable BufferedReader getReader() {
        if (this.getUrl == null) {
            return null;
        }
        HttpRequest request = createRequestBuilder().uri(URI.create(this.getUrl)).GET().build();
        HttpResponse<InputStream> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch configuration from " + this.getUrl + ": HTTP " + response.statusCode());
        }

        return new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
    }

    @Override
    public @Nullable BufferedWriter getWriter() {
        if (this.postUrl == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            @Override
            public void close() throws IOException {
                super.close();

                HttpRequest request = createRequestBuilder()
                        .uri(URI.create(HttpStore.this.postUrl))
                        .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                        .build();

                HttpResponse<Void> response = null;
                try {
                    response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(exception);
                }
                if (response.statusCode() != 200 && response.statusCode() != 201) {
                    throw new RuntimeException("Failed to post configuration to " + HttpStore.this.postUrl + ": HTTP " + response.statusCode());
                }
            }
        };
    }

    public interface IHttpStoreAuth {

        void applyAuth(HttpRequest.Builder requestBuilder);
    }

    public static class BasicAuth implements IHttpStoreAuth {

        private final String credentials;

        public BasicAuth(String credentials) {
            this.credentials = credentials;
        }

        public BasicAuth(String username, String password) {
            this(Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
        }

        @Override
        public void applyAuth(HttpRequest.Builder requestBuilder) {
            requestBuilder.setHeader("Authorization", "Basic " + this.credentials);
        }
    }

    public static class ApiKeyAuth implements IHttpStoreAuth {

        private final String headerName;
        private final String apiKey;

        public ApiKeyAuth(String headerName, String apiKey) {
            this.headerName = headerName;
            this.apiKey = apiKey;
        }

        @Override
        public void applyAuth(HttpRequest.Builder requestBuilder) {
            requestBuilder.header(this.headerName, this.apiKey);
        }
    }

    public static class BearerTokenAuth implements IHttpStoreAuth {

        private final String token;

        public BearerTokenAuth(String token) {
            this.token = token;
        }

        @Override
        public void applyAuth(HttpRequest.Builder requestBuilder) {
            requestBuilder.setHeader("Authorization", "Bearer " + this.token);
        }
    }
}
