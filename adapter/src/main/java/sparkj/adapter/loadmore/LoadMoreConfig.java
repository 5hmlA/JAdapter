package sparkj.adapter.loadmore;

import androidx.annotation.Keep;
import androidx.recyclerview.widget.GridLayoutManager;
import sparkj.adapter.AbsLoadMoreWrapperAdapter;
import sparkj.adapter.vb.FullSpan;
import sparkj.adapter.vb.JLoadMoreVb;
import sparkj.adapter.vb.JViewBean;

/**
 * @author yun.
 * @date 2021/4/6 0006
 * @des [一句话描述]
 * @since [https://github.com/5hmlA]
 * <p><a href="https://github.com/5hmlA">github</a>
 */
public final class LoadMoreConfig {

  private JLoadMoreVb loadMoreVb = new JLoadMoreVb();
  private boolean isEnable = true;
  private LoadMoreSpanSizeLookup spanSizeLookup = new LoadMoreSpanSizeLookup();
  private CharSequence loadingTips;
  private Style style;

  @Keep
  private LoadMoreConfig(JLoadMoreVb loadMoreVb, Style style, CharSequence loadingTips, boolean isEnable) {
    this.loadMoreVb = loadMoreVb;
    this.style = style;
    this.loadingTips = loadingTips;
    this.isEnable = isEnable;
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
  public boolean isEnable() {
    return isEnable;
  }

  public LoadMoreSpanSizeLookup getSpanSizeLookup() {
    return spanSizeLookup;
  }

  @Keep
  public static class Builder {
    private JLoadMoreVb loadMoreVb = new JLoadMoreVb();
    private LoadMoreConfig.Style style = Style.FIX;
    private CharSequence loadingTips;
    private boolean enable = true;

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

    public Builder setEnable(boolean enable) {
      this.enable = enable;
      return this;
    }

    public LoadMoreConfig build() {
      return new LoadMoreConfig(loadMoreVb, style, loadingTips, enable);
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
    int spanCount;
    private AbsLoadMoreWrapperAdapter adapter;

    @Keep
    public LoadMoreSpanSizeLookup() {
    }

    public LoadMoreSpanSizeLookup setSpanCount(int spanCount) {
      this.spanCount = spanCount;
      return this;
    }

    public LoadMoreSpanSizeLookup setAdapter(AbsLoadMoreWrapperAdapter adapter) {
      this.adapter = adapter;
      return this;
    }

    @Override
    public int getSpanSize(int position) {
      Object itemData = getItemData(position);
      if (itemData instanceof FullSpan) {
        return spanCount;
      }
      if (itemData instanceof JViewBean) {
        return ((JViewBean) itemData).getSpanSize(position);
      }
      return 1;
    }

    @Keep
    protected Object getItemData(int position) {
      return adapter.getItemData(position);
    }
  }
}
