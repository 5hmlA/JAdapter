package sparkj.adapter.decoration;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import sparkj.adapter.helper.LLog;

/**
 * @another 江祖赟
 * @date 2017/9/27 0027.
 */
@Keep
public class JLinearLayoutManager extends LinearLayoutManager {

    public JLinearLayoutManager(Context context){
        super(context);
    }

    public JLinearLayoutManager(Context context, int orientation, boolean reverseLayout){
        super(context, orientation, reverseLayout);
    }

    public JLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state){
        try {
            super.onLayoutChildren(recycler, state);
        }catch(IndexOutOfBoundsException e) {
            LLog.llog(Log.getStackTraceString(e));
        }
    }
}
