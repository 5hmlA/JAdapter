package first.lunar.yun.adapter.face;

import androidx.annotation.Keep;

/**
 * @author yun.
 * @date 2021/4/10 0010
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
@Keep
public interface LoadMoreCallBack {
  void onLoadMore(boolean retry);
}
