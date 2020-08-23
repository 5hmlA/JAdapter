package first.lunar.yun.adapter.helper;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.Keep;
import first.lunar.yun.adapter.LApp;
import java.util.Collection;
import java.util.Objects;

@Keep
public class CheckHelper {

    public static final int EQUALTAG = 0x7f199101;
    private static final String DEFAULTSTR = "";

    /**
     * 检查对象是否相同/都不为空
     *
     * @return true 安全的对象 都不为空
     */
    public static boolean isEqual(Object object1, Object object2){
        return Objects.equals(object1, object2);
    }

    /**
     * @param strs
     * @return true 所有字符串都有效
     */
    public static boolean checkStrings(CharSequence... strs){
        for(CharSequence str : strs) {
            if(TextUtils.isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param strs
     * @return true 所有字符串都有效
     */
    public static boolean checkObjectStr(Object... strs){
        if(strs != null) {
            for(Object str : strs) {
                if(TextUtils.isEmpty(str.toString())) {
                    return false;
                }
            }
        }else {
            return false;
        }
        return true;
    }

    /**
     * 检查对象是否为空
     *
     * @return true 安全的对象 都不为空
     */
    public static boolean checkObjects(Object... objects){
        for(Object object : objects) {
            if(object == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 注意传过来的对象 是不相关的not like{p,p.friend}
     *
     * @param arrays
     * @param <T>
     * @return
     */
    public static <T> boolean checkArrays(T[]... arrays){
        if(arrays != null) {
            for(Object[] array : arrays) {
                if(array == null || array.length == 0) {
                    return false;
                }
            }
        }else {
            return false;
        }
        return true;
    }

    public static <T> T checkNotNull(T o){
        return checkNotNull(o, "CheckHelper");
    }

    public static <T> T checkNotNull(T o, String warm){
        if(o == null) {
            throw new NullPointerException(warm);
        }
        return o;
    }

    /**
     * 校验对象是否为空 为空抛异常
     */
    public static void verifyObjects(Object... objects){
        for(Object object : objects) {
            if(object == null) {
                throw new NullPointerException("CheckHelper");
            }
        }
    }


    public static Object safeObject(Object o){
        if(o == null) {
            return "";
        }else {
            return o;
        }
    }


    /**
     * 检查List集合是否 有效
     *
     * @return true 安全的Collection 都不为空
     */
    public static boolean checkLists(Collection... lists){
        for(Collection list : lists) {
            if(list == null || list.size()<=0) {
                return false;
            }
        }
        return true;
    }


    /**
     * 返回 安全的字符串
     *
     * @return 为空则返回“”
     */
    public static String safeString(Object str){
        return Objects.toString(str, DEFAULTSTR);
    }

    public static boolean viewTagBoolean(View view, int viewTag){
        if(view.getTag(viewTag) != null && view.getTag(viewTag) instanceof Boolean) {
            return (boolean)view.getTag(viewTag);
        }else {
            return false;
        }
    }

    /**
     * 到底有没有某项权限，怎么检测呢，基于以往 Android 在这方面的不精细，
     * 很多人都不会太在意这方面的逻辑判断，新出的6.0系统也只是基于targetSdkVersion 23以上的app的判断，
     * 包括6.0以下的版本，怎样判断是不是被安全中心这种禁掉了呢，这就需要 AppOpsManager 这个类了
     * AppOpsManager.OPSTR_CAMERA
     * <li>
     * <p>AppOpsManager.MODE_ALLOWED 有权限</p>
     * <p>AppOpsManager.MODE_IGNORED "被禁止了</p>
     * <p>AppOpsManager.MODE_ERRORED 出错了</p>
     * </li>
     *
     * @param permission
     */
    public static int checkPermission(final Activity activity, String permission){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            AppOpsManager appOpsManager = (AppOpsManager) LApp.getContext().getSystemService(Context.APP_OPS_SERVICE);
            int checkResult = appOpsManager.checkOpNoThrow(permission, Binder.getCallingUid(), LApp.getContext().getPackageName());
            if(checkResult == AppOpsManager.MODE_ALLOWED) {
                LLog.llog("有权限");
            }else if(checkResult == AppOpsManager.MODE_IGNORED) {
                // TODO: 只需要依此方法判断退出就可以了，这时是没有权限的。
                LLog.llog("权限被禁止了");
                //                new AlertDialog.Builder(activity).setTitle("权限")
                //                        .setCancelable(true).setMessage("权限被禁止，需要进入应用设置页面开启权限")
                //                        .setPositiveButton("去修改", new DialogInterface.OnClickListener() {
                //                            @Override
                //                            public void onClick(DialogInterface dialog, int which){
                //                                    IntentHelper.toAppDetailPage(activity,LApp.getPackageName());
                //                            }
                //                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                //                            @Override
                //                            public void onClick(DialogInterface dialog, int which){
                //                                dialog.dismiss();
                //                            }
                //                        }).create().show();
            }else if(checkResult == AppOpsManager.MODE_ERRORED) {
                LLog.llog("出错了");
            }else if(checkResult == 4) {
                LLog.llog("权限需要询问");
            }
            return checkResult;
        }
        return AppOpsManager.MODE_ALLOWED;
    }

    /**
     * 判断是否包含
     * <p>( window.getDecorView().getSystemUiVisibility()&View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;</p>
     *
     * @param orin
     * @param flag
     * @return
     */
    public static boolean checkHasFlag(int orin, int flag){
        return ( orin&flag ) == flag;
    }
}

