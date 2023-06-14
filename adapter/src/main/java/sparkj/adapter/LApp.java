package sparkj.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Keep;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/5hmlA]
 * <p><a href="https://github.com/5hmlA">github</a>
 */
public class LApp {
  private static Context sContext;
  private static final Handler sHandle;
  private static boolean sIsDebug = false;

  static {
    sHandle = new Handler(Looper.getMainLooper()) {
      Toast mToast;

      public void handleMessage(Message paramMessage) {
        super.handleMessage(paramMessage);
        CharSequence localCharSequence = (CharSequence) paramMessage.obj;
        if (this.mToast == null)
          this.mToast = Toast.makeText(LApp.sContext, "来一个toast", 0);
        this.mToast.setText(localCharSequence);
        if (!this.mToast.getView().isShown())
          this.mToast.show();
      }
    };
  }

  @Keep
  private LApp() {
  }

  @Keep
  public static int findColor(int paramInt) {
    return sContext.getResources().getColor(paramInt);
  }

  @Keep
  public static float findDimens(int paramInt) {
    return sContext.getResources().getDimension(paramInt);
  }

  @Keep
  public static String findString(int paramInt) {
    return sContext.getString(paramInt);
  }

  @Keep
  public static String findString(int paramInt, Object[] paramArrayOfObject) {
    return sContext.getString(paramInt, paramArrayOfObject);
  }

  @Keep
  public static final void fly(Context paramContext) {
    sContext = paramContext.getApplicationContext();
  }

  @Keep
  public static Activity getAct4View(View paramView) {
    for (Context localContext = paramView.getContext(); (localContext instanceof ContextWrapper); localContext = ((ContextWrapper) localContext).getBaseContext())
      if ((localContext instanceof Activity))
        return (Activity) localContext;
    return null;
  }

  @Keep
  public static Context getContext() {
    return sContext;
  }

  @Keep
  public static boolean isDebug() {
    return sIsDebug;
  }

  @Keep
  public static void setDebug(boolean isDebug) {
    sIsDebug = isDebug;
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public static int dp2px(float dpValue) {
    return (int)(0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
  }
}
