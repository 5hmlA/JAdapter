package first.lunar.yun.adapter;

import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.adapter.helper.Damping;
import first.lunar.yun.adapter.vb.JViewBean;


/**
 * @des [recycleview适配器 基类，上拉加载更多,多类型布局,拖拽,滑动删除 支持]
 */
public class LoadMoreWrapperDampAdapter<T extends JViewBean> extends LoadMoreWrapperAdapter<T> {

    public boolean mIsNeedDamp = true;

    public LoadMoreWrapperDampAdapter(JVBrecvAdapter innerAdapter) {
        super(innerAdapter);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
        if(mIsNeedDamp) {
            Damping.wrapper(mRecyclerView).configDirection(LinearLayout.VERTICAL);
        }
    }
}
