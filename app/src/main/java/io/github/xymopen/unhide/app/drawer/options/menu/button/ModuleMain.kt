package io.github.xymopen.unhide.app.drawer.options.menu.button

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

class ModuleMain(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {
    override fun onPackageLoaded(param: PackageLoadedParam) {

    }
}
