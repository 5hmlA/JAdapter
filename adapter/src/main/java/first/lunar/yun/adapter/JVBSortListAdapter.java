package first.lunar.yun.adapter;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.JVBSortList;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JVBSortListAdapter extends JVBrecvAdapter<JViewBean> implements ListUpdateCallback {

  JVBSortList mSortList = new JVBSortList(this);
  ListUpdateCallback mUpdateCallback = new ListUpdateCallback() {
    @Override
    public void onInserted(int position, int count) {
      notifyItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
      notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
      notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, @Nullable Object payload) {
      notifyItemRangeChanged(position, count, payload);
    }
  };

  @Keep
  public JVBSortListAdapter() {
  }

  @Keep
  public JVBSortListAdapter(OnViewClickListener<JViewBean> onViewClickListener) {
    super(onViewClickListener);
  }

  public JVBSortList getSortList() {
    return mSortList;
  }

  public void setSortList(JVBSortList sortList) {
    mSortList = sortList;
  }

  public void setUpdateCallback(ListUpdateCallback updateCallback) {
    mUpdateCallback = updateCallback;
  }

  @Override
  public void onInserted(int position, int count) {
    mUpdateCallback.onInserted(position, count);
  }

  @Override
  public void onRemoved(int position, int count) {
    mUpdateCallback.onRemoved(position, count);
  }

  @Override
  public void onMoved(int fromPosition, int toPosition) {
    mUpdateCallback.onMoved(fromPosition, toPosition);
  }

  @Override
  public void onChanged(int position, int count, @Nullable Object payload) {
    mUpdateCallback.onChanged(position, count, payload);
  }

  @Override
  public int getItemCount() {
    return mSortList.size();
  }

  @Override
  public int getItemViewType(int position) {
    return mSortList.get(position).bindLayout();
  }

  @Override
  protected JViewBean getItemData(int position) {
    return mSortList.get(position);
  }

  @Override
  public void remove(int position) {
    mSortList.removeItemAt(position);
  }

  @Override
  public void addData(List<JViewBean> datas) {
    mSortList.addAll(datas);
  }

  @Override
  public void addData(int position, List<JViewBean> datas) {
    if (position < mSortList.size()) {
      throw new RuntimeException("try to use refreshData");
    }
    mSortList.addAll(datas);
  }
}
  

