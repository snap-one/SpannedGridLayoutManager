plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    `maven-publish`
}

android {
    namespace = "com.arasthel.spannedgridlayoutmanager"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.androidx.recyclerview)
}

// Single source of truth for the release version. CI publishes this verbatim to the release repo,
// or appends -SNAPSHOT (via -PpublishSnapshot=true) to publish to the snapshot repo — the
// destination below is chosen by the version suffix, so the two stay in sync.
val baseVersion = providers.gradleProperty("publishVersion").orNull ?: "4.1.0"
val isSnapshot = providers.gradleProperty("publishSnapshot").orNull == "true"
val publishVersion = if (isSnapshot) "$baseVersion-SNAPSHOT" else baseVersion

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.control4"
            artifactId = "spannedgridlayoutmanager"
            version = publishVersion

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("SpannedGridLayoutManager")
                description.set(
                    "RecyclerView LayoutManager with variable span sizes and free-space filling. " +
                        "Fork of arasthel/SpannedGridLayoutManager."
                )
                url.set("https://github.com/snap-one/SpannedGridLayoutManager")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/snap-one/SpannedGridLayoutManager/blob/main/LICENSE")
                    }
                }
            }
        }
    }
    repositories {
        // Destination is supplied at publish time (CI) — no internal hostnames or credentials
        // live in this public repo. The release/snapshot repo is chosen by the version suffix,
        // which covers both -PpublishSnapshot=true and an explicit -SNAPSHOT publishVersion.
        val repoUrl = if (publishVersion.endsWith("-SNAPSHOT")) {
            providers.gradleProperty("publishSnapshotUrl").orNull
        } else {
            providers.gradleProperty("publishReleaseUrl").orNull
        }
        if (repoUrl != null) {
            maven {
                url = uri(repoUrl)
                credentials {
                    username = providers.gradleProperty("publishUser").orNull
                    password = providers.gradleProperty("publishPassword").orNull
                }
            }
        }
    }
}
