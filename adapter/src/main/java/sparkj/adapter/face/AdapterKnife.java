package sparkj.adapter.face;

import androidx.annotation.Keep;

import java.util.List;

/**
 * @author yun.
 * @date 2021/4/10 0010
 * @des [一句话描述]
 * @since [https://github.com/5hmlA]
 * <p><a href="https://github.com/5hmlA">github</a>
 */
@Keep
public interface AdapterKnife<D> {
  void remove(int position);

  void remove(D data);

  void addData(List<D> datas);

  void addData(int position, List<D> datas);
}
