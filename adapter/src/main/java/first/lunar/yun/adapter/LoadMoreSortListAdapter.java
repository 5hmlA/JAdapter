package first.lunar.yun.adapter;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.loadmore.JLoadMoreVBSortList;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;

/**
 * @author yun.
 * @date 2021/4/10 0010
 * @des [SortList适合单个增删]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
@Deprecated
public class LoadMoreSortListAdapter extends AbsLoadMoreWrapperAdapter<JViewBean> {

  private JVBSortListAdapter mJvbSortListAdapter;

  @Keep
  public LoadMoreSortListAdapter(OnViewClickListener<JViewBean> viewClickListener) {
    mJvbSortListAdapter = new JVBSortListAdapter(viewClickListener);
    mJvbSortListAdapter.setSortList(new JLoadMoreVBSortList(mJvbSortListAdapter));
    mJvbSortListAdapter.setUpdateCallback(this);
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
