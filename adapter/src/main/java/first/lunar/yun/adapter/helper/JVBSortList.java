package first.lunar.yun.adapter.helper;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.SortedList;
import first.lunar.yun.adapter.JVBSortListAdapter;
import first.lunar.yun.adapter.vb.JViewBean;

/**
 * @author yun.
 * @date 2019/6/1 0001
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JVBSortList extends SortedList<JViewBean>{

  @Keep
  public JVBSortList(JVBSortListAdapter jvBrecvAdapter){
    super(JViewBean.class, new JVBSortListCallback(jvBrecvAdapter));
  }

  public static final class JVBSortListCallback extends SortedList.Callback<JViewBean>{

    private JVBSortListAdapter mAdapter;

    /**
     * Creates a {@link Callback} that will forward data change events to the provided Adapter.
     *
     * @param adapter The Adapter instance which should receive events from the SortedList.
     */
    public JVBSortListCallback(JVBSortListAdapter adapter) {
      mAdapter = adapter;
    }

    @Override
    public int compare(JViewBean oldData, JViewBean newData) {
      return oldData.compare(newData);
    }

    @Override
    public void onChanged(int position, int count) {
      mAdapter.onChanged(position,count,null);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
      mAdapter.onChanged(position, count, payload);
    }

    @Override
    public boolean areContentsTheSame(JViewBean oldItem, JViewBean newItem) {
      return oldItem.areContentsTheSame(newItem);
    }

    @Override
    public boolean areItemsTheSame(JViewBean oldItem, JViewBean newItem) {
      return oldItem.areItemsTheSame(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(JViewBean oldItem, JViewBean newItem) {
      return newItem.getChangePayload(oldItem);
    }

    @Override
    public void onInserted(int position, int count) {
      mAdapter.onInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
      mAdapter.onRemoved(position,count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
      mAdapter.onMoved(fromPosition, toPosition);

    }
  }
}
  

