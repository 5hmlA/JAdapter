package sparkj.adapter;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import sparkj.adapter.face.OnViewClickListener;
import sparkj.adapter.helper.JVBSortList;
import sparkj.adapter.holder.JViewHolder;
import sparkj.adapter.vb.JViewBean;

import java.util.List;

/**
 * @author yun.
 * @date 2021/4/10 0010
 * @des [SortList适合单个增删]
 * @since [https://github.com/5hmlA]
 * <p><a href="https://github.com/5hmlA">github</a>
 */
@Deprecated
public class LoadMoreSortListAdapter extends AbsLoadMoreWrapperAdapter<JViewBean> {

  private JVBSortListAdapter mJvbSortListAdapter;

  @Keep
  public LoadMoreSortListAdapter(OnViewClickListener<JViewBean> viewClickListener) {
    mJvbSortListAdapter = new JVBSortListAdapter(viewClickListener);
    mJvbSortListAdapter.setSortList(new JVBSortList(mJvbSortListAdapter));
    mJvbSortListAdapter.setUpdateCallback(this);
  }

  @Override
  public JViewBean getItemData(int position) {
    JViewBean itemData = super.getItemData(position);
    if (itemData == null) {
      return mJvbSortListAdapter.getItemData(position);
    }
    return itemData;
  }

  @Override
  protected RecyclerView.Adapter<JViewHolder> getInnerAdapter() {
    return mJvbSortListAdapter;
  }

  @Override
  public void onRemoved(int position, int count) {
    super.onRemoved(position, count);
  }

  @Override
  protected void onRefreshData(List<JViewBean> data) {
    mJvbSortListAdapter.getSortList().replaceAll(data);
  }

  @Override
  protected void onLoadMoreSucceed(List<JViewBean> moreData) {
    mJvbSortListAdapter.addData(moreData);
  }

  @Keep
  public SortedList<JViewBean> getSortList(){
    return mJvbSortListAdapter.getSortList();
  }

}
