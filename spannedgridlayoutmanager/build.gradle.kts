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

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.snap-one"
            artifactId = "SpannedGridLayoutManager"
            version = "4.1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
