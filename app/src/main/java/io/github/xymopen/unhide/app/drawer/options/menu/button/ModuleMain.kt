package io.github.xymopen.unhide.app.drawer.options.menu.button

import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

private lateinit var HomeSettingsSharedPreferences: Class<*>

val AllAppsHostLayout = XposedInterface.Hooker { chain ->
    chain.proceed()
    val thisObject = chain.thisObject!!
    val mOptionsMenuButtonField =
        thisObject.javaClass.getDeclaredField("mOptionsMenuButton")
    mOptionsMenuButtonField.isAccessible = true

    val mOptionsMenuButton = mOptionsMenuButtonField.get(thisObject) as
            ImageButton

    mOptionsMenuButton.setVisibility(View.VISIBLE)
}

fun getHomescreenLockEnabled(chain: XposedInterface.Chain): Boolean {
    val context = ((chain.thisObject) as FrameLayout).context
    val getHomescreenLockEnabled = HomeSettingsSharedPreferences.getDeclaredMethod(
        "getHomescreenLockEnabled",
        Context::class.java
    )

    return getHomescreenLockEnabled.invoke(
        chain.thisObject,
        context
    ) as Boolean
}

val AllAppsHostOnPrepareOptionsMenu = XposedInterface.Hooker { chain ->
    val result = chain.proceed() as Boolean
    if (result) {
        if (!getHomescreenLockEnabled(chain)) {
            val menu = chain.args[0] as Menu

            menu.setGroupEnabled(0, true)
        }
    }
    return@Hooker result
}

class ModuleMain : XposedModule() {
    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName == "com.lge.launcher3") {
            this.onMatchPackage(param)
        }
    }

    fun onMatchPackage(param: PackageReadyParam) {
        val classLoader = param.classLoader

        val AllAppsHostClass = classLoader.loadClass("com.lge.launcher3.allapps.AllAppsHost")
        HomeSettingsSharedPreferences =
            classLoader.loadClass("com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences")

        this.hook(AllAppsHostClass.getDeclaredMethod("layout"))
            .intercept(AllAppsHostLayout)

        this.hook(
            AllAppsHostClass.getDeclaredMethod(
                "onPrepareOptionsMenu",
                Menu::class.java
            )
        ).intercept(AllAppsHostOnPrepareOptionsMenu)
    }
}
