package first.lunar.yun.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import first.lunar.yun.adapter.face.LayoutManagers.FullSpan;
import first.lunar.yun.adapter.face.OnMoreloadListener;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.LLog;
import first.lunar.yun.adapter.holder.BaseLoadMoreBinder;
import first.lunar.yun.adapter.holder.DefaultLoadMoreBinder;
import first.lunar.yun.adapter.holder.JRecvBaseBinder;
import first.lunar.yun.adapter.holder.JViewHolder;
import java.util.ArrayList;
import java.util.List;

import static first.lunar.yun.adapter.LConsistent.LoadMoreWrapper.NEED_UP2LOAD_MORE;
import static first.lunar.yun.adapter.LConsistent.LoadMoreWrapper.NON_UP2LOAD_MORE;
import static first.lunar.yun.adapter.helper.CheckHelper.checkLists;
import static first.lunar.yun.adapter.holder.BaseLoadMoreBinder.FOOT_STATE_LOAD_FINISH;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持] 分页列表 涉及到改变数据的比如回复删除 获取分页数据最好用索引 从哪个索引开始取多少条数据
 * 关于回复评论/回复回复，需要自己伪造新增的回复数据添加的被回复的评论中去 （涉及到分页不能重新刷洗数据）
 */
public class LoadMoreWrapperAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnViewClickListener {

  /**
   * 底部loadingholder永远都在
   * <p>{@link #mLoadmoreitem} 将无效</p>
   */
  public static final int STYLE_FIX_LOADING_HOLDER = 120;
  /**
   * 设置{@link #enAbleLoadMore(boolean)}为false之后 将会隐藏底部loadingholder
   */
  public static final int STYLE_LOADING_HOLDER_GONE = 130;
  public int mLoadMoreWrapperStyle = STYLE_FIX_LOADING_HOLDER;

  public static final int ITEMTYPE_LOADMORE = -13;
  public static final String FOOT_STATE_LOAD_LOADING = "loadingholder_up2load_loading";
  public static final String FOOT_STATE_LOAD_ERROR = "up2load_error";
  public static final String FOOT_STATE_LOAD_NOMORE = "up2load_nomore";
  /**
   * 当状态为需要上拉加载 但是数量少于PAGESIZE的时候 关闭上拉加载 情况一般不存在，告诉有下一页一定有，只有第一页数据不够显示完整一屏幕才会 数据只有0条但是 外部设置需要上拉加载 还是需要显示上啦加载ITEM
   */
  public int PAGESIZE = 0;
  public final static String TAG = LoadMoreWrapperAdapter.class.getSimpleName();
  public RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;
  public List<Object> mData = new ArrayList<>();
  public BaseLoadMoreBinder mLoadingBinder;
  public OnMoreloadListener mListener;

  public StaggeredGridLayoutManager mStaggeredGridLayoutManager;
  public RecyclerView mRecyclerView;
  public int mLastCheckDataSize;
  public JViewHolder mLoadMoreHolder;

  /**
   * <h1>状态</h1>
   * 是否正处于上拉加载 数据状态，{已调用 onUp2loadmore 还未拿到数据 true}
   */
  public boolean mInLoadingMore;

  /**
   * <h1>开关</h1>
   * 是否可以 上啦 抓取数据，和是否需要底部加载holder区分
   * <h1>默认关闭</h1>
   */
  public boolean mCanUp2LoadMore = false;
  /**
   * <h1>开关</h1>
   * <p> 1表示 可以加载更多
   * <p> 0 表示 没有更多可加载了
   * <p>当为0的时候 一定无法上啦抓数据<p/>
   */
  private int mLoadmoreitem = NON_UP2LOAD_MORE;

