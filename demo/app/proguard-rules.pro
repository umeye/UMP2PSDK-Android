#其它需要混淆的类参照网上写法，这里不多阐述

#aar引用需要的混淆
-keep class com.audio.** { *; }
-keep class com.audio2.** { *; }
-keep class com.hkstreamSB.** { *; }
-keep class com.mp4.** { *; }
-keep class com.Player.** { *; }
-keep class com.SPWipet.** { *; }
-keep class com.stream.** { *; }
-keep class com.video.** { *; }
-keep class com.yuv.** { *; }
-keep class com.tool.websocket.** { *; }
-keep class com.context.UmeyeApplication { *; }
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-dontwarn org.apache.commons.**
-dontwarn com.jcraft.jsch.**
