package io.github.xymopen.unhide.app.drawer.options.menu.button

import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.XposedHooker

private lateinit var HomeSettingsSharedPreferences: Class<*>

@XposedHooker
class AllAppsHostLayout : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        @AfterInvocation
        fun after(callback: AfterHookCallback) {
            val thisObject = callback.thisObject!!
            val mOptionsMenuButtonField =
                thisObject.javaClass.getDeclaredField("mOptionsMenuButton")
            mOptionsMenuButtonField.isAccessible = true

            val mOptionsMenuButton = mOptionsMenuButtonField.get(thisObject) as
                    ImageButton

            mOptionsMenuButton.setVisibility(View.VISIBLE)
        }
    }
}

fun getHomescreenLockEnabled(callback: AfterHookCallback): Boolean {
    val context = ((callback.thisObject) as FrameLayout).context
    val getHomescreenLockEnabled = HomeSettingsSharedPreferences.getDeclaredMethod(
        "getHomescreenLockEnabled",
        Context::class.java
    )

    return getHomescreenLockEnabled.invoke(
        callback.thisObject,
        context
    ) as Boolean
}

@XposedHooker
class AllAppsHostOnPrepareOptionsMenu : XposedInterface.Hooker {
    companion object {
        @JvmStatic
        @AfterInvocation
        fun after(callback: AfterHookCallback) {
            if (callback.result as Boolean) {
                if (!getHomescreenLockEnabled(callback)) {
                    val menu = callback.args[0] as Menu

                    menu.setGroupEnabled(0, true)
                }
            }

        }
    }
}

class ModuleMain(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.lge.launcher3") {
            this.onMatchPackage(param)
        }
    }

    fun onMatchPackage(param: PackageLoadedParam) {
        val classLoader = param.classLoader

        val AllAppsHostClass = classLoader.loadClass("com.lge.launcher3.allapps.AllAppsHost")
        HomeSettingsSharedPreferences =
            classLoader.loadClass("com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences")

        this.hook(
            AllAppsHostClass.getDeclaredMethod("layout"),
            AllAppsHostLayout::class.java
        )

        this.hook(
            AllAppsHostClass.getDeclaredMethod(
                "onPrepareOptionsMenu",
                Menu::class.java
            ),
            AllAppsHostOnPrepareOptionsMenu::class.java
        )
    }
}