  private BaseLoadMoreBinder.LoadMoreState mLoadMoreState;
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
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      super.onItemRangeMoved(fromPosition, toPosition, itemCount);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
    }

    @Override
    public void onChanged() {
      //数据数量 变化了才需要判断
      if (isShowLoadMoreHolder() && mData.size() != mLastCheckDataSize) {
        //                if(mLoadmoreitem == NEED_UP2LOAD_MORE && mLastCheckDataSize == 0 || mData.size() != mLastCheckDataSize) {
        LLog.llog("load_more 数据发生变化同时数据数量发生变化 检测是否需要触发上拉加载");
        mLastCheckDataSize = mData.size();
        checkUp2loadMore(RecyclerView.SCROLL_STATE_IDLE);
      }
    }
  };
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


  /**
   * 多布局模式 支持上拉刷新
   */
  @Keep
  public LoadMoreWrapperAdapter(JVBrecvAdapter innerAdapter) {
    mInnerAdapter = innerAdapter;
    mData = innerAdapter.getDataList();
    mLastCheckDataSize = mData.size();
  }


  /**
   * 多布局模式 支持上拉刷新
   */
  @Keep
  public LoadMoreWrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> innerAdapter, List data) {
    mInnerAdapter = innerAdapter;
    mData = data;
    mLastCheckDataSize = mData.size();
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    mRecyclerView = recyclerView;
    super.onAttachedToRecyclerView(recyclerView);
    setSpanCount(recyclerView);
    if (mCanUp2LoadMore) {
      registerAdapterDataObserver(mAdapterDataObserver);
      recyclerView.addOnScrollListener(mOnScrollListener);
    }
  }

  /**
   * <p>只在停止滚动的状态检测</p>
   * 检查 是否loadingholder可见，可见则回掉监听的onup2LoadingMore 去加载下一页数据
   */
  private void checkUp2loadMore(int newState) {
    RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
    int lastPosition = 0;
    //当前状态为停止滑动状态SCROLL_STATE_IDLE时
    if (isEnable2LoadMore() && getItemCount() > 0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
      if (layoutManager instanceof GridLayoutManager) {
        //通过LayoutManager找到当前显示的最后的item的position
        lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
      } else if (layoutManager instanceof LinearLayoutManager) {
        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
      } else if (layoutManager instanceof StaggeredGridLayoutManager) {
        //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
        //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
        lastPosition = findMax(lastPositions);
      }
      //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
      //如果相等则说明已经滑动到最后了
      if (lastPosition >= getItemCount() - 1) {
        LLog.llog("loading 上拉提示 item 可见");
        if (!mInLoadingMore) {
          loadLoading();
          mInLoadingMore = true;
          if (mListener != null) {
            mListener.onup2LoadingMore();
          }
        }
      }

      //                    if(mLoadingBinder != null && mLoadingBinder.itemView != null) {
      //                        //或者 loading可见自动加载 下一页
      //                        Rect visiRect = new Rect();
      //                        mLoadingBinder.itemView.getGlobalVisibleRect(visiRect);
      //                        System.out.println(visiRect.toString());
      //                        mLoadingBinder.itemView.getLocalVisibleRect(visiRect);
      //                        System.out.println(visiRect.toString());
      //                        mLoadingBinder.itemView.getWindowVisibleDisplayFrame(visiRect);
      //                        System.out.println(visiRect.toString());
      //                    }
    }
  }

  //找到数组中的最大值
  private int findMax(int[] lastPositions) {
    int max = lastPositions[0];
    for (int value : lastPositions) {
      if (value > max) {
        max = value;
      }
    }
    return max;
  }

  private void setSpanCount(RecyclerView recv) {
    final RecyclerView.LayoutManager layoutManager = recv.getLayoutManager();
    if (layoutManager != null) {
      if (layoutManager instanceof StaggeredGridLayoutManager) {
        mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
      } else if (layoutManager instanceof GridLayoutManager) {
        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new LoadMoreSpanSizeLookup((GridLayoutManager) layoutManager,mData));
      }
    } else {
      Log.e(TAG, "LayoutManager 为空,请先设置 recycleView.setLayoutManager(...)");
    }
  }

  @Override
  public int getItemCount() {
    if (!mData.isEmpty()) {
      return mData.size() + mLoadmoreitem;
    } else {
      return 0;
    }
  }

  @Override
  public final int getItemViewType(int position) {
    if (position == mData.size()) {
      return ITEMTYPE_LOADMORE;
    } else if (position < mData.size()) {
      return mInnerAdapter.getItemViewType(position);
    } else {
      return ITEMTYPE_LOADMORE;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    if (viewType == ITEMTYPE_LOADMORE) {
      if (mLoadingBinder == null) {
        mLoadingBinder = onCreateLoadmoreBinder(parent);
        if (mLoadingBinder == null) {
          mLoadingBinder = new DefaultLoadMoreBinder(this);
          mLoadMoreHolder = (JViewHolder) mLoadingBinder.onCreateViewHolder(inflater, parent);
          getLoadMoreStateBean();
        }
      }
      if (mStaggeredGridLayoutManager != null) {
        StaggeredGridLayoutManager.LayoutParams fullSpanLayoutparam = new StaggeredGridLayoutManager.LayoutParams(
            -1, -2);
        fullSpanLayoutparam.setFullSpan(true);
        ((LinearLayout) mLoadMoreHolder.getView(R.id.recyc_item_tv_loadmore).getParent())
            .setLayoutParams(fullSpanLayoutparam);
      }
      LLog.llog(" ****** onCreateViewHolder : " + mLoadMoreHolder.itemView);
      return mLoadMoreHolder;
    } else {
      if (LApp.isDebug()) {
        RecyclerView.ViewHolder viewHolder = mInnerAdapter.onCreateViewHolder(parent, viewType);
        LLog.llog(" ****** onCreateViewHolder : " + viewHolder.itemView);
        return viewHolder;
      }
      return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }
  }

  @Keep
  private BaseLoadMoreBinder.LoadMoreState getLoadMoreStateBean() {
    if (mLoadMoreState == null) {
      return createLoadmoreStateBean();
    } else {
      return mLoadMoreState;
    }
  }

  protected BaseLoadMoreBinder.LoadMoreState createLoadmoreStateBean() {
    return mLoadMoreState = new BaseLoadMoreBinder.LoadMoreState();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    this.onBindViewHolder(holder, position, null);
  }


  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, List<Object> payloads) {
    if (position < mData.size()) {
      mInnerAdapter.onBindViewHolder(holder, position, payloads);
    } else if (isShowLoadMoreHolder()) {
      mLoadingBinder.onBindViewHolder(holder, mLoadMoreState, payloads);
    }
  }

  /**
   * 设置每页显示的数量
   */
  @Keep
  public LoadMoreWrapperAdapter setPagesize(int pagesize) {
    if (mData.size() < pagesize) {
      LLog.llog(TAG, "mData.size() < pagesize 不显示上拉加载状态");
    }
    this.PAGESIZE = pagesize;
    return this;
  }

  /**
   * 重回 加载状态
   */
  @Keep
  public void loadLoading() {
    if (mLoadingBinder != null) {
      mInLoadingMore = true;
      //mLoadingBinder不应该为null 默认不允许上啦加载会导致nullpointexception
      mLoadingBinder.onLoadMoreState("");
    } else {
      LLog.llog("检查是否默认关闭了上拉加载");
    }
  }

  /**
   * 外部手动调用 加载错误
   */
  @Keep
  public void loadError() {
    if (mLoadingBinder != null) {
      mInLoadingMore = false;
      mLoadingBinder.onLoadErrorState("");
      getLoadMoreStateBean().state = FOOT_STATE_LOAD_ERROR;
      notifyItemChanged(mData.size(), "上拉加载失败");
    } else {
      LLog.llog("检查是否默认关闭了上拉加载");
    }
  }

  /**
   * 外部手动调用 自定义 loading加载内容 设置是否允许上啦加载数据 调用{@link #enAbleLoadMore}
   */
  @Keep
  public void loadCustomMsg(boolean canUp2LoadMore, CharSequence tip) {
    if (TextUtils.isEmpty(tip)) {
      enAbleLoadMore(false);
    } else if (mLoadingBinder != null) {
      enAbleLoadMore(canUp2LoadMore, tip);
    } else {
      LLog.llog("检查是否默认关闭了上拉加载");
    }
  }

  /**
   * 外部手动调用 自定义 loading加载内容 设置 不允许上啦加载数据，不涉及隐藏底部loadingholder
   */
  @Keep
  public void loadCustomMsg(CharSequence tip) {
    loadCustomMsg(false, tip);
  }

  /**
   * 是否 需要 底部的 上拉加载holder
   */
  @Keep
  public boolean isShowLoadMoreHolder() {
    return mLoadmoreitem == NEED_UP2LOAD_MORE;
  }

  /**
   * 是否 允许上拉{同时需要允许显示底部的loadingholder} 抓取数据 回掉监听的 onup2LoadingMore
   */
  @Keep
  public boolean isEnable2LoadMore() {
    return mCanUp2LoadMore;
  }

  /**
   * <p>在{@link #STYLE_FIX_LOADING_HOLDER}模式下 只控制是否允许上啦加载数据，
   * true 表示loadingholder处于loading状态， false表示处于加载结束状态，显示结束提示信息{提示信息可以通过获取loadingholder设置{@link #getLoadingHolderBinder()}
   * 需要自定义提示内容可调用{@link #enAbleLoadMore(boolean)}
   * </p>
   * <p>在STYLE_LOADING_HOLDER_GONE模式下控制是否还有上拉的底部布局loadingholder同时控制是否允许上啦加载数据</p>
   * <h1>注意需要在notify之前调用，该方法会重新设置mCanUp2LoadMore</h1>
   */
  @Keep
  public void enAbleLoadMore(boolean enable) {
    enAbleLoadMore(enable, "");
  }

  /**
   * 同时将模式设置为 STYLE_FIX_LOADING_HOLDER
   */
  @Keep
  public void enAbleLoadMore(boolean enable, CharSequence tips) {
    if (mCanUp2LoadMore != enable) {
      mCanUp2LoadMore = enable;
      if (mRecyclerView != null) {
        if (enable) {
          registerAdapterDataObserver(mAdapterDataObserver);
          mRecyclerView.addOnScrollListener(mOnScrollListener);
        } else {
          unregisterAdapterDataObserver(mAdapterDataObserver);
          mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
      }
      if (!TextUtils.isEmpty(tips) || mLoadMoreWrapperStyle == STYLE_FIX_LOADING_HOLDER) {
        //有提示内容一定显示loadingholder
        mLoadmoreitem = NEED_UP2LOAD_MORE;
        getLoadMoreStateBean().state = enable ? FOOT_STATE_LOAD_NOMORE : FOOT_STATE_LOAD_FINISH;
        getLoadMoreStateBean().tips = tips;
      } else {
        if (enable) {
          mLoadmoreitem = NEED_UP2LOAD_MORE;
          getLoadMoreStateBean().state = FOOT_STATE_LOAD_NOMORE;
          getLoadMoreStateBean().tips = tips;
        } else {
          mLoadmoreitem = NON_UP2LOAD_MORE;
        }
      }
//      notifyItemChanged(mData.size());//会导致 上拉加载holder再次创建
      notifyItemChanged(mData.size(), "上拉加载状态更新:" + enable);
      mInLoadingMore = false;
//    } else {
//      getLoadMoreStateBean().state = FOOT_STATE_LOAD_NOMORE;
//      getLoadMoreStateBean().tips = tips;
    }
  }

  @Keep
  public LoadMoreWrapperAdapter setLoadeMoreWrapperStyle(int style) {
    mLoadMoreWrapperStyle = style;
    return this;
  }

  @Keep
  public LoadMoreWrapperAdapter setOnMoreloadListener(OnMoreloadListener listener) {
    mListener = listener;
    return this;
  }

  @Keep
  public void addMoreList(@NonNull List<T> data) {
    if (checkLists(data)) {
      mInLoadingMore = false;
      int startposition = mData.size();
      mData.addAll(data);
      mLastCheckDataSize = mData.size();
      notifyItemRangeInserted(startposition, data.size());
    }
  }

  @Keep
  public void refreshAllData(@NonNull List<T> data) {
    changeAllData(data);
  }

  @Keep
  public void changeAllData(@NonNull List<T> data) {
    if (checkLists(data)) {
      checkPageSize(data.size());
      mInLoadingMore = false;
      int size = mData.size();
      if (size > 0) {
        mData.clear();
        notifyItemRangeRemoved(0, size);
      }
      mData.addAll(data);
      mLastCheckDataSize = data.size();
      notifyDataSetChanged();
//      notifyItemRangeInserted(0, mLastCheckDataSize);
    }
  }

  private void checkPageSize(int size) {
    if (mCanUp2LoadMore) {
      enAbleLoadMore(size > PAGESIZE);
    }
  }

  @Keep
  public void removeItem(int position) {
    if (position < mData.size()) {
      mData.remove(position);
      if (isShowLoadMoreHolder() && mData.size() == PAGESIZE) {
        notifyDataSetChanged();
      } else {
        notifyItemRemoved(position);
      }
    } else {
      LLog.llog(TAG, "position out of bounde of mData.size()");
    }
  }

  @Keep
  public void removeItem(T item) {
    int index = mData.indexOf(item);
    if (index > -1) {
      mData.remove(index);
      if (isEnable2LoadMore() && mData.size() == PAGESIZE) {
        notifyDataSetChanged();
      } else {
        notifyItemRemoved(index);
      }
    } else {
      LLog.llog(TAG, "position out of bounde of mData.size()");
    }
  }


  @Keep
  public void addItem(T data, int position) {
    mInLoadingMore = false;
    if (position > mData.size()) {
      LLog.llog(TAG, position + " > mData.size():" + mData.size());
      return;
    }
    mData.add(position, data);
    mLastCheckDataSize = mData.size();
    notifyItemInserted(position);
  }


  /**
   * 自定义实现上拉加载布局 //最好包括 三种状态 上拉加载中,加载失败,没有更多 ,可自由切换 同时需要复写 {@link #loadError()},{@link #enAbleLoadMore(boolean)} }
   */
  @Keep
  public BaseLoadMoreBinder onCreateLoadmoreBinder(ViewGroup parent) {
    return null;
  }

  @Override
  public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    if (mInnerAdapter!=null) {
      mInnerAdapter.onViewAttachedToWindow(holder);
    }
  }

  @Override
  public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    if (mInnerAdapter!=null) {
      mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
    }
    if (mLoadingBinder != null) {
      mLoadingBinder.onDetachedFromRecyclerView(recyclerView);
    }
  }

  @Override
  public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    if (mInnerAdapter!=null) {
      mInnerAdapter.onViewDetachedFromWindow(holder);
    }
    if (mLoadingBinder != null) {
      mLoadingBinder.onViewDetachedFromWindow(holder);
    }
  }

  @Override
  public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
    super.onViewRecycled(holder);
    LLog.llog(" ****** onViewRecycled : " + holder.itemView);
    if (mInnerAdapter != null) {
      mInnerAdapter.onViewRecycled(holder);
    }
  }

  @Override
  public void onItemClicked(View view, Object itemData) {
    if (!mInLoadingMore) {
      //没有正在拉取数据
      mInLoadingMore = true;
      LLog.llog(TAG, "点击加载更多");
      if (isShowLoadMoreHolder() && mListener != null) {
        mListener.retryUp2LoadingMore();
      }
    }
  }

  @Keep
  public JViewHolder getLoadingHolder() {
    return mLoadMoreHolder;
  }

  @Keep
  public JRecvBaseBinder getLoadingHolderBinder() {
    return mLoadingBinder;
  }

  @Keep
  public static class LoadMoreSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    GridLayoutManager mGridLayoutManager;
    List<Object> mItems;

    public LoadMoreSpanSizeLookup(GridLayoutManager gridLayoutManager, List<Object> items) {
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
}
