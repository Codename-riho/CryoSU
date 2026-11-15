package org.cryosu.espfix;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * CVE-2024-43093: pemeriksaan path ExternalStorageProvider dilewati dengan karakter Unicode tak
 * terlihat. Untuk setiap pemeriksaan, bila argumennya mengandung karakter itu, pemeriksaan asli
 * dijalankan ulang dengan argumen yang sudah dibersihkan; kalau hasilnya "dibatasi", hasil itu dipakai.
 */
public class Hook implements IXposedHookLoadPackage {
    private static final String TARGET_PACKAGE = "com.android.externalstorage";
    private static final String TARGET_CLASS = "com.android.externalstorage.ExternalStorageProvider";

    private static final XC_MethodHook RECHECK = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (Boolean.TRUE.equals(param.getResult())) return;
            Object[] args = param.args;
            Object[] clean = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    String s = (String) args[i];
                    String stripped = PathSanitizer.strip(s);
                    if (stripped != s) {
                        if (clean == null) clean = args.clone();
                        clean[i] = stripped;
                    }
                }
            }
            if (clean == null) return;
            try {
                Object again = XposedBridge.invokeOriginalMethod(param.method, param.thisObject, clean);
                if (Boolean.TRUE.equals(again)) param.setResult(Boolean.TRUE);
            } catch (Throwable t) {
                XposedBridge.log("ESPUnicodeFix: recheck failed: " + t);
            }
        }
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) return;
        Class<?> cls = XposedHelpers.findClassIfExists(TARGET_CLASS, lpparam.classLoader);
        if (cls == null) {
            XposedBridge.log("ESPUnicodeFix: " + TARGET_CLASS + " not found");
            return;
        }
        hook(cls, "shouldHideDocument", String.class);
        hook(cls, "shouldBlockDirectoryFromTree", String.class);
        hook(cls, "isRestrictedPath", String.class, String.class);
    }

    private static void hook(Class<?> cls, String name, Object... types) {
        try {
            Object[] all = new Object[types.length + 1];
            System.arraycopy(types, 0, all, 0, types.length);
            all[types.length] = RECHECK;
            XposedHelpers.findAndHookMethod(cls, name, all);
        } catch (Throwable t) {
            XposedBridge.log("ESPUnicodeFix: cannot hook " + name + ": " + t);
        }
    }
}
