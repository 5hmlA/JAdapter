package sparkj.adapter.face;

import android.view.View;
import androidx.annotation.Keep;

/**
 * @author yun.
 * @date 2019/6/2 0002
 * @des [一句话描述]
 * @since [https://github.com/5hmlA]
 * <p><a href="https://github.com/5hmlA">github</a>
 */
@Keep
public abstract class JOnClickListener implements View.OnClickListener {

  private static final int SHAKE_TIME = 500;
  private static final int TAG_CLICK = 0x7654321d;
  private int mShakeTime = SHAKE_TIME;

  public JOnClickListener(int shakeTime) {
    mShakeTime = shakeTime;
  }

  public JOnClickListener() {
  }

  @Override
  public void onClick(View v) {
    Object tag = v.getTag(TAG_CLICK);
    if (tag != null && (System.currentTimeMillis() - ((Long) tag) < mShakeTime)) {
      return;
    }
    v.setTag(TAG_CLICK, System.currentTimeMillis());
    doClick(v);
  }

  protected abstract void doClick(View v);
}
