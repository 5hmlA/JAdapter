package first.lunar.yun.jadapter;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import sparkj.adapter.JVBrecvAdapter;
import sparkj.adapter.LApp;
import sparkj.adapter.LoadMoreWrapperAdapter;
import sparkj.adapter.face.JOnClickListener;
import sparkj.adapter.face.OnMoreloadListener;
import sparkj.adapter.face.OnViewClickListener;
import sparkj.adapter.holder.JViewHolder;
import sparkj.adapter.vb.JViewBean;
import sparkj.jadapter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnViewClickListener<DataTest>, OnMoreloadListener, SwipeRefreshLayout.OnRefreshListener {

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
    mAdapter = new LoadMoreWrapperAdapter(new JVBrecvAdapter(dataTests, this), dataTests);
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.enAbleLoadMore(true);
    mAdapter.setOnMoreloadListener(this);

    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        List<DataTest> dataTests = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
          dataTests.add(new DataTest());
        }
        mAdapter.refreshAllData(dataTests);
      }
    }, 1000);
  }

  @Override
  public void onItemClicked(View view, DataTest itemData) {
    Toast.makeText(view.getContext(), itemData.text + "", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onup2LoadingMore() {
    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (new Random().nextBoolean()) {
          List<DataTest> dataTests = new ArrayList<>();
          for (int i = 0; i < 20; i++) {
            dataTests.add(new DataTest());
          }
          mAdapter.addMoreList(dataTests);
        } else {
          if (new Random().nextBoolean()) {
            mAdapter.loadError();
          } else {
            mAdapter.enAbleLoadMore(false, "啦啦啦");
          }
        }
      }
    }, 1000);
  }

  @Override
  public void retryUp2LoadingMore() {
    onup2LoadingMore();
  }

  @Override
  public void onRefresh() {
    mRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        List<DataTest> dataTests = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
          dataTests.add(new DataTest());
        }
        mAdapter.refreshAllData(dataTests);
        mRefreshLayout.setRefreshing(false);
      }
    }, 1000);
  }
}

class DataTest extends JViewBean {

  String text = "测试:" + String.valueOf(new Random().nextInt());

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

  @Override
  public void onViewDetachedFromWindow(@NonNull JViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    System.out.println("onViewDetachedFromWindow - "+getPosition() +" - " + holder);
  }
}