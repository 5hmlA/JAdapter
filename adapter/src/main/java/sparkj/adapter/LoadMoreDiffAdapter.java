package sparkj.adapter;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.RecyclerView;
import sparkj.adapter.face.OnViewClickListener;
import sparkj.adapter.holder.JViewHolder;
import sparkj.adapter.vb.JViewBean;

import java.util.List;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持] 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public class LoadMoreDiffAdapter<T extends JViewBean> extends AbsLoadMoreWrapperAdapter<T> {

  JVBrecvDiffAdapter<T> mInnerAdapter;

  @Keep
  public LoadMoreDiffAdapter(OnViewClickListener<T> viewClickListener) {
    mInnerAdapter = new JVBrecvDiffAdapter<>(viewClickListener);
    mInnerAdapter.setUpdateCallback(this);
  }

  @Override
  public T getItemData(int position) {
    T itemData = super.getItemData(position);
    if (itemData == null) {
      return mInnerAdapter.getItemData(position);
    }
    return itemData;
  }

  @Override
  protected RecyclerView.Adapter<JViewHolder> getInnerAdapter() {
    return mInnerAdapter;
  }

  @Override
  public void onRefreshData(List<T> data) {
    mInnerAdapter.submitList(data);
  }

  @Override
  protected void onLoadMoreSucceed(List<T> moreData) {
    mInnerAdapter.addMoreList(moreData);
  }

}
