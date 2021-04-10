package first.lunar.yun.adapter.holder;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.LApp;
import first.lunar.yun.adapter.face.AdapterKnife;
import first.lunar.yun.adapter.vb.JViewBean;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
@Keep
public class JViewHolder extends RecyclerView.ViewHolder {
  private static final int NO_COLOR = -19910113;
  private static final int JVIEW_TAG = 0x20190601;
  private final SparseArray<WeakReference<View>> mCacheViews;
  private String tag = JViewHolder.class.getSimpleName();
  private final WeakReference<Activity> mActivityWeakReference;
  private JViewBean mHoldVBean;
  private AdapterKnife mAdapterKnife;
  private List mList;
  private Object extra;

  public <E> E getExtra() {
    return (E) extra;
  }
  
  public <E> void setExtra(E extra) {
    this.extra = extra;
  }

  public JViewHolder(View itemView) {
    super(itemView);
    if (LApp.getContext() == null) {
      LApp.fly(itemView.getContext().getApplicationContext());
    }
    mActivityWeakReference = new WeakReference<>(getActivity(itemView));
    mCacheViews = new SparseArray<>(10);
  }
  
  public Activity getActivity() {
    if (mActivityWeakReference == null || mActivityWeakReference.get() == null) {
      return getActivity(itemView);
    } else {
      return mActivityWeakReference.get();
    }
  }
  
  public Activity getActivity(View view) {
    return LApp.getAct4View(view);
  }

  public <V extends View> V getView(int viewId) {
    return getView(itemView, viewId);
  }

  public <V extends View> V getView(View rootView, int viewId) {
    View view = null;
    if (mCacheViews.get(viewId) != null) {
      view = mCacheViews.get(viewId).get();
      if (view == null) {
        view = rootView.findViewById(viewId);
        mCacheViews.put(viewId, new WeakReference<View>(view));
      }
    } else {
      view = rootView.findViewById(viewId);
      mCacheViews.put(viewId, new WeakReference<View>(view));
    }
    return (V) view;
  }

  public JViewHolder setText(int viewId, int strRes, @ColorRes int colorRes) {
    TextView textView = getView(viewId);
//    if (textView != null) { //不判断空 好知道是否写错了ID
    String text = textView.getContext().getResources().getString(strRes);
    setText(viewId, text, colorRes);
    return this;
  }
  
  public JViewHolder setText(int viewId, @StringRes int strRes) {
    return setText(viewId, strRes, NO_COLOR);
  }

  public JViewHolder setText(int viewId, CharSequence text) {
    return setText(viewId, text, NO_COLOR);
  }
  
  public JViewHolder setText(int viewId, CharSequence text, @ColorRes int colorRes) {
    int color = NO_COLOR;
    if (NO_COLOR != colorRes) {
      color = ContextCompat.getColor(getView(viewId).getContext(), colorRes);
    }
    return setTextWithColor(viewId,text,color);
  }

  public JViewHolder setTextWithColor(int viewId, CharSequence text, @ColorInt int color) {
    TextView textView = getView(viewId);
    if (!TextUtils.isEmpty(text)) {
      textView.setVisibility(View.VISIBLE);
      if (color != NO_COLOR) {
        textView.setTextColor(color);
      }
      textView.setText(text);
    }
    return this;
  }

  /**
   * 当 text为空的时候会设置textview不可见
   * @param viewId
   * @param text
   * @return
   */
  
  public JViewHolder setText2(int viewId, CharSequence text) {
    return setText2(viewId, text, NO_COLOR);
  }

  /**
   * 当 text为空的时候会设置textview不可见
   * @param viewId
   * @param text
   * @param color
   * @return
   */
  
  public JViewHolder setText2(int viewId, CharSequence text, @ColorInt int color) {
    TextView textView = getView(viewId);
    if (!TextUtils.isEmpty(text)) {
      textView.setVisibility(View.VISIBLE);
      if (NO_COLOR != color) {
        textView.setTextColor(color);
      }
      textView.setText(text);
    } else {
      textView.setVisibility(View.GONE);
    }
    return this;
  }

  public JViewHolder setTextColorState(int viewId, ColorStateList colors) {
    TextView tv = getView(viewId);
    tv.setTextColor(colors);
    return this;
  }

  public JViewHolder setVisibility(int visibility, int... viewIds) {
    for (int viewId : viewIds) {
      View view = getView(viewId);
      if (view != null) {
        view.setVisibility(visibility);
      }
    }
    return this;
  }

  public JViewHolder goneViews(int... viewIds) {
    return setVisibility(View.GONE, viewIds);
  }
  
  public JViewHolder visibleViews(int... viewIds) {
    return setVisibility(View.VISIBLE, viewIds);
  }
  
  public int getVisibility(int viewId) {
    View view = getView(viewId);
    if (view == null) {
      return View.GONE;
    }
    return view.getVisibility();
  }

  /**
   * 为ImageView设置图片
   */
  
  public JViewHolder setImageResource(int viewId, @DrawableRes int drawableId) {
    ImageView view = getView(viewId);
    view.setImageResource(drawableId);
    return this;
  }

  /**
   * 为ImageView设置图片
   */
  
  public JViewHolder setImageBitmap(int viewId, Bitmap bm) {
    ImageView view = getView(viewId);
    view.setImageBitmap(bm);
    return this;
  }

  public JViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener l) {
    getView(viewId).setOnLongClickListener(l);
    return this;
  }
  
  public JViewHolder setOnClickListener(View.OnClickListener l, int... viewIds) {
    for (int viewId : viewIds) {
      getView(viewId).setOnClickListener(l);
    }
    return this;
  }

  public JViewHolder setOnClickListener(View.OnClickListener l) {
    this.itemView.setOnClickListener(l);
    return this;
  }

  public JViewHolder setOnLongclickListener(View.OnLongClickListener l) {
    this.itemView.setOnLongClickListener(l);
    return this;
  }
  
  public String getTag() {
    return tag;
  }

  public JViewHolder setTag(String tag) {
    this.tag = tag;
    return this;
  }

  public static void setViewTag(View view, Object Tag) {
    if (Tag != null) {
      view.setTag(JVIEW_TAG, Tag);
    }
  }

  @Nullable
  public static <T> T getViewTag(View view) {
    return (T) view.getTag(JVIEW_TAG);
  }

  public <D extends JViewBean> JViewHolder setHoldVBean(JViewBean holdVBean) {
    mHoldVBean = holdVBean;
    return this;
  }
  
  public JViewBean getHoldVBean() {
    return mHoldVBean;
  }

  public <D extends JViewBean> JViewHolder setAdapterKnife(AdapterKnife adapterKnife) {
    mAdapterKnife = adapterKnife;
    return this;
  }

  public AdapterKnife getAdapterKnife() {
    return mAdapterKnife;
  }

  public List getList() {
    return mList;
  }

  public JViewHolder keepList(List list) {
    mList = list;
    return this;
  }

}
