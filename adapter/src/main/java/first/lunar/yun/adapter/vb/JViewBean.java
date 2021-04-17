package first.lunar.yun.adapter.vb;

import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

  protected boolean isTypeOf(Object item){
    return item instanceof JViewBean;
  }

  @Override
  public int compare(IRecvDataDiff newData) {
    return 0;
  }

  @Override
  public boolean areItemsTheSame(IRecvDataDiff newData) {
    return newData.getClass().isAssignableFrom(getClass());
  }

  @Override
  public boolean areContentsTheSame(IRecvDataDiff newData) {
    return false;
  }

  @Override
  public Object getChangePayload(IRecvDataDiff oldData) {
    return this;
  }

  /**
   * Return the stable ID for the item at <code>position</code>. If {@link RecyclerView.Adapter#hasStableIds()()}
   * would return false this method should return {@link RecyclerView.Adapter#NO_ID}. The default implementation
   * of this method returns {@link RecyclerView.Adapter#NO_ID}.
   *
   * @param position Adapter position to query
   * @return the stable ID of the item at position
   */
  @Override
  public long getItemId(int position) {
    return RecyclerView.NO_ID;
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

  public int getSpanSize(int position) {
    return 1;
  }
}
