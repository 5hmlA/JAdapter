package first.lunar.yun.adapter.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import first.lunar.yun.LApp;

import static first.lunar.yun.LApp.dp2px;

/**
 * @author yun.
 * @date 2021/4/16 0016
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class BottomMsgDecoration extends RecyclerView.ItemDecoration{

    int textSize = 40;
    int textColor = Color.RED;
    TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    String tips = "00";
    float drawTextX;
    int drawTextHeight;
    StaticLayout staticLayout;

    @Keep
    public BottomMsgDecoration() {
    }

    @Keep
    public BottomMsgDecoration(int textSize, int textColor, int tipsRes) {
        this.textSize = dp2px(textSize);
        this.textColor = textColor;
        this.tips = LApp.findString(tipsRes);
        paint.setTextSize(this.textSize);
        paint.setColor(this.textColor);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getLayoutManager() == null) {
            return;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager)parent.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }
        int measuredWidth = parent.getMeasuredWidth();
        if (measuredWidth == 0) {
            return;
        }
        if (drawTextX == 0) {
            int measureText = (int)Math.min(paint.measureText(tips), measuredWidth - dp2px(24)*2);
            staticLayout = StaticLayout.Builder.obtain(tips,0,tips.length(),paint,measureText)
//                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .build();
            float tipsWidth = staticLayout.getWidth();
            drawTextHeight = staticLayout.getHeight();
            parent.setPadding(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingRight(), dp2px(40) + drawTextHeight);
            drawTextX = (measuredWidth >> 1) - tipsWidth / 2;
        }
        int itemCount = adapter.getItemCount();

        if (itemCount - 1 == lastVisibleItemPosition && !parent.canScrollVertically(11) && !parent.canScrollVertically(-11)) {
//            c.drawText(tips, drawTextX, parent.getBottom() - dp2px(20), this.paint);
            c.save();
            c.translate(drawTextX,parent.getBottom() - dp2px(20) - drawTextHeight);
            staticLayout.draw(c);
            c.restore();
        } else if (itemCount - 1 == lastVisibleItemPosition) {
            View viewByPosition = layoutManager.findViewByPosition(lastVisibleItemPosition);
            if (viewByPosition != null) {
                c.save();
                c.translate(drawTextX,viewByPosition.getBottom() + dp2px(20));
                staticLayout.draw(c);
                c.restore();
//                c.drawText(tips, drawTextX, viewByPosition.getBottom() + dp2px(20) + drawTextHeight, this.paint);
            }
        }

    }
}
