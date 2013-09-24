package org.fkfk.xposed.camera;

import android.content.res.XResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XSharedPreferences;

public class SoundHook implements IXposedHookZygoteInit, IXposedHookLoadPackage {
  private static XSharedPreferences pref;
  private static Boolean sw;
  private static Boolean vo;
  
  @Override
  public void initZygote(StartupParam startupParam) throws Throwable {
    pref = new XSharedPreferences("org.fkfk.xposed.camera");
    pref.reload();
    sw = Boolean.valueOf(pref.getBoolean("system-wide_setting", false));
    vo = Boolean.valueOf(pref.getBoolean("htccamera_setting", true));
    if( ! sw ){
      XResources.setSystemWideReplacement("android", "bool", "config_camera_sound_forced", false);
    }
  }

  public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
    if (!lpparam.packageName.equals("com.android.camera"))
      return;

    findAndHookMethod("com.android.camera.FeatureConfig", lpparam.classLoader, "forceSutterSound", new XC_MethodHook() {
      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if( vo ){
          param.setResult(Boolean.valueOf(false));
        }
      }
    });
  }
}
