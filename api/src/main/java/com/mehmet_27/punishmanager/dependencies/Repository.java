package com.mehmet_27.punishmanager.dependencies;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public enum Repository {

    EXCEPTIONFLUG("https://mvn.exceptionflug.de/repository/exceptionflug-public/");

    private final String url;

    Repository(String url) {
        this.url = url;
    }

    private URLConnection openConnection(Dependency dependency) throws IOException {
        URL dependencyUrl = new URL(this.url + dependency.getMavenRepoPath());
        return dependencyUrl.openConnection();
    }

    @SuppressWarnings("UnstableApiUsage")
    public byte[] downloadRaw(Dependency dependency) throws DependencyDownloadException {
        try {
            URLConnection connection = openConnection(dependency);
            connection.setRequestProperty("User-Agent",
                    "PunishManager");
            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);
                if (bytes.length == 0) {
                    throw new DependencyDownloadException("Empty stream");
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new DependencyDownloadException(e);
        }
    }

    public byte[] download(Dependency dependency) throws DependencyDownloadException {

        return downloadRaw(dependency);
    }

    public void download(Dependency dependency, Path file) throws DependencyDownloadException {
        try {
            Files.write(file, download(dependency));
        } catch (IOException e) {
            throw new DependencyDownloadException(e);
        }
    }
}
