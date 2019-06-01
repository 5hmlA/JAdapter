package first.lunar.yun.adapter.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author yun.
 * @date 2017/9/21
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class GrideDividerDecoration extends RecyclerView.ItemDecoration {

    private int mDivider;
    private boolean mRepading;

    public GrideDividerDecoration(int divider){
        mDivider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        //recvcleview中item外部其实有包裹一个矩阵 rect就是矩阵的pading
        GridLayoutManager gridLayoutManager = (GridLayoutManager)parent.getLayoutManager();
        int spanCount = gridLayoutManager.getSpanCount();
        if(parent.getChildLayoutPosition(view)<spanCount) {

        }
        //        if(!mRepading) {
        //            mRepading = true;
        //            parent.setPadding(mDivider/2, parent.getPaddingTop()+mDivider/2, mDivider/2, mDivider/2);
        //        }
        //不是第一个的格子都设一个左边的间距
        outRect.top = mDivider/2;
        outRect.left = mDivider/2;
        outRect.right = mDivider/2;
        outRect.bottom = mDivider/2;
        //由于每行第一个，把左边距设为0
        //        if(parent.getChildLayoutPosition(view)%spanCount == 0) {
        //            outRect.left = 0;
        //        }
    }
}
