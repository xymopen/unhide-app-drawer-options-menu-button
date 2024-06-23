package io.github.xymopen.unhide.app.drawer.options.menu.button

import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class AllAppsHostLayout : XC_MethodHook() {
    override fun afterHookedMethod(mhparam: MethodHookParam) {
        val mOptionsMenuButton =
            XposedHelpers.getObjectField(
                mhparam.thisObject,
                "mOptionsMenuButton"
            ) as ImageButton

        mOptionsMenuButton.setVisibility(View.VISIBLE)
    }
}

fun getHomescreenLockEnabled(
    mhparam: MethodHookParam,
    classLoader: ClassLoader
): Boolean {
    val context = ((mhparam.thisObject) as FrameLayout).context
    val HomeSettingsSharedPreferences = XposedHelpers.findClass(
        "com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences",
        classLoader
    )
    val getHomescreenLockEnabled = XposedHelpers.findMethodExact(
        HomeSettingsSharedPreferences ,
        "getHomescreenLockEnabled",
        Context::class.java
    )

    return getHomescreenLockEnabled.invoke(HomeSettingsSharedPreferences, context) as Boolean
}

class AllAppsHostOnPrepareOptionsMenu(val classLoader: ClassLoader) : XC_MethodHook() {
    override fun afterHookedMethod(mhparam: MethodHookParam) {
        if (mhparam.result as Boolean) {
            if (!getHomescreenLockEnabled(mhparam, classLoader)) {
                val menu = mhparam.args[0] as Menu

                menu.setGroupEnabled(0, true)
            }
        }
    }
}

class ModuleMain : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == "com.lge.launcher3") {
            onMatchPackage(lpparam)
        }
    }

    fun onMatchPackage(lpparam: LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.lge.launcher3.allapps.AllAppsHost",
            lpparam.classLoader,
            "layout",
            AllAppsHostLayout()
        )
        XposedHelpers.findAndHookMethod(
            "com.lge.launcher3.allapps.AllAppsHost",
            lpparam.classLoader,
            "onPrepareOptionsMenu",
            Menu::class.java,
            AllAppsHostOnPrepareOptionsMenu(lpparam.classLoader)
        )
    }
}
