package first.lunar.yun.adapter.face;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import first.lunar.yun.adapter.holder.JViewHolder;
import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/7/5.
 */
public interface IRecvData {
  void onViewDetachedFromWindow(@NonNull JViewHolder holder);
  void onViewAttachedToWindow(@NonNull JViewHolder holder);
  /**
   *
   */
  void onBindViewHolder(JViewHolder holder, int position, @Nullable List<Object> payloads, OnViewClickListener viewClickListener);
}
