package first.lunar.yun.adapter.face;

import android.support.annotation.Nullable;


import first.lunar.yun.adapter.holder.JViewHolder;
import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/7/5.
 */
public interface IRecvData {
    /**
     *
     * @param holder
     * @param viewClickListener
     * @param payloads
     */
    void onBindViewHolder(JViewHolder holder, OnViewClickListener viewClickListener, @Nullable List<Object> payloads);
}
