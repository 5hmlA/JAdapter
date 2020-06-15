package first.lunar.yun.adapter.face;

import androidx.annotation.Keep;

/**
 * @another 江祖赟
 * @date 2017/6/21.
 */
@Keep
public interface OnItemClickListener<T> {
    void onItemClicked(T itemData, int position);
}
