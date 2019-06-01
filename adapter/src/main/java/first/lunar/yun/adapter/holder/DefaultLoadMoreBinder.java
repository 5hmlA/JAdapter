package first.lunar.yun.adapter.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import first.lunar.yun.adapter.LConsistent;
import first.lunar.yun.adapter.R;
import first.lunar.yun.adapter.face.OnViewClickListener;

import static first.lunar.yun.adapter.LApp.findString;

/**
 * @another 江祖赟
 * @date 2017/10/28 0028.
 */
public class DefaultLoadMoreBinder extends BaseLoadMoreBinder<BaseLoadMoreBinder.LoadMoreState> {

  public JViewHolder mLoadMoreHolder;
  private OnViewClickListener mViewClickListener;
  private CharSequence mNomoreLoadTipsIfneed = "=== 我是有底线的 ===";

  public DefaultLoadMoreBinder(OnViewClickListener viewClickListener) {
    mViewClickListener = viewClickListener;
    System.out.println(" ====== DefaultLoadMoreBinder  ");
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull final LoadMoreState item) {
    System.out.println(" ========  "+holder.itemView);
    if (item.state.equals(FOOT_STATE_LOAD_ERROR)) {
      onLoadErrorState(item.tips);
    } else if (item.state.equals(FOOT_STATE_LOAD_NOMORE)) {
      onLoadMoreState(item.tips);
    } else {
      onNomoreLoadTips(item.tips);
    }
  }

  public void onLoadErrorClick(@NonNull Object item) {
    if (FOOT_STATE_LOAD_ERROR.equals(mRootView.getTag(LConsistent.ViewTag.view_tag5))) {
      mViewClickListener.onItemClicked(mRootView, item);
      //加载错误状态==点击===转为 加载状态！
      onLoadMoreState("");
      //点击重试之后变成加载更多
    } else {
      //正常状态 ，一般不可达
    }
  }

  @NonNull
  @Override
  public JViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    mLoadMoreHolder = new JViewHolder(
        mRootView = inflater.inflate(R.layout.default_recyc_loading_more, parent, false));
    System.out.println(" ====== onCreateViewHolder  "+mLoadMoreHolder.itemView);
    rootViewLoadingTag(FOOT_STATE_LOAD_NOMORE);//holder处于 loadmore状态
    mLoadMoreHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onLoadErrorClick(mLoadMoreHolder);
      }
    });
//        RxView.clicks(mLoadMoreHolder.itemView).throttleFirst(300, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception{
//                        onLoadErrorClick(mLoadMoreHolder);
//                    }
//                });
    return mLoadMoreHolder;
  }

  /**
   * 设置holder的当前状态 主要 用于 状态切换的时候 防止被多次设置同一个状态，没必要
   */
  public void rootViewLoadingTag(String tag) {
    mRootView.setTag(LConsistent.ViewTag.view_tag5, tag);
  }

  public boolean checkRootViewLoadingTag(String tag) {
    return tag.equals(mRootView.getTag(LConsistent.ViewTag.view_tag5));
  }

  /**
   * 重新设置holder到loadmore界面和状态
   */
  public void onLoadMoreState(CharSequence tips) {
    if (!checkRootViewLoadingTag(FOOT_STATE_LOAD_NOMORE)) {
      rootViewLoadingTag(FOOT_STATE_LOAD_NOMORE);
      mLoadMoreHolder.setText(R.id.recyc_item_tv_loadmore,
          TextUtils.isEmpty(tips) ? findString(R.string.jonas_recyc_loading_more) : tips);
      mLoadMoreHolder.visibleViews(R.id.recyc_item_pb_loadmore);
    }
  }

  /**
   * 重新设置holder到loaderror界面和状态
   */
  public void onLoadErrorState(CharSequence tips) {
    rootViewLoadingTag(FOOT_STATE_LOAD_ERROR);
    mLoadMoreHolder.visibleViews(R.id.recyc_item_pb_loadmore);
    mLoadMoreHolder.setText(R.id.recyc_item_tv_loadmore,
        TextUtils.isEmpty(tips) ? findString(R.string.jonas_recyc_load_retry) : tips);
  }

  public void onLoadCustomState(CharSequence msg) {
    rootViewLoadingTag(FOOT_STATE_LOAD_ERROR);
    mLoadMoreHolder.setText(R.id.recyc_item_tv_loadmore, msg);
    mLoadMoreHolder.goneViews(R.id.recyc_item_pb_loadmore);
  }

  @Override
  public void onNomoreLoadTips(CharSequence msg) {
    rootViewLoadingTag(FOOT_STATE_LOAD_FINISH);
    mLoadMoreHolder.goneViews(R.id.recyc_item_pb_loadmore);
    if (!TextUtils.isEmpty(msg)) {
      mLoadMoreHolder.setText(R.id.recyc_item_tv_loadmore, msg);
    } else {
      mLoadMoreHolder.setText(R.id.recyc_item_tv_loadmore, mNomoreLoadTipsIfneed);
    }
  }

  @Override
  public void bindNomoreLoadTipsIfneed(CharSequence msg) {
    mNomoreLoadTipsIfneed = msg;
  }
}
