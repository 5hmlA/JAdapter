package first.lunar.yun.adapter.vb;

import android.app.Activity;
import android.view.View;
import first.lunar.yun.adapter.LApp;
import first.lunar.yun.adapter.face.IRecvDataDiff;
import java.lang.ref.WeakReference;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public abstract class JViewBean implements IRecvDataDiff {

  private WeakReference<Activity> mActivityWeakReference;

  public void bindActivity(Activity activity) {
    mActivityWeakReference = new WeakReference<>(activity);
  }

  public Activity getActivity(View view) {
    if (mActivityWeakReference != null) {
      Activity activity = mActivityWeakReference.get();
      if (activity != null) {
        return activity;
      } else {
        return (mActivityWeakReference = new WeakReference<>(LApp.getAct4View(view))).get();
      }
    } else {
      return (mActivityWeakReference = new WeakReference<>(LApp.getAct4View(view))).get();
    }
  }

  @Override
  public boolean areItemsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData) {
    return false;
  }

  @Override
  public boolean areContentsTheSame(IRecvDataDiff oldData, IRecvDataDiff newData) {
    return false;
  }

  @Override
  public Object getChangePayload(IRecvDataDiff oldData, IRecvDataDiff newData) {
    return null;
  }
}
