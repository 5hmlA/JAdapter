package first.lunar.yun.adapter.vb;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import first.lunar.yun.adapter.AbsLoadMoreWrapperAdapter;
import first.lunar.yun.adapter.R;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import java.util.List;

import static first.lunar.yun.LApp.findString;

/**
 * @author yun.
 * @date 2021/4/6 0006
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JLoadMoreVb extends JViewBean implements View.OnClickListener {

  @Keep
  public JLoadMoreVb() {
  }

  @Nullable
  private OnViewClickListener mViewClickListener;

  AbsLoadMoreWrapperAdapter.HolderState mLoadState = AbsLoadMoreWrapperAdapter.HolderState.LOADING;

  @Override
  public int bindLayout() {
    return R.layout.default_recyc_loading_more;
  }

  @Override
  public final void onBindViewHolder(JViewHolder holder, int position, @Nullable List<Object> payloads,
                                @Nullable OnViewClickListener viewClickListener) {
    mViewClickListener = viewClickListener;
    if (!payloads.isEmpty()) {
      mLoadState = (AbsLoadMoreWrapperAdapter.HolderState) payloads.get(0);
    }
    if (mLoadState == AbsLoadMoreWrapperAdapter.HolderState.LOADNOMORE) {
      showNoMoreLoad(holder, mLoadState.getTips());
    } else if (mLoadState == AbsLoadMoreWrapperAdapter.HolderState.LOADING) {
      showLoading(holder, mLoadState.getTips());
    } else if (mLoadState == AbsLoadMoreWrapperAdapter.HolderState.LOADERETRY) {
      showLoadError(holder, mLoadState.getTips());
    }
  }

  /**
   * 重新设置holder到loaderror界面和状态
   */
  protected void showLoadError(JViewHolder holder, CharSequence tips) {
    holder.visibleViews(R.id.recyc_item_pb_loadmore);
    holder.setText(R.id.recyc_item_tv_loadmore,
        TextUtils.isEmpty(tips) ? findString(R.string.jonas_recyc_load_retry) : tips);
    holder.setOnClickListener(this);
  }

  protected void showLoading(JViewHolder holder, CharSequence msg) {
    holder.setText(R.id.recyc_item_tv_loadmore, TextUtils.isEmpty(msg) ? findString(R.string.jonas_recyc_loading_more) : msg);
    holder.visibleViews(R.id.recyc_item_pb_loadmore);
    holder.setOnClickListener(null);
  }


  protected void showNoMoreLoad(JViewHolder holder, CharSequence msg) {
    holder.goneViews(R.id.recyc_item_pb_loadmore);
    if (!TextUtils.isEmpty(msg)) {
      holder.setText(R.id.recyc_item_tv_loadmore, msg);
    } else {
      holder.setText(R.id.recyc_item_tv_loadmore, R.string.jonas_recyc_load_finish);
    }
    holder.setOnClickListener(null);
  }

  @Override
  public final void onClick(View v) {
    mViewClickListener.onItemClicked(v, this);
  }

  public final void setLoadState(AbsLoadMoreWrapperAdapter.HolderState loadState) {
    mLoadState = loadState;
  }
}
