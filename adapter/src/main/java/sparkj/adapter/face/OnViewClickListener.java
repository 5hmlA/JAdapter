package sparkj.adapter.face;

import android.view.View;
import androidx.annotation.Keep;

/**
 * @another 江祖赟
 * @date 2017/6/21.
 */
@Keep
public interface OnViewClickListener<T> {
    void onItemClicked(View view, T itemData);
}
