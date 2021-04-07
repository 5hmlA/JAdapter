package first.lunar.yun.adapter.loadmore;

import first.lunar.yun.adapter.vb.JLoadMoreVb;
import first.lunar.yun.adapter.vb.JViewBean;

/**
 * @author yun.
 * @date 2021/4/6 0006
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class LoadMoreConfig {

  private JViewBean loadMoreVb = new JLoadMoreVb();
  private Style style;
  private CharSequence loadingTips;

  public JViewBean getLoadMoreVb() {
    return loadMoreVb;
  }

  public Style getStyle() {
    return style;
  }

  public CharSequence getLoadingTips() {
    return loadingTips;
  }

  public static enum Style{
    FIX("固定"),GONE("可移除");
    private String desc;

    Style(String desc) {
      this.desc = desc;
    }
  }

  public static enum HolderState{
    LOADFINISH("关闭加载"), LOADING("加载中"), LOADSUCCED("加载成功"), LOADERETRY("重试");
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
