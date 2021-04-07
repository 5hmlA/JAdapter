package first.lunar.yun.adapter.vb;

import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import first.lunar.yun.adapter.face.IRecvDataDiff;
import first.lunar.yun.adapter.holder.JViewHolder;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
@Keep
public abstract class JViewBean implements IRecvDataDiff {

  /**
   * after notifyRemove or notifyInsert will be useless
   */
  private int mPosition;

  public int getPosition() {
    return mPosition;
  }

  public void setPosition(int position) {
    mPosition = position;
  }

  @Override
  public int compare(IRecvDataDiff newData) {
    return 0;
  }

  @Override
  public boolean areItemsTheSame(IRecvDataDiff newData) {
    return false;
  }

  @Override
  public boolean areContentsTheSame(IRecvDataDiff newData) {
    return false;
  }

  @Override
  public Object getChangePayload(IRecvDataDiff oldData) {
    return new Bundle();
  }

  @LayoutRes
  public abstract int bindLayout();

  @Override
  public void onViewDetachedFromWindow(@NonNull JViewHolder holder) {

  }

  @Override
  public void onViewAttachedToWindow(@NonNull JViewHolder holder) {

  }

  @Override
  public void onViewRecycled(@NonNull JViewHolder holder) {

  }
}
