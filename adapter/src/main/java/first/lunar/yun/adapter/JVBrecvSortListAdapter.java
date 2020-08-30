package first.lunar.yun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;
import first.lunar.yun.adapter.face.IVBrecvAdapter;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class JVBrecvSortListAdapter extends RecyclerView.Adapter<JViewHolder> implements IVBrecvAdapter<JViewBean>,
    View.OnClickListener {

  private OnViewClickListener<JViewBean> mOnViewClickListener;
  private SortedList<JViewBean> mSortedList;

  @Keep
  public JVBrecvSortListAdapter(SortedList<JViewBean> list) {
    this(list, null);
  }

  @Keep
  public JVBrecvSortListAdapter(SortedList<JViewBean> dataList, OnViewClickListener<JViewBean> onViewClickListener) {
    mSortedList = dataList;
    mOnViewClickListener = onViewClickListener;
  }

  @Override
  public int getItemViewType(int position) {
    return mSortedList.get(position).bindLayout();
  }

  @NonNull
  @Override
  public JViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemLayout) {
    return new JViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull JViewHolder jViewHolder, int position) {
    this.onBindViewHolder(jViewHolder, position, null);
  }

  @Override
  public void onBindViewHolder(@NonNull JViewHolder holder, int position, @NonNull List<Object> payloads) {
    final JViewBean d = mSortedList.get(position);
    holder.setHoldVBean(d);
    d.setPosition(position);
    if (mOnViewClickListener != null) {
      JViewHolder.setViewTag(holder.itemView, d);
      holder.itemView.setOnClickListener(this);
    }
    d.onBindViewHolder(holder, position, payloads, mOnViewClickListener);
  }

  @Keep
  @Override
  public int getItemCount() {
    return mSortedList.size();
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

  @Override
  public void onClick(View v) {
    if (mOnViewClickListener != null) {
      JViewBean d = JViewHolder.getViewTag(v);
      mOnViewClickListener.onItemClicked(v, d);
    }
  }

  @Keep
  public void addMoreList(@NonNull List<JViewBean> data) {
    //nothing
  }

  @Keep
  public void refreshAllData(@NonNull List<JViewBean> data) {
    //nothing
  }

  @Keep
  public void removeItem(int position) {
    //nothing
  }

  @Override
  public void removeItem(JViewBean item) {
    //nothing
  }

  @Override
  public void addItem(JViewBean data, int position) {
    //nothing
  }

  @Keep
  public void diffAll(List<JViewBean> newData) {
    //nothing
  }

  @Keep
  public void diffAll(final List<JViewBean> newData, final boolean detectMoves) {
    //nothing
  }

  @Override
  public int getDataSize() {
    return mSortedList.size();
  }
  
  public static final class JVBSortList extends SortedList<JViewBean>{

    public JVBSortList(JVBrecvAdapter<JViewBean> jvBrecvAdapter){
      super(JViewBean.class, new JVBSortListCallback(jvBrecvAdapter));
    }
  
  }
  
  public static final class JVBSortListCallback extends SortedListAdapterCallback<JViewBean>{
    
    /**
     * Creates a {@link SortedList.Callback} that will forward data change events to the provided Adapter.
     *
     * @param adapter The Adapter instance which should receive events from the SortedList.
     */
    public JVBSortListCallback(RecyclerView.Adapter adapter) {
      super(adapter);
    }

    @Override
    public int compare(JViewBean oldData, JViewBean newData) {
      return oldData.compare(newData);
    }

    @Override
    public boolean areContentsTheSame(JViewBean oldItem, JViewBean newItem) {
      return oldItem.areContentsTheSame(newItem);
    }

    @Override
    public boolean areItemsTheSame(JViewBean oldItem, JViewBean newItem) {
      return oldItem.areContentsTheSame(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(JViewBean oldItem, JViewBean newItem) {
      return oldItem.getChangePayload(newItem);
    }
  }
}
