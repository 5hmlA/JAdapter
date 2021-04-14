package first.lunar.yun.adapter.loadmore;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.GridLayoutManager;
import first.lunar.yun.adapter.face.IDataProvider;
import first.lunar.yun.adapter.vb.FullSpan;
import first.lunar.yun.adapter.vb.JLoadMoreVb;

/**
 * @author yun.
 * @date 2021/4/6 0006
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public final class LoadMoreConfig {

  private JLoadMoreVb loadMoreVb = new JLoadMoreVb();
  private boolean enableLoadMore = true;
  private GridLayoutManager.SpanSizeLookup spanSizeLookup;
  private CharSequence loadingTips;
  private Style style;

  @Keep
  private LoadMoreConfig(JLoadMoreVb loadMoreVb, Style style, CharSequence loadingTips, boolean enableLoadMore) {
    this.loadMoreVb = loadMoreVb;
    this.style = style;
    this.loadingTips = loadingTips;
    this.enableLoadMore = enableLoadMore;
  }

  public JLoadMoreVb getLoadMoreVb() {
    return loadMoreVb;
  }

  @Keep
  public Style getStyle() {
    return style;
  }

  public CharSequence getLoadingTips() {
    return loadingTips;
  }

  @Keep
  public boolean isEnableLoadMore() {
    return enableLoadMore;
  }

  public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
    return spanSizeLookup;
  }

  @Keep
  public static class Builder {
    private JLoadMoreVb loadMoreVb = new JLoadMoreVb();
    private LoadMoreConfig.Style style = Style.FIX;
    private CharSequence loadingTips;
    private boolean enableLoadMore = true;

    public Builder setLoadMoreVb(JLoadMoreVb loadMoreVb) {
      this.loadMoreVb = loadMoreVb;
      return this;
    }

    public Builder setStyle(LoadMoreConfig.Style style) {
      this.style = style;
      return this;
    }

    public Builder setLoadingTips(CharSequence loadingTips) {
      this.loadingTips = loadingTips;
      return this;
    }

    public Builder setEnableLoadMore(boolean enableLoadMore) {
      this.enableLoadMore = enableLoadMore;
      return this;
    }

    public LoadMoreConfig build() {
      return new LoadMoreConfig(loadMoreVb, style, loadingTips, enableLoadMore);
    }

  }

  @Keep
  public static enum Style{
    FIX("固定"),GONE("可移除");
    private String desc;

    Style(String desc) {
      this.desc = desc;
    }
  }

  @Keep
  public static class LoadMoreSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    GridLayoutManager mGridLayoutManager;
    IDataProvider mIDataProvider;

    public LoadMoreSpanSizeLookup(GridLayoutManager gridLayoutManager, IDataProvider JVBrecvAdapter) {
      mGridLayoutManager = gridLayoutManager;
      mIDataProvider = JVBrecvAdapter;
    }

    @Override
    public int getSpanSize(int position) {
      if (position == mIDataProvider.getDataSize()) {
        return mGridLayoutManager.getSpanCount();
      }
      return mIDataProvider.getItemData(position) instanceof FullSpan ? mGridLayoutManager.getSpanCount() : 1;
    }
  }
}
