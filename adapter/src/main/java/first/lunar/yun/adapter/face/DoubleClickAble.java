package first.lunar.yun.adapter.face;

import androidx.annotation.Keep;

/**
 * @another 江祖赟
 * @date 2017/10/20 0020.
 */
@Keep
public interface DoubleClickAble {
    void setOnDoubleClickListener(OnDoubleClickListener dl);

    interface OnDoubleClickListener {
        void onDoubleClicked(DoubleClickAble view);
    }
}
