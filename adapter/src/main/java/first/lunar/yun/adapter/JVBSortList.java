package first.lunar.yun.adapter;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;
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
  public JVBSortList(JVBrecvAdapter<JViewBean> jvBrecvAdapter){
    super(JViewBean.class, new JVBSortListCallback(jvBrecvAdapter));
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
      return oldItem.areItemsTheSame(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(JViewBean oldItem, JViewBean newItem) {
      return newItem.getChangePayload(oldItem);
    }
  }
}
  

