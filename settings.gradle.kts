pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://www.jitpack.io") }
        mavenLocal()
    }
}
// Patched Fossify commons (fake-copy warnings removed), built from the submodule.
includeBuild("commons-bgsms") {
    dependencySubstitution {
        substitute(module("org.fossify:commons")).using(project(":commons"))
    }
}
include(":app")
