package first.lunar.yun.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.face.LoadMoreCallBack;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.LLog;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.loadmore.LoadMoreChecker;
import first.lunar.yun.adapter.loadmore.LoadMoreConfig;
import first.lunar.yun.adapter.vb.FullSpan;
import java.util.Collections;
import java.util.List;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持] 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public abstract class AbsLoadMoreWrapperAdapter<T> extends RecyclerView.Adapter<JViewHolder> implements
    OnViewClickListener, LoadMoreCallBack, ListUpdateCallback {

  public final static String TAG = AbsLoadMoreWrapperAdapter.class.getSimpleName();
  LoadMoreConfig mLoadMoreConfig = new LoadMoreConfig.Builder().build();
  LoadMoreChecker mLoadMoreChecker;
  private AbsLoadMoreWrapperAdapter.LoadMoreSpanSizeLookup mSpanSizeLookup;
  LoadMoreCallBack mLoadMoreCallBack;

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    getInnerAdapter().onAttachedToRecyclerView(recyclerView);
    mLoadMoreChecker = new LoadMoreChecker();
    mLoadMoreChecker.toggleLoadMore(mLoadMoreConfig.isEnableLoadMore());
    mLoadMoreChecker.attach(recyclerView, this);
    setSpanCount(recyclerView);
  }

  @Override
  @Keep
  public void onViewAttachedToWindow(JViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    if (getInnerAdapter() != null) {
      getInnerAdapter().onViewAttachedToWindow(holder);
    }
  }

  @Override
  public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    if (getInnerAdapter() != null) {
      getInnerAdapter().onDetachedFromRecyclerView(recyclerView);
    }
  }

  @Override
  @Keep
  public void onViewDetachedFromWindow(JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    if (getInnerAdapter() != null) {
      getInnerAdapter().onViewDetachedFromWindow(holder);
    }
  }

  @Override
  @Keep
  public void onViewRecycled(@NonNull JViewHolder holder) {
    super.onViewRecycled(holder);
    LLog.llog(" ****** onViewRecycled : " + holder.itemView);
    if (getInnerAdapter() != null) {
      getInnerAdapter().onViewRecycled(holder);
    }
  }

  private void setSpanCount(RecyclerView recv) {
    final RecyclerView.LayoutManager layoutManager = recv.getLayoutManager();
    if (layoutManager != null) {
      if (layoutManager instanceof GridLayoutManager && getInnerAdapter() instanceof JVBrecvAdapter) {
        if (mSpanSizeLookup == null) {
          mSpanSizeLookup = new LoadMoreSpanSizeLookup((GridLayoutManager) layoutManager, ((JVBrecvAdapter) getInnerAdapter()).getDataList());
        }
        ((GridLayoutManager) layoutManager).setSpanSizeLookup(mSpanSizeLookup);
      }
    } else {
      Log.e(TAG, "LayoutManager 为空,请先设置 recycleView.setLayoutManager(...)");
    }
  }

  @Override
  public int getItemCount() {
    int itemCount = getInnerAdapter().getItemCount();
    if (itemCount == 0) {
      return 0;
    }
    if (mLoadMoreChecker.enableLoadMore()) {
      return itemCount + 1;
    }
    return itemCount;
  }

  @Override
  public final int getItemViewType(int position) {
    if (mLoadMoreChecker.enableLoadMore()) {
      if (position < getInnerAdapter().getItemCount()) {
        return getInnerAdapter().getItemViewType(position);
      }
      return mLoadMoreConfig.getLoadMoreVb().bindLayout();
    } else {
      return getInnerAdapter().getItemViewType(position);
    }
  }

  @Override
  @Keep
  public JViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (mLoadMoreChecker.enableLoadMore()) {
      int layout = mLoadMoreConfig.getLoadMoreVb().bindLayout();
      if (layout == viewType) {
        return new JViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
      }
    }
    return getInnerAdapter().onCreateViewHolder(parent, viewType);
  }

  @Override
  @Keep
  public void onBindViewHolder(JViewHolder holder, final int position) {
    this.onBindViewHolder(holder, position, Collections.emptyList());
  }

  @Override
  @Keep
  public void onBindViewHolder(JViewHolder holder, final int position, List<Object> payloads) {
    if (position < getInnerAdapter().getItemCount()) {
      getInnerAdapter().onBindViewHolder(holder, position, payloads);
    } else {
      mLoadMoreConfig.getLoadMoreVb().onBindViewHolder((JViewHolder) holder, position, payloads, this);
    }
  }

  @Override
  public void onItemClicked(View view, Object itemData) {
    //点击重试 就是开始加载
    mLoadMoreChecker.loadingMore();
    showLoading();
  }

  @Keep
  public final void refreshData(List<T> data) {
    //刷新数据之后 需要允许上拉加载检测
    if (mLoadMoreConfig.getStyle() == LoadMoreConfig.Style.GONE) {
      mLoadMoreChecker.toggleLoadMore(true);
      notifyItemInserted(getInnerAdapter().getItemCount());
    } else {
      mLoadMoreChecker.loadMoreCheck();
    }
    showLoading();
    onRefreshData(data);
  }

  /**
   * 外部手动调用 加载错误
   */
  @Keep
  public void loadMoreError(CharSequence tips) {
    LLog.llogi("loadError >>>");
    //加载失败之后 需要允许上拉加载检测
    mLoadMoreChecker.loadMoreCheck();
    LoadMoreConfig.HolderState loadError = LoadMoreConfig.HolderState.LOADERETRY;
    loadError.setTips(tips);
    notifyLoadMore(loadError);
  }

  @Keep
  public void loadMoreSucceed(List<T> moreData) {
    //更多数据加载成功之后 需要允许上拉加载检测
    mLoadMoreChecker.loadMoreCheck();
    showLoading();
  }

  @Keep
  public void noMoreLoad(CharSequence finishTips) {
    if (mLoadMoreConfig.getStyle() == LoadMoreConfig.Style.GONE) {
        mLoadMoreChecker.toggleLoadMore(false);
        notifyItemRemoved(getItemCount());
    } else {
        mLoadMoreChecker.noMoreLoad();
        LoadMoreConfig.HolderState disload = LoadMoreConfig.HolderState.LOADNOMORE;
        disload.setTips(finishTips);
        notifyLoadMore(disload);
    }
  }

  private void showLoading() {
    LoadMoreConfig.HolderState loading = LoadMoreConfig.HolderState.LOADING;
    loading.setTips(mLoadMoreConfig.getLoadingTips());
    notifyLoadMore(loading);
  }

  private void notifyLoadMore(LoadMoreConfig.HolderState loadState) {
    mLoadMoreConfig.getLoadMoreVb().setLoadState(loadState);
    notifyItemChanged(getInnerAdapter().getItemCount(), loadState);
  }

  @Override
  public void onLoadMore(boolean retry) {
    if (mLoadMoreCallBack != null) {
      mLoadMoreCallBack.onLoadMore(retry);
    }
  }

  @Keep
  public void setLoadMoreConfig(LoadMoreConfig loadMoreConfig) {
    mLoadMoreConfig = loadMoreConfig;
  }

  @Override
  public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
    super.registerAdapterDataObserver(observer);
    getInnerAdapter().registerAdapterDataObserver(observer);
  }

  @Override
  public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
    getInnerAdapter().unregisterAdapterDataObserver(observer);
    super.unregisterAdapterDataObserver(observer);
  }


  private boolean isRemoveAll(int itemCount) {
    return mLoadMoreChecker.isRemoveAll(itemCount);
  }


  @Override
  public void onInserted(int position, int count) {
    notifyItemRangeInserted(position, count);
  }

  @Override
  public void onRemoved(int position, int count) {
    if (isRemoveAll(count)) {
      count += 1;//移除全部要把loading也移除否则insert的时候会滚动到loading
    }
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

  @Keep
  protected abstract RecyclerView.Adapter<JViewHolder> getInnerAdapter();

  @Keep
  protected abstract void onRefreshData(List<T> data);

  @Keep
  public static class LoadMoreSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    GridLayoutManager mGridLayoutManager;
    List<? extends Object> mItems;

    public LoadMoreSpanSizeLookup(GridLayoutManager gridLayoutManager, List<? extends Object> items) {
      mGridLayoutManager = gridLayoutManager;
      mItems = items;
    }

    @Override
    public int getSpanSize(int position) {
      if (position == mItems.size()) {
        return mGridLayoutManager.getSpanCount();
      }
      return mItems.get(position) instanceof FullSpan ? mGridLayoutManager.getSpanCount() : 1;
    }
  }

  @Keep
  public void setLoadMoreCallBack(LoadMoreCallBack loadMoreCallBack) {
    mLoadMoreCallBack = loadMoreCallBack;
  }
}
