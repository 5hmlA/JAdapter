package first.lunar.yun.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.face.LayoutManagers.FullSpan;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.LLog;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.loadmore.LoadMoreChecker;
import first.lunar.yun.adapter.loadmore.LoadMoreConfig;
import java.util.Collections;
import java.util.List;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持] 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public abstract class AbsLoadMoreWrapperAdapter<T> extends RecyclerView.Adapter<JViewHolder> implements OnViewClickListener, LoadMoreChecker.LoadMoreCallBack {

  LoadMoreChecker mLoadMoreChecker;
  public final static String TAG = AbsLoadMoreWrapperAdapter.class.getSimpleName();
  public JViewHolder mLoadMoreHolder;
  LoadMoreConfig mLoadMoreConfig = new LoadMoreConfig();
  private AbsLoadMoreWrapperAdapter.LoadMoreSpanSizeLookup mSpanSizeLookup;
  LoadMoreChecker.LoadMoreCallBack mLoadMoreCallBack;

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    mLoadMoreChecker = new LoadMoreChecker();
    mLoadMoreChecker.attach(recyclerView, this);
    super.onAttachedToRecyclerView(recyclerView);
    setSpanCount(recyclerView);
  }

  @Override
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
  public void onViewDetachedFromWindow(JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    if (getInnerAdapter() != null) {
      getInnerAdapter().onViewDetachedFromWindow(holder);
    }
  }

  @Override
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
    if (mLoadMoreChecker.canLoadMore()) {
      return getInnerAdapter().getItemCount() + 1;
    }
    return getInnerAdapter().getItemCount();
  }

  @Override
  public final int getItemViewType(int position) {
    if (mLoadMoreChecker.canLoadMore()) {
      if (position < getInnerAdapter().getItemCount()) {
        return getInnerAdapter().getItemViewType(position);
      }
      return mLoadMoreConfig.getLoadMoreVb().bindLayout();
    } else {
      return getInnerAdapter().getItemViewType(position);
    }
  }

  protected abstract RecyclerView.Adapter<JViewHolder> getInnerAdapter();

  @Override
  public JViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layout = mLoadMoreConfig.getLoadMoreVb().bindLayout();
    if (layout == viewType) {
      return new JViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }
    return getInnerAdapter().onCreateViewHolder(parent, viewType);
  }

  @Override
  public void onBindViewHolder(JViewHolder holder, final int position) {
    this.onBindViewHolder(holder, position, Collections.emptyList());
  }

  @Override
  public void onBindViewHolder(JViewHolder holder, final int position, List<Object> payloads) {
    if (position < getInnerAdapter().getItemCount()) {
      getInnerAdapter().onBindViewHolder(holder, position, payloads);
    } else {
      mLoadMoreConfig.getLoadMoreVb().onBindViewHolder((JViewHolder) holder, position, payloads,this);
    }
  }

  @Override
  public void onItemClicked(View view, Object itemData) {
    //点击重试
    mLoadMoreChecker.loadMoreRetry();
    showLoading();
  }

  /**
   * 外部手动调用 加载错误
   */
  @Keep
  public void loadError(CharSequence tips) {
    LLog.llogi("loadError >>>");
    mLoadMoreChecker.loadMoreSucceed();
    LoadMoreConfig.HolderState loadError = LoadMoreConfig.HolderState.LOADERETRY;
    loadError.setTips(tips);
    notifyLoadMore(loadError);
  }

  @Keep
  public void loadMoreSucceed(List<T> moreData) {
    mLoadMoreChecker.loadMoreSucceed();
    showLoading();
  }

  private void showLoading() {
    LoadMoreConfig.HolderState loading = LoadMoreConfig.HolderState.LOADING;
    loading.setTips(mLoadMoreConfig.getLoadingTips());
    notifyLoadMore(loading);
  }

  @Keep
  public void loadMoreFinish(CharSequence finishTips) {
    mLoadMoreChecker.toggleLoadMore(false);
    LoadMoreConfig.HolderState disload = LoadMoreConfig.HolderState.LOADFINISH;
    disload.setTips(finishTips);
    notifyLoadMore(disload);
  }

  @Override
  public void onLoadMore(boolean retry) {
    if (mLoadMoreCallBack != null) {
      mLoadMoreCallBack.onLoadMore(retry);
    }
  }

  private void notifyLoadMore(LoadMoreConfig.HolderState loadState) {
    notifyItemChanged(getItemCount() -1, loadState);
  }


  @Override
  public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
    getInnerAdapter().registerAdapterDataObserver(observer);
  }

  @Override
  public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
    getInnerAdapter().unregisterAdapterDataObserver(observer);
  }

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

  public void setLoadMoreCallBack(LoadMoreChecker.LoadMoreCallBack loadMoreCallBack) {
    mLoadMoreCallBack = loadMoreCallBack;
  }
}
