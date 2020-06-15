# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\DEVELOP\Android\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-keep class * extends first.lunar.yun.adapter.face.IRecvDataDiff {*;}
-keep class first.lunar.yun.adapter.holder.JViewHolder {*;}
-keep class first.lunar.yun.adapter.face.** {*;}
-keep class first.lunar.yun.adapter.diff.** {*;}
-keep class first.lunar.yun.adapter.vb.** {*;}
-keep class first.lunar.yun.adapter.decoration.** {*;}
-keep class first.lunar.yun.adapter.LApp { *;}
-keep class first.lunar.yun.adapter.LConsistent {*;}
-keepclassmembers class first.lunar.yun.adapter.JVBrecvAdapter {}
-keepclassmembers class first.lunar.yun.adapter.LoadMoreWrapperAdapter {}
-keepclassmembers class first.lunar.yun.adapter.LoadMoreWrapperDampAdapter {}
