pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        // Repositório Maven do MapLibre - necessário para SDK gratuito de mapas
        maven {
            url = uri("https://maven.maplibre.org/releases/")
        }
    }
}

rootProject.name = "AgroColetor"
include(":app")
