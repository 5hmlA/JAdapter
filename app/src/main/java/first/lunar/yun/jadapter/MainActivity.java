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
import first.lunar.yun.adapter.JVBrecvDiffAdapter;
import first.lunar.yun.adapter.LApp;
import first.lunar.yun.adapter.LoadMoreWrapperAdapter;
import first.lunar.yun.adapter.face.JOnClickListener;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.helper.LLog;
import first.lunar.yun.adapter.holder.JViewHolder;
import first.lunar.yun.adapter.loadmore.LoadMoreChecker;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnViewClickListener<DataTest>, SwipeRefreshLayout.OnRefreshListener, LoadMoreChecker.LoadMoreCallBack {

  private LoadMoreWrapperAdapter mAdapter;
  private RecyclerView mRecyclerView;
  private SwipeRefreshLayout mRefreshLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    LApp.setDebug(true);
    List<DataTest> dataTests = new ArrayList<>();

    mRecyclerView = findViewById(R.id.rcv);
    mRefreshLayout = findViewById(R.id.refresh);
    mRefreshLayout.setOnRefreshListener(this);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mAdapter = new LoadMoreWrapperAdapter(new JVBrecvDiffAdapter(this));
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.setLoadMoreCallBack(this);

    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        List<DataTest> dataTests = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
          dataTests.add(new DataTest());
        }
        mAdapter.refreshData(dataTests);
      }
    }, 100);
  }

  @Override
  public void onItemClicked(View view, DataTest itemData) {
    Toast.makeText(view.getContext(), itemData.text + "", Toast.LENGTH_SHORT).show();
    mAdapter.noMoreLoad("fff");
  }


  @Override
  public void onRefresh() {
    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        List<DataTest> dataTests = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
          dataTests.add(new DataTest());
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
          List<DataTest> dataTests = new ArrayList<>();
          for (int i = 0; i < 20; i++) {
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
            holder.getAdatper().removeItem(holder.getAdapterPosition());
//            ((LoadMoreWrapperAdapter) holder.getAdatper()).loadMoreFinish("9090");
          }
        }, R.id.iv);
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    LLog.llog("onViewDetachedFromWindow - "+getPosition() +" - " + holder);
  }
}