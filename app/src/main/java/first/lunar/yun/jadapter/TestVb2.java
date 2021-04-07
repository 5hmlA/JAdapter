package first.lunar.yun.jadapter;

import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import first.lunar.yun.adapter.face.JOnClickListener;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;

/**
 * @author yun.
 * @date 2020/8/23 0023
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class TestVb2 extends JViewBean {

  private String text = "TestVb2";

  @Override
  public int bindLayout() {
    return R.layout.item_test_vb;
  }

  @Override
  public void onBindViewHolder(final JViewHolder holder, final int position, @Nullable List<Object> payloads, OnViewClickListener viewClickListener) {
    holder.setText(R.id.tv, position + "    " + text)
        .setOnClickListener(new JOnClickListener() {
          @Override
          public void doClick(View v) {
            Toast.makeText(v.getContext(), holder.getLayoutPosition() + "", Toast.LENGTH_SHORT).show();
          }
        }, R.id.iv);
  }
}
