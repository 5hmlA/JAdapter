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
  public int mLastCheckDataSize;
  LoadState mLoadState = LoadState.LOADSUCCED;
  LoadMoreCallBack mLoadMoreCallBack;
  private RecyclerView.Adapter mAdapter;

  public enum LoadState {
    DISLOAD("关闭加载"), LOADFINISH("加载完成"), LOADING("加载中"), LOADSUCCED("加载成功");
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
    boolean mIsRemoveAll = false;
    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      super.onItemRangeChanged(positionStart, itemCount);
      mLastCheckDataSize = mAdapter.getItemCount();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
      super.onItemRangeChanged(positionStart, itemCount, payload);
      mLastCheckDataSize = mAdapter.getItemCount();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      super.onItemRangeInserted(positionStart, itemCount);
      mLastCheckDataSize = mAdapter.getItemCount();
      LLog.llogi("load_more onItemRangeInserted mLastCheckDataSize " + mLastCheckDataSize);
      if (mIsRemoveAll) {
        mIsRemoveAll = false;
        mRecyclerView.scrollToPosition(0);
      }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      super.onItemRangeMoved(fromPosition, toPosition, itemCount);
      mIsRemoveAll = false;
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      final int orignSize = mLastCheckDataSize;
      mIsRemoveAll = mLastCheckDataSize == itemCount;
      checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
      mLastCheckDataSize = mAdapter.getItemCount();
      LLog.llogi("load_more onItemRangeRemoved mLastCheckDataSize "
          + mLastCheckDataSize + " reomved count:" + itemCount
          + " orignSize:" + orignSize + " mIsRemoveAll:" + mIsRemoveAll);
    }

    @Override
    public void onChanged() {
      mIsRemoveAll = false;
      LLog.llogi("onChanged " + mLastCheckDataSize);
      //数据数量 变化了才需要判断
      if (shouldCheckLoadMore() && mAdapter.getItemCount() != mLastCheckDataSize) {
        //                if(mLoadmoreitem == NEED_UP2LOAD_MORE && mLastCheckDataSize == 0 || mAdapter.getItemCount() != mLastCheckDataSize) {
        LLog.llogi("load_more 数据发生变化同时数据数量发生变化 检测是否需要触发上拉加载");
        mLastCheckDataSize = mAdapter.getItemCount();
        checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
      }
    }
  };


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
    if (mAdapter.getItemCount() <= 0) {
      return;
    }
    if (!shouldCheckLoadMore()) {
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
    if (lastPosition >= mAdapter.getItemCount() - 1) {
      LLog.llogi("loading 上拉提示 item 可见", mLoadMoreCallBack.toString());
      mLoadState = LoadState.LOADING;
      mLoadMoreCallBack.onLoadMore(false);
    }
  }


  private boolean shouldCheckLoadMore(){
    return mLoadState.compareTo(LoadState.LOADING) > 0;
  }

  public boolean canLoadMore(){
    return mLoadState.compareTo(LoadState.LOADFINISH) > 0;
  }

  public boolean enableLoadMore(){
    return mLoadState.compareTo(LoadState.DISLOAD) > 0;
  }

  public void attach(RecyclerView recyclerView, LoadMoreCallBack loadMoreCallBack) {
    mLoadMoreCallBack = loadMoreCallBack;
    mRecyclerView = recyclerView;
    mAdapter = recyclerView.getAdapter();
    assert mAdapter != null;
    mLoadState = LoadState.LOADSUCCED;
    mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    mRecyclerView.removeOnScrollListener(mOnScrollListener);
    mRecyclerView.addOnScrollListener(mOnScrollListener);
  }

  public void toggleLoadMore(boolean enable) {
    if (enable && !canLoadMore()) {
      mLoadState = LoadState.LOADSUCCED;
      mRecyclerView.addOnScrollListener(mOnScrollListener);
      mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    } else if (canLoadMore()) {
      mLoadState = LoadState.DISLOAD;
      mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
      mRecyclerView.removeOnScrollListener(mOnScrollListener);
    }
  }

  public void loadFinish() {
    if (canLoadMore()) {
      mLoadState = LoadState.LOADFINISH;
      mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
      mRecyclerView.removeOnScrollListener(mOnScrollListener);
    }
  }

  public void loadMoreSucceed() {
    mLoadState = LoadState.LOADSUCCED;
  }

  public void loadMoreRetry(){
    mLoadState = LoadState.LOADING;
    mLoadMoreCallBack.onLoadMore(true);
  }

  public static interface LoadMoreCallBack{
    void onLoadMore(boolean retry);
  }
}
