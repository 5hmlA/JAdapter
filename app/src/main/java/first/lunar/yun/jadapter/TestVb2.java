package first.lunar.yun.jadapter;

import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import sparkj.adapter.face.JOnClickListener;
import sparkj.adapter.face.OnViewClickListener;
import sparkj.adapter.holder.JViewHolder;
import sparkj.adapter.vb.JViewBean;
import sparkj.jadapter.R;

import java.util.List;

/**
 * @author yun.
 * @date 2020/8/23 0023
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class TestVb2 extends JViewBean {
  private String text = "TestVb2";

  @Override
  public int bindLayout() {
    return R.layout.item_test_vb;
  }

  @Override
  public void onBindViewHolder(JViewHolder holder, final int position, @Nullable List<Object> payloads, OnViewClickListener viewClickListener) {
    holder.setText(R.id.tv, position + "    " + text)
        .setOnClickListener(new JOnClickListener() {
          @Override
          public void throttleFirstclick(View v) {
            Toast.makeText(v.getContext(), getPosition() + "", Toast.LENGTH_SHORT).show();
          }
        }, R.id.iv);
  }
}
