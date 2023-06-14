package sparkj.adapter;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.ListUpdateCallback;
import sparkj.adapter.diff.JitemDiffCallback;
import sparkj.adapter.face.OnViewClickListener;
import sparkj.adapter.helper.JAsyncListDiffer;
import sparkj.adapter.vb.JViewBean;

import java.util.List;

/**
 * @author yun.
 * @date 2020/8/29 0029
 * @des [一句话描述]
 * @since [https://github.com/5hmlA]
 * <p><a href="https://github.com/5hmlA">github</a>
 */
public class JVBrecvDiffAdapter<D extends JViewBean> extends JVBrecvAdapter<D>{

  private final JAsyncListDiffer<D> mDiffer;

  @Keep
  public JVBrecvDiffAdapter() {
    this(null);
  }

  @Keep
  public JVBrecvDiffAdapter(OnViewClickListener<D> onViewClickListener) {
    super(onViewClickListener);
    mDiffer = new JAsyncListDiffer<>(new AdapterListUpdateCallback(this),
        new AsyncDifferConfig.Builder<>(new JitemDiffCallback<D>()).build());
    JAsyncListDiffer.ListListener<D> listener = new JAsyncListDiffer.ListListener<D>() {
      @Override
      public void onCurrentListChanged(
          @NonNull List<D> previousList, @NonNull List<D> currentList) {
        JVBrecvDiffAdapter.this.onCurrentListChanged(previousList, currentList);
      }
    };
    mDiffer.addListListener(listener);
  }

  /**
   * Submits a new list to be diffed, and displayed.
   * <p>
   * If a list is already being displayed, a diff will be computed on a background thread, which
   * will dispatch Adapter.notifyItem events on the main thread.
   *
   * @param list The new list to be displayed.
   */
  @Keep
  public void submitList(@Nullable List<D> list) {
    mDiffer.submitList(list);
  }

  /**
   * Set the new list to be displayed.
   * <p>
   * If a List is already being displayed, a diff will be computed on a background thread, which
   * will dispatch Adapter.notifyItem events on the main thread.
   * <p>
   * The commit callback can be used to know when the List is committed, but note that it
   * may not be executed. If List B is submitted immediately after List A, and is
   * committed directly, the callback associated with List A will not be run.
   *
   * @param list The new list to be displayed.
   * @param commitCallback Optional runnable that is executed when the List is committed, if
   *                       it is committed.
   */
  @Keep
  public void submitList(@Nullable List<D> list, @Nullable final Runnable commitCallback) {
    mDiffer.submitList(list, commitCallback);
  }

  /**
   * Get the current List - any diffing to present this list has already been computed and
   * dispatched via the ListUpdateCallback.
   * <p>
   * If a <code>null</code> List, or no List has been submitted, an empty list will be returned.
   * <p>
   * The returned list may not be mutated - mutations to content must be done through
   * {@link #submitList(List)}.
   *
   * @return The list currently being displayed.
   *
   * @see #onCurrentListChanged(List, List)
   */
  @NonNull
  @Override
  public List<D> getCurrentList() {
    return mDiffer.getCurrentList();
  }

  /**
   * Called when the current List is updated.
   * <p>
   * If a <code>null</code> List is passed to {@link #submitList(List)}, or no List has been
   * submitted, the current List is represented as an empty List.
   *
   * @param previousList List that was displayed previously.
   * @param currentList new List being displayed, will be empty if {@code null} was passed to
   *          {@link #submitList(List)}.
   *
   * @see #getCurrentList()
   */
  public void onCurrentListChanged(@NonNull List<D> previousList, @NonNull List<D> currentList) {
    mDataList.clear();
    mDataList.addAll(currentList);
  }

  @Keep
  public void addMoreList(@NonNull List<D> data) {
    addData(data);
  }

  @Override
  public void remove(int position) {
    mDiffer.remove(position);
  }

  @Override
  public void addData(int position, List<D> datas) {
    mDiffer.addAll(position, datas);
  }

  protected void setUpdateCallback(ListUpdateCallback updateCallback) {
    mDiffer.setUpdateCallback(updateCallback);
  }
}
