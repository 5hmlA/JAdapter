package first.lunar.yun.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持] 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public class LoadMoreWrapperAdapter<T extends JViewBean> extends AbsLoadMoreWrapperAdapter<T> {

  JVBrecvAdapter<T> mInnerAdapter;

  public LoadMoreWrapperAdapter(JVBrecvAdapter<T> innerAdapter) {
    mInnerAdapter = innerAdapter;
  }

  @Override
  protected int getRowDataSize() {
    return mInnerAdapter.getDataSize();
  }

  @Override
  protected List<T> getRowData() {
    return mInnerAdapter.getDataList();
  }

  @Override
  protected RecyclerView.Adapter getInnerAdapter() {
    return mInnerAdapter;
  }

  @Override
  public void addMoreList(@NonNull List<T> data) {
    mInnerAdapter.addMoreList(data);
    notifyBottomItem();
  }

  @Override
  public void refreshAllData(@NonNull List<T> data) {
    mInnerAdapter.diffAll(data);
    notifyBottomItem();
  }

  @Override
  public void removeItem(int position) {
    mInnerAdapter.removeItem(position);
    notifyBottomItem();
  }

  @Override
  public void removeItem(T item) {
    mInnerAdapter.removeItem(item);
    notifyBottomItem();
  }

  @Override
  public void addItem(T data, int position) {
    mInnerAdapter.addItem(data, position);
    notifyBottomItem();
  }

  @Override
  public void diffAll(List<T> newData) {
    mInnerAdapter.diffAll(newData);
  }

  @Override
  public void diffAll(List<T> newData, boolean detectMoves) {
    mInnerAdapter.diffAll(newData, detectMoves);
    notifyBottomItem();
  }

  @Override
  public int getDataSize() {
    return mInnerAdapter.getDataSize();
  }
}
