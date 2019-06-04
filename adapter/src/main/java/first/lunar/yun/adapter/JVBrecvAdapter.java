package first.lunar.yun.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class JVBrecvAdapter<D extends JViewBean> extends RecyclerView.Adapter<JViewHolder> implements View.OnClickListener {

  private List<D> mDataList = new ArrayList<>();

  private OnViewClickListener<D> mOnViewClickListener;

  public JVBrecvAdapter(List<D> list) {
    mDataList = list;
  }

  public JVBrecvAdapter(List<D> dataList, OnViewClickListener<D> onViewClickListener) {
    mDataList = dataList;
    mOnViewClickListener = onViewClickListener;
  }

  public List<D> getDataList() {
    return mDataList;
  }

  @Override
  public int getItemViewType(int position) {
    return mDataList.get(position).bindLayout();
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
    final D d = mDataList.get(position);
    d.setPosition(position);
    if (d.getActivity() == null) {
      d.bindActivity(holder.getActivity());
    }
    if (mOnViewClickListener != null) {
      JViewHolder.setViewTag(holder.itemView, d);
      holder.itemView.setOnClickListener(this);
    }
    d.onBindViewHolder(holder, position, payloads, mOnViewClickListener);
  }

  @Override
  public int getItemCount() {
    return mDataList.size();
  }

  @Override
  public void onViewAttachedToWindow(@NonNull JViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    int adapterPosition = holder.getAdapterPosition();
    mDataList.get(adapterPosition).onViewAttachedToWindow(holder);
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    int adapterPosition = holder.getAdapterPosition();
    mDataList.get(adapterPosition).onViewDetachedFromWindow(holder);
  }

  @Override
  public void onClick(View v) {
    if (mOnViewClickListener != null) {
      D d = JViewHolder.getViewTag(v);
      mOnViewClickListener.onItemClicked(v, d);
    }
  }
}
