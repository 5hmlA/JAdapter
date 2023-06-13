package sparkj.adapter.face;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import sparkj.adapter.holder.JViewHolder;

import java.util.List;

/**
 * @another 江祖赟
 * @date 2017/7/5.
 */
@Keep
public interface IRecvData {

  void onViewDetachedFromWindow(@NonNull JViewHolder holder);

  void onViewAttachedToWindow(@NonNull JViewHolder holder);

  void onBindViewHolder(JViewHolder holder, int position, @Nullable List<Object> payloads, @Nullable OnViewClickListener viewClickListener);
}
