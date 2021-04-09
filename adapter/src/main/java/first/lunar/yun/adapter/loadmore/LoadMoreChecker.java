package first.lunar.yun.adapter.loadmore;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.helper.LLog;

/**
 * @author yun.
 * @date 2021/4/7 0007
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class LoadMoreChecker {
  public RecyclerView mRecyclerView;
  public int mLastDataSize;
  LoadState mLoadState = LoadState.LOADCHECK;
  LoadMoreCallBack mLoadMoreCallBack;
  private RecyclerView.Adapter mAdapter;

  public enum LoadState {
    LOADDISABLE("关闭加载"), LOADNOMORE("加载结束,不再检查加载"), LOADING("加载中"), LOADCHECK("要检查加载更多");
    private String desc;
    private String tips;

    LoadState(String desc) {
      this.desc = desc;
    }

    public String getTips() {
      return tips;
    }

    public void setTips(String tips) {
      this.tips = tips;
    }
  }

  private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      checkUp2loadMore(newState);
    }

    //            @Override
    //            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
    //                super.onScrolled(recyclerView, dx, dy);
    //                if(mLoadmoreitem == NEED_UP2LOAD_MORE) {
    //                    //向上无法滚动
    //                    if(dy>0 && !mRecyclerView.canScrollVertically(1) && mLoadmoreitem == NEED_UP2LOAD_MORE && !mInLoadingMore) {
    //                        mInLoadingMore = true;
    //                        if(mListener != null) {
    //                            mListener.onup2LoadingMore();
    //                        }
    //                    }
    //                }
    //            }
  };

  private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      super.onItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
      super.onItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      super.onItemRangeInserted(positionStart, itemCount);
      updateDataSize();
      LLog.llogi("load_more onItemRangeInserted mLastCheckDataSize " + mLastDataSize);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      super.onItemRangeMoved(fromPosition, toPosition, itemCount);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      LLog.llogi("load_more onItemRangeRemoved mLastCheckDataSize "
          + mLastDataSize + " reomved count:" + itemCount);
      if (isRemoveAll(itemCount)) {
        return;
      }
      checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
      updateDataSize();
    }

    @Override
    public void onChanged() {
      LLog.llogi("onChanged " + mLastDataSize);
      //数据数量 变化了才需要判断
      if (shouldCheckLoadMore()) {
        LLog.llogi("load_more 数据发生变化同时数据数量发生变化 检测是否需要触发上拉加载");
        updateDataSize();
        checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
      }
    }
  };

  private void updateDataSize() {
    mLastDataSize = mAdapter.getItemCount() - (enableLoadMore() ? 1 : 0);
  }


  /**
   * <p>只在停止滚动的状态检测</p>
   * 检查 是否loadingholder可见，可见则回掉监听的onup2LoadingMore 去加载下一页数据
   */
  private void checkUp2loadMore(int newState) {
    LLog.llog("checkUp2loadMore >>> " + mLoadState);
    if (mLoadMoreCallBack == null) {
      return;
    }
    if (newState != RecyclerView.SCROLL_STATE_IDLE) {
      return;
    }
    if (!shouldCheckLoadMore()) {
      return;
    }
    if (mAdapter.getItemCount() <= 0) {
      return;
    }
    RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
    int lastPosition = 0;
    if (layoutManager instanceof GridLayoutManager) {
      //通过LayoutManager找到当前显示的最后的item的position
      lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
    } else if (layoutManager instanceof LinearLayoutManager) {
      lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
    }
    //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
    //如果相等则说明已经滑动到最后了
    LLog.llogi("find lastPosition " + lastPosition);
    if (lastPosition >= mAdapter.getItemCount() - 1) {//上拉检查一定有loading
      LLog.llogi("loading 上拉提示 item 可见", mLoadMoreCallBack.toString());
      mLoadState = LoadState.LOADING;
      mLoadMoreCallBack.onLoadMore(false);
    }
  }

  /**
   * 加载中的时候 不再需要检测上拉加载 加载成功之后 滚动/数据量变化的时候需要再次检测上拉加载
   *
   * @return
   */
  private boolean shouldCheckLoadMore() {
    //加载中不需要检测, 没有更多数据的时候不需要上拉检测
    return mLoadState.compareTo(LoadState.LOADING) > 0;
  }

  /**
   * 是否关闭了上拉加载检测  和 没有更多数据不检测不一样
   *
   * @return
   */
  public boolean enableLoadMore() {
    return mLoadState.compareTo(LoadState.LOADDISABLE) > 0;
  }

  public void attach(RecyclerView recyclerView, LoadMoreCallBack loadMoreCallBack) {
    mLoadMoreCallBack = loadMoreCallBack;
    mRecyclerView = recyclerView;
    mAdapter = recyclerView.getAdapter();
    assert mAdapter != null;
    mLoadState = LoadState.LOADCHECK;
    mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    mRecyclerView.removeOnScrollListener(mOnScrollListener);
    mRecyclerView.addOnScrollListener(mOnScrollListener);
  }

  /**
   * 没有更多数据了, 不再需要上拉加载
   */
  public void noMoreLoad() {
    if (mLoadState.compareTo(LoadState.LOADNOMORE) <= 0) {
      return;
    }
    mLoadState = LoadState.LOADNOMORE;
    mRecyclerView.removeOnScrollListener(mOnScrollListener);
  }

  /**
   * 设置需要上拉加载检测了
   */
  public void loadMoreCheck() {
    if (mLoadState.compareTo(LoadState.LOADNOMORE) <= 0) {
      mRecyclerView.addOnScrollListener(mOnScrollListener);
    }
    mLoadState = LoadState.LOADCHECK;
  }

  public void toggleLoadMore(boolean enable) {
    if (enable && !enableLoadMore()) {
      mLoadState = LoadState.LOADCHECK;
      mRecyclerView.addOnScrollListener(mOnScrollListener);
    } else if (!enable && enableLoadMore()) {
      mLoadState = LoadState.LOADDISABLE;
      mRecyclerView.removeOnScrollListener(mOnScrollListener);
    }
  }

  /**
   * 设置正在加载中 不需要上拉检测了
   */
  public void loadingMore() {
    mLoadState = LoadState.LOADING;
    mLoadMoreCallBack.onLoadMore(true);
  }

  public boolean isRemoveAll(int itemCount) {
    return mLastDataSize == itemCount;
  }

  public static interface LoadMoreCallBack {
    void onLoadMore(boolean retry);
  }
}
