package first.lunar.yun.adapter.face;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import java.util.List;

/**
 * @author yun.
 * @date 2020/8/29 0029
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
@Keep
public interface IVBrecvAdapter<D> {

  public void addMoreList(@NonNull List<D> data);

  @Keep
  public void refreshAllData(@NonNull List<D> data);

  @Keep
  public void removeItem(int position);

  @Keep
  public void removeItem(D item);

  @Keep
  public void addItem(D data, int position);

  @Keep
  public void diffAll(List<D> newData);

  void diffAll(final List<D> newData, final boolean detectMoves);

  int getDataSize();
}
