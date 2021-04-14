package first.lunar.yun.adapter.vb;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import java.util.List;

/**
 * @author yun.
 * @date 2020/10/28 0028
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class CViewVb extends JViewBean{

  private int mLayoutRes;

  @Keep
  public CViewVb(int layoutRes) {
    mLayoutRes = layoutRes;
  }

  @Override
  public int bindLayout() {
    return mLayoutRes;
  }

  @Override
  public void onBindViewHolder(JViewHolder holder, int position, @Nullable List<Object> payloads, @Nullable OnViewClickListener viewClickListener) {
  }
}
