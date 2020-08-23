package first.lunar.yun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.LLog;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.ArrayList;
import java.util.List;

import static first.lunar.yun.adapter.helper.CheckHelper.checkLists;

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

  @Keep
  public JVBrecvAdapter(List<D> list) {
    mDataList = list;
  }

  @Keep
  public JVBrecvAdapter(List<D> dataList, OnViewClickListener<D> onViewClickListener) {
    mDataList = dataList;
    mOnViewClickListener = onViewClickListener;
  }

  @Keep
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
    return mDataList.size();
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
      D d = JViewHolder.getViewTag(v);
      mOnViewClickListener.onItemClicked(v, d);
    }
  }
  @Keep
  public void addMoreList(@NonNull List<D> data) {
    if (checkLists(data)) {
      int startposition = mDataList.size();
      mDataList.addAll(data);
      notifyItemRangeInserted(startposition, data.size());
    }
  }

  @Keep
  public void refreshAllData(@NonNull List<D> data) {
    changeAllData(data);
  }

  @Keep
  public void changeAllData(@NonNull List<D> data) {
    if (checkLists(data)) {
      int size = mDataList.size();
      if (size > 0) {
        mDataList.clear();
        notifyItemRangeRemoved(0, size);
      }
      notifyDataSetChanged();
    }
  }

  @Keep
  public void removeItem(int position) {
    if (position < mDataList.size()) {
      mDataList.remove(position);
      notifyItemRemoved(position);
    }
  }

  @Keep
  public void removeItem(D item) {
    int index = mDataList.indexOf(item);
    if (index > -1) {
      removeItem(index);
    }
  }


  @Keep
  public void addItem(D data, int position) {
    if (position > mDataList.size()) {
      LLog.llog("JVBrecvAdapter", position + " > mData.size():" + mDataList.size());
      return;
    }
    mDataList.add(position, data);
    notifyItemInserted(position);
  }

}
