plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "io.github.xymopen.unhide.app.drawer.options.menu.button"
	compileSdk = 34

	defaultConfig {
		applicationId = "io.github.xymopen.unhide.app.drawer.options.menu.button"
		minSdk = 27
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	packaging {
		resources {
			merges += "META-INF/xposed/*"
			excludes += "**"
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

dependencies {
	compileOnly(libs.libxposed.api)
	implementation(libs.libxposed.service)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}
