package com.mehmet_27.punishmanager.dependencies;

import java.util.Locale;

public enum Dependency {
    PROTOCOLIZE_BUNGEECORD(
            "dev.simplix",
            "protocolize-bungeecord",
            "2.1.2"
    );

    private final String mavenRepoPath;
    private final String version;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    Dependency(String groupId, String artifactId, String version) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                groupId.replace(".", "/"),
                artifactId,
                version,
                artifactId,
                version
        );
        this.version = version;
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public String getFileName() {
        String name = name().toLowerCase(Locale.ROOT).replace('_', '-');
        return name + "-" + this.version + ".jar";
    }
}
