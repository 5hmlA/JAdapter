plugins {
    id("com.android.library")
    id("kotlin-android")
}


rootProject.ext["GROUP_ID"] = "osp.sparkj.ui"
rootProject.ext["ARTIFACT_ID"] = "adapter"
rootProject.ext["VERSION"] = "2023.10.12"

//apply(from = "https://raw.githubusercontent.com/5hmlA/5hmlA/space/publish-plugin.gradle")
//apply(from = "../publish-plugin.gradle")


android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
//        consumerProguardFiles()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    productFlavors {
        //https://developer.android.google.cn/studio/build/build-variants
    }

    buildTypes {
        getByName("debug") {
            extra["alwaysUpdateBuildId"] = false
        }
        release {
            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
        jvmTarget = "17"
    }

    namespace = "sparkj.jadapter"
}

dependencies {
    api(libs.androidx.activity.ktx)
    api(libs.androidx.core.ktx)
    api(libs.lifecycle.runtime.ktx)
    api(libs.lifecycle.livedata.ktx)
    api(libs.lifecycle.viewmodel.ktx)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:+")
    implementation("androidx.recyclerview:recyclerview:+")
//    https://medium.com/androiddevelopers/jetpack-compose-interop-using-compose-in-a-recyclerview-569c7ec7a583
//    implementation 'androidx.customview:customview-poolingcontainer:1.0.0'//recycleview 1.3已经依赖了
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.bundles.androidx.benchmark)
}