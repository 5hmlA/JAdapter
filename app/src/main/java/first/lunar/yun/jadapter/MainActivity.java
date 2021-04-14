package first.lunar.yun.jadapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import first.lunar.yun.LApp;
import first.lunar.yun.adapter.AbsLoadMoreWrapperAdapter;
import first.lunar.yun.adapter.LoadMoreDiffDampAdapter;
import first.lunar.yun.adapter.face.IRecvDataDiff;
import first.lunar.yun.adapter.face.JOnClickListener;
import first.lunar.yun.adapter.face.LoadMoreCallBack;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.LLog;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.loadmore.LoadMoreConfig;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnViewClickListener<JViewBean>, SwipeRefreshLayout.OnRefreshListener,
    LoadMoreCallBack {

  private AbsLoadMoreWrapperAdapter<JViewBean> mAdapter;
  private RecyclerView mRecyclerView;
  private SwipeRefreshLayout mRefreshLayout;
  List<JViewBean> dataTests = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    LApp.setDebug(true);

    mRecyclerView = findViewById(R.id.rcv);
    mRefreshLayout = findViewById(R.id.refresh);
    mRefreshLayout.setOnRefreshListener(this);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mAdapter = new LoadMoreDiffDampAdapter(this);
    mAdapter.setLoadMoreConfig(new LoadMoreConfig.Builder().setEnable(false).setStyle(LoadMoreConfig.Style.GONE).build());
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.setLoadMoreCallBack(this);
    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 13; i++) {
          dataTests.add(new DataTest("初始化数据 " + i));
        }
        mAdapter.refreshData(dataTests);
      }
    }, 100);
  }

  @Override
  public void onItemClicked(View view, JViewBean itemData) {
    Toast.makeText(view.getContext(), ((DataTest) itemData).text + "", Toast.LENGTH_SHORT).show();
    mAdapter.noMoreLoad("fff");
  }


  @Override
  public void onRefresh() {
    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        dataTests = new ArrayList(dataTests);
        if (dataTests.size() > 4) {
          Collections.swap(dataTests, 0, 3);
//        Collections.shuffle(dataTests);
        } else {
          for (int i = 0; i < 13; i++) {
            dataTests.add(new DataTest());
          }
        }
        mAdapter.refreshData(dataTests);
        mRefreshLayout.setRefreshing(false);
      }
    }, 1000);
  }

  @Override
  public void onLoadMore(boolean retry) {
    Log.d("DFJ", "onLoadMore ");
    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (new Random().nextBoolean()) {
          List<JViewBean> dataTests = new ArrayList<>();
          for (int i = 0; i < 13; i++) {
            dataTests.add(new DataTest());
          }
          LLog.llogi(" add more data ");
          mAdapter.loadMoreSucceed(dataTests);
        } else {
          if (new Random().nextBoolean()) {
            mAdapter.loadMoreError("加载失败啦");
            LLog.lloge("load_more load more error >>> ");
          } else {
            LLog.lloge("load_more load finish >>> ");
            mAdapter.noMoreLoad("啦啦啦");
          }
        }
      }
    }, 200);
  }
}

class DataTest extends JViewBean {

  String text = "测试:" + String.valueOf(new Random().nextInt());

  public DataTest(String text) {
    this.text = text;
  }

  public DataTest() {
  }

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
//            Toast.makeText(v.getContext(), getPosition() + "", Toast.LENGTH_SHORT).show();
            holder.getAdapterKnife().remove(holder.getAdapterPosition());
//            ((LoadMoreDiffAdapter) holder.getAdatper()).loadMoreFinish("9090");
          }
        }, R.id.iv);
  }

  @Override
  public boolean areItemsTheSame(IRecvDataDiff newData) {
    return ((DataTest) newData).text.equals(text);
  }

  @Override
  public boolean areContentsTheSame(IRecvDataDiff newData) {
    return ((DataTest) newData).text.equals(text);
  }

  @Override
  public Object getChangePayload(IRecvDataDiff oldData) {
    return text = ((DataTest) oldData).text;
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    LLog.llog("onViewDetachedFromWindow - "+getPosition() +" - " + holder);
  }
}