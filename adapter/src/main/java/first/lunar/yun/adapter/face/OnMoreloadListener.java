package first.lunar.yun.adapter.face;

import androidx.annotation.Keep;

/**
 * @another 江祖赟
 * @date 2017/8/16 0016.
 */
@Keep
public interface OnMoreloadListener {
    /**
     * 发起请求 加载更多数据/重试
     */
    void onUpLoadingMore();
    void retryUp2LoadingMore();
}
