package ru.mr_hares.chooseMode.utils;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class ModrinthUpdate {
    private static final String API_URL = "https://api.modrinth.com/v2/project/{id}/version";
    private final String projectId;
    private final String loader;
    private final @Nullable String minecraftVersion;

    public ModrinthUpdate(String projectId, String loader) {
        this(projectId, loader, (String)null);
    }

    public ModrinthUpdate(String projectId, String loader, @Nullable String minecraftVersion) {
        super();
        this.projectId = projectId;
        this.loader = loader;
        this.minecraftVersion = minecraftVersion;
    }

    public void checkVersion(Consumer<String> consumer) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.modrinth.com/v2/project/{id}/version".replace("{id}", this.projectId))).GET().build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAcceptAsync((response) -> {
                if (response.statusCode() == 200) {
                    JsonArray versionsArray = JsonParser.parseString((String)response.body()).getAsJsonArray();
                    String latestVersion = this.getLatestVersion(versionsArray);
                    if (latestVersion != null) {
                        consumer.accept(latestVersion);
                    }
                }
            });
        } catch (Exception var4) {
        }

    }

    protected @Nullable String getLatestVersion(JsonArray versions) {
        return (String)versions.asList().stream().map(JsonElement::getAsJsonObject).filter(this::isVersionCompatible).map((version) -> version.get("version_number").getAsString()).map(this::getRawVersion).max(String::compareTo).orElse((String) null);
    }

    protected boolean isVersionCompatible(JsonObject version) {
        JsonArray versions = version.get("game_versions").getAsJsonArray();
        JsonArray loaders = version.get("loaders").getAsJsonArray();
        return (this.minecraftVersion == null || versions.contains(new JsonPrimitive(this.minecraftVersion))) && loaders.contains(new JsonPrimitive(this.loader));
    }

    public String getRawVersion(String version) {
        if (version.isEmpty()) {
            return version;
        } else {
            version = version.replaceAll("^\\D+", "");
            String[] split = version.split("\\+");
            return split[0];
        }
    }
}

