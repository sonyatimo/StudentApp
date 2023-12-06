import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight") version "2.0.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("app.cash.sqldelight:sqlite-driver:2.0.1")
    api("app.cash.sqldelight:runtime:2.0.1")
    api("app.cash.sqldelight:coroutines-extensions:2.0.1")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            modules("java.sql")
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe)
            packageName = "StudentApp"
            packageVersion = "1.0.0"

            windows {
                packageVersion = "1.0.0"

                exePackageVersion = "1.0.0"
            }

            macOS {
                packageVersion = "1.0.0"

                dmgPackageVersion = "1.0.0"

                packageBuildVersion = "1.0.0"

                dmgPackageBuildVersion = "1.0.0"

            }
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.example.database")
        }
    }
}