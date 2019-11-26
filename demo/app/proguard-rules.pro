-renamesourcefileattribute SourceFile
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable,keepattributes
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Signature
-keepattributes *Annotation*
-dontshrink

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-ignorewarnings

-keep public class * extends android.app.Application


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }


-keep class javassist.** { *; }

-keep class javassist.util.HotSwapper.** { *;}
-keep class org.apache.harmony.beans.** { *; }
-keep class org.json.simple.** { *; }
-keep class junit.** { *; }
-keep class org.junit.** { *; }
-keep class * extends android.support.v4.app.FragmentActivity




-keepclassmembers class **.R$* {
  public static <fields>;
}


-assumenosideeffects class android.util.Log {
      public static boolean isLoggable(java.lang.String,int);
      public static int v(...);
      public static int i(...);
      public static int w(...);
      public static int d(...);
     public static int e(...);
}


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService



-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembers class com.stream.NewAllStreamParser {
   public void callBackData(...);
   public void callBackDataEx(...);
}


-keep class com.Player.Source.** { *;}

-keep class com.mp4.maker.**

-keep class com.Player.web.** {
   public *;
}
-keep class com.Player.Core.** {
   public *;
}
-keep class com.stream.FileDownloadParser {
   public *;
}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }


-keep class com.tool.websocket.** { *; }
-keep class com.macrovideo.smartlink.** { *; }


#个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

#云存储加密ESEncAndDec，不需要用到不用加
-keep class com.cryptlib.tool.** { *; }
-keep class org.json.simple.** { *; }
-keep class net.sourceforge.zbar.** { *; }