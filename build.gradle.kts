import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin

plugins {
    alias(libs.plugins.gradleup.shadow) apply false
}

allprojects {
    group = "dev.minceraft"
    version = "1.0.0"
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<ShadowPlugin>()

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<Jar> {
        archiveBaseName = "${rootProject.name.lowercase()}-${project.name}"
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}