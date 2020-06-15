package first.lunar.yun.adapter.face;

import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.Keep;

/**
 * @another 江祖赟
 * @date 2017/10/18 0018.
 */
@Keep
public class JSimpleOnGestureListener implements GestureDetector.OnGestureListener {
    @Override
    public boolean onDown(MotionEvent e){
        //消费了down事件才会收到后续事件
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e){

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e){
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e){

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        return false;
    }
}
