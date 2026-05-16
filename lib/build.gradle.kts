import org.gradle.kotlin.dsl.version

plugins {
    id("com.vanniktech.maven.publish") version "0.38.0"
}

group = "io.github.yours-valentiine"
version = "0.2.0-beta02"

/*mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(project.group.toString(), "hyperkey", project.version.toString())

    pom {
        name = "HyperKey"
        description = "A Kotlin DSL for declarative keyboard shortcut handling in Compose Multiplatform"
        url = "https://github.com/yours-valentiine/HyperKey"

        licenses {
            license {
                name = "BSD 3-Clause License"
                url = "https://opensource.org/license/bsd-3-clause"
            }
        }

        developers {
            developer {
                id = "yours.valentiine"
                name = "yours.valentiine"
            }
        }

        scm {
            url = "https://github.com/yours-valentiine/HyperKey"
            connection = "https://github.com/yours-valentiine/HyperKey.git"
        }
    }
}*/
