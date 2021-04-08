package first.lunar.yun.adapter;

import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持] 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public class LoadMoreWrapperAdapter<T extends JViewBean> extends AbsLoadMoreWrapperAdapter<T> {

  JVBrecvDiffAdapter<T> mInnerAdapter;

  public LoadMoreWrapperAdapter(JVBrecvDiffAdapter<T> innerAdapter) {
    mInnerAdapter = innerAdapter;
  }

  @Override
  protected RecyclerView.Adapter<JViewHolder> getInnerAdapter() {
    return mInnerAdapter;
  }

  @Override
  public void loadMoreSucceed(List<T> moreData) {
    super.loadMoreSucceed(moreData);
    mInnerAdapter.addMoreList(moreData);
  }

  public void refreshData(List<T> data) {
    mInnerAdapter.submitList(data);
  }
}
