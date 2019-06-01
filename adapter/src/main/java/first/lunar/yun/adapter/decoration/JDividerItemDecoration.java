package first.lunar.yun.adapter.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import first.lunar.yun.adapter.helper.Damping;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;

/**
 * @author yun.
 * @date 2017/9/21
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class JDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int mDivider;
    private Onscroll mListener;

    private float mScrolledPx;
    private float mScall;

    public JDividerItemDecoration(int divider){
        mDivider = divider;
//        mListener = new Onscroll();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
//        parent.addOnScrollListener(mListener);
        if(parent.getChildLayoutPosition(view) != 0) {
            outRect.top = mDivider;
        }
    }


    class Onscroll extends RecyclerView.OnScrollListener {
        private int mScrollState;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState){
            mScrollState = newState;
            if(newState == SCROLL_STATE_DRAGGING) {
                System.out.println("lashen jianju");
            }else {
                System.out.println("还原");
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if(mScrollState == SCROLL_STATE_DRAGGING) {
                mScrolledPx += dy;
                mScall = Damping.calculateDamping(mScrolledPx);
                System.out.println(mScrolledPx+"------------------------"+mScall);
            }else {
                mScrolledPx = 0;
            }
        }
    }
}
