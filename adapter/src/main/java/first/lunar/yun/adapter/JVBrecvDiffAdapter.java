package first.lunar.yun.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yun.
 * @date 2020/8/29 0029
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
class JVBrecvDiffAdapter<D extends JViewBean> extends JVBrecvAdapter<D>{

  private AsyncListDiffer<D> mDAsyncListDiffer = new AsyncListDiffer<D>(this, new DiffUtil.ItemCallback<D>() {
    @Override
    public boolean areItemsTheSame(@NonNull D oldItem, @NonNull D newItem) {
      return oldItem.areItemsTheSame(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull D oldItem, @NonNull D newItem) {
      return oldItem.areContentsTheSame(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(@NonNull D oldItem, @NonNull D newItem) {
      return oldItem.getChangePayload(newItem);
    }
  });

  public JVBrecvDiffAdapter() {
    super();
  }

  public JVBrecvDiffAdapter(OnViewClickListener<D> onViewClickListener) {
    super(onViewClickListener);
  }

  @Deprecated
  public JVBrecvDiffAdapter(List<D> list) {
    throw new RuntimeException("you should use JVBrecvDiffAdapter()");
  }

  @Deprecated
  public JVBrecvDiffAdapter(List<D> dataList, OnViewClickListener<D> onViewClickListener) {
    throw new RuntimeException("you should use JVBrecvDiffAdapter(OnViewClickListener<D> onViewClickListener)");
  }

  @Override
  public void diffAll(List<D> newData, boolean detectMoves) {
    setDataList(newData);
    mDAsyncListDiffer.submitList(newData);
  }

  @Override
  public void addItem(D data, int position) {
    //AsyncListDiffer不支持 只支持刷新所有数据计算差异
    ArrayList<D> newData = new ArrayList<>(getDataList());
    newData.add(position, data);
    diffAll(newData);
  }

  @Override
  public void addMoreList(@NonNull List<D> data) {
    ArrayList<D> newData = new ArrayList<>(getDataList());
    newData.addAll(data);
    diffAll(newData);
  }

  @Override
  public void removeItem(D item) {
    ArrayList<D> newData = new ArrayList<>(getDataList());
    newData.remove(item);
    diffAll(newData);
  }

  @Override
  public void removeItem(int position) {
    ArrayList<D> newData = new ArrayList<>(getDataList());
    newData.remove(position);
    diffAll(newData);
  }

}
