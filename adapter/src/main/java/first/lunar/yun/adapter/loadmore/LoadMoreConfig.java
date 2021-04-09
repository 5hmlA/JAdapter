package first.lunar.yun.adapter.loadmore;

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
  private CharSequence loadingTips;
  private Style style;

  public LoadMoreConfig() {
  }

  private LoadMoreConfig(JLoadMoreVb loadMoreVb, Style style, CharSequence loadingTips, boolean enableLoadMore) {
    this.loadMoreVb = loadMoreVb;
    this.style = style;
    this.loadingTips = loadingTips;
    this.enableLoadMore = enableLoadMore;
  }

  public JLoadMoreVb getLoadMoreVb() {
    return loadMoreVb;
  }

  public Style getStyle() {
    return style;
  }

  public CharSequence getLoadingTips() {
    return loadingTips;
  }

  public boolean isEnableLoadMore() {
    return enableLoadMore;
  }

  public static class Builder {
    private JLoadMoreVb loadMoreVb = new JLoadMoreVb();
    private LoadMoreConfig.Style style;
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

  public static enum Style{
    FIX("固定"),GONE("可移除");
    private String desc;

    Style(String desc) {
      this.desc = desc;
    }
  }

  public static enum HolderState{
    LOADNOMORE("没有更多"), LOADING("加载中"), LOADERETRY("重试");
    private String desc;
    private CharSequence tips;

    HolderState(String desc) {
      this.desc = desc;
    }

    public CharSequence getTips() {
      return tips;
    }

    public void setTips(CharSequence tips) {
      this.tips = tips;
    }
  }

}
