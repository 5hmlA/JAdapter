package first.lunar.yun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.LApp;
import first.lunar.yun.adapter.face.AdapterKnife;
import first.lunar.yun.adapter.face.JOnClickListener;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JVBrecvAdapter<D extends JViewBean> extends RecyclerView.Adapter<JViewHolder> implements AdapterKnife<D> {

  protected List<D> mDataList = new ArrayList<>();
  private OnViewClickListener<D> mOnViewClickListener;
  JOnClickListener jOnClickListener = new JOnClickListener() {
    @Override
    protected void doClick(View v) {
      if (mOnViewClickListener != null) {
        D d = JViewHolder.getViewTag(v);
        mOnViewClickListener.onItemClicked(v, d);
      }
    }
  };

  @Keep
  public JVBrecvAdapter() {
  }

  @Keep
  public JVBrecvAdapter(OnViewClickListener<D> onViewClickListener) {
    mOnViewClickListener = onViewClickListener;
  }

  @Keep
  @Deprecated
  public JVBrecvAdapter(List<D> list) {
    this(list, null);
  }

  @Keep
  @Deprecated
  public JVBrecvAdapter(List<D> dataList, OnViewClickListener<D> onViewClickListener) {
    this();
    mDataList = dataList;
    mOnViewClickListener = onViewClickListener;
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    LApp.fly(recyclerView.getContext().getApplicationContext());
  }

  @Keep
  public List<D> getDataList() {
    return mDataList;
  }

  @Override
  public int getItemViewType(int position) {
    return getDataList().get(position).bindLayout();
  }

  @NonNull
  @Override
  @Keep
  public JViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemLayout) {
    JViewHolder jViewHolder = new JViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false));
    jViewHolder.itemView.setOnClickListener(jOnClickListener);
    return jViewHolder;
  }

  @Override
  @Keep
  public void onBindViewHolder(@NonNull JViewHolder jViewHolder, int position) {
    this.onBindViewHolder(jViewHolder, position, Collections.emptyList());
  }

  @Override
  @Keep
  public void onBindViewHolder(@NonNull JViewHolder holder, int position, @NonNull List<Object> payloads) {
    final D d = getItemData(position);
    holder.setHoldVBean(d)
        .setAdapterKnife(this)
        .keepList(getDataList());
    d.setPosition(position);
    if (mOnViewClickListener != null) {
      JViewHolder.setViewTag(holder.itemView, d);
    }
    d.onBindViewHolder(holder, position, payloads, mOnViewClickListener);
  }

  protected D getItemData(int position) {
    return getDataList().get(position);
  }

  @Keep
  @Override
  public int getItemCount() {
    return getDataList().size();
  }

  @Override
  public void onViewAttachedToWindow(@NonNull JViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    JViewBean holdVBean = holder.getHoldVBean();
    if (holdVBean != null) {
      holdVBean.onViewAttachedToWindow(holder);
    }
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    JViewBean holdVBean = holder.getHoldVBean();
    if (holdVBean != null) {
      holdVBean.onViewDetachedFromWindow(holder);
    }
  }

  @Override
  public void onViewRecycled(@NonNull JViewHolder holder) {
    super.onViewRecycled(holder);
    JViewBean holdVBean = holder.getHoldVBean();
    if (holdVBean != null) {
      holdVBean.onViewRecycled(holder);
    }
  }

  public void notifyDataSetChanged(List<D> dataList) {
    mDataList.clear();
    mDataList.addAll(dataList);
    notifyDataSetChanged();
  }

  @Override
  public void remove(int position) {
    mDataList.remove(position);
    notifyItemRemoved(position);
  }

  @Override
  public void remove(D data) {
    remove(mDataList.indexOf(data));
  }

  @Override
  public void addData(List<D> datas) {
    int size = mDataList.size();
    addData(size, datas);
  }

  @Override
  public void addData(int position, List<D> datas) {
    mDataList.addAll(position, datas);
    notifyItemRangeInserted(position, datas.size());
  }
}
