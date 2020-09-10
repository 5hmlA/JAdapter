package first.lunar.yun.adapter.decoration;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author yun.
 * @date 2017/9/21
 * @des [均分Grid布局 两边对其边框, 中间平分]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class GrideSpreadInsideDecoration extends RecyclerView.ItemDecoration {

    /**
     * 中间边距
     */
    private int mMiddleInterval;
    /**
     * 底部边距
     */
    private int mBottomInterval;
    /**
     * 每个item所在的左下角(左边)x坐标
     */
    private float mItemLeftAbscissa = 0;
    /**
     * 每个item所在的有下角(右边)x坐标
     */
    private float mItemRightAbscissa = 0;

    @Keep
    public GrideSpreadInsideDecoration(int middleInterval, int bottomInterval){
        mMiddleInterval = middleInterval;
        mBottomInterval = bottomInterval;
    }

    @Keep
    public GrideSpreadInsideDecoration(int interval){
        mMiddleInterval = interval;
        mBottomInterval = interval;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int totalWidth = parent.getMeasuredWidth() - parent.getPaddingStart() - parent.getPaddingEnd();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager)layoutManager).getSpanCount();
            //横向 扣除 中间的间隔之后每个item最多能分配多少空间
            float itemWidthAfterInterval = (totalWidth - mMiddleInterval * (spanCount - 1)) * 1F / spanCount;
            //默认GridLayoutManager为每个item最多能分配多少空间(outRect就是在这个空间里面设置内边距的且设置的最终效果不超过这个空间)
            float itemWidthStanded = totalWidth * 1F / spanCount;

            //每个位置所对应的横向第几个位置
            int index = position % spanCount;

            if (index == 0) {
                //横向第一个item
                mItemLeftAbscissa = 0;
                mItemRightAbscissa = itemWidthAfterInterval;
            }
            float standLeft = itemWidthStanded * index;
            float standRight = itemWidthStanded * (index + 1);
            outRect.left = (int)(mItemLeftAbscissa - standLeft);
            outRect.right = (int)(standRight - mItemRightAbscissa);
            outRect.bottom = mBottomInterval;
            //计算好下一个item左边所在的位置
            mItemLeftAbscissa += itemWidthAfterInterval + mMiddleInterval;
            mItemRightAbscissa += itemWidthAfterInterval + mMiddleInterval;
        }

    }
}
