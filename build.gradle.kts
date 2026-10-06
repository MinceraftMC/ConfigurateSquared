plugins {
    id("java-library")

    alias(libs.plugins.gradleup.shadow)
}

group = "dev.minceraft"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.bundles.configurate)
    api(libs.jspecify)
}