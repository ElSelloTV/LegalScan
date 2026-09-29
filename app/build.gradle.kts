plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Datos de firma de release: SIEMPRE afuera del repo. Se toman de propiedades de Gradle
// (project.findProperty), que normalmente viven en ~/.gradle/gradle.properties (carpeta
// personal del usuario, nunca en el proyecto) o se pasan con -P al invocar Gradle.
// Sin esas propiedades, el build type "release" queda simplemente sin firmar.
val releaseStoreFile = project.findProperty("legalscanStoreFile") as String?

android {
    namespace = "ar.com.elsellotv.legalscan"
    compileSdk = 36

    defaultConfig {
        applicationId = "ar.com.elsellotv.legalscan"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = project.findProperty("legalscanStorePassword") as String
                keyAlias = project.findProperty("legalscanKeyAlias") as String
                keyPassword = project.findProperty("legalscanKeyPassword") as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            if (releaseStoreFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Escaneo de documentos con detección de bordes y recorte automático (Google Play services)
    implementation("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0-beta1")

    // Único anuncio de la app (App Open Ad al inicio) + consentimiento (UMP, exigido por Google en UE/Reino Unido/California)
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
