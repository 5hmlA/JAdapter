package first.lunar.yun.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.AsyncDifferConfig;
import first.lunar.yun.adapter.diff.JitemDiffCallback;
import first.lunar.yun.adapter.face.OnViewClickListener;
import first.lunar.yun.adapter.holder.JAsyncListDiffer;
import first.lunar.yun.adapter.vb.JViewBean;
import java.util.List;

/**
 * @author yun.
 * @date 2020/8/29 0029
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JVBrecvDiffAdapter<D extends JViewBean> extends JVBrecvAdapter<D>{

  final JAsyncListDiffer<D> mDiffer;
  private final JAsyncListDiffer.ListListener<D> mListener =
      new JAsyncListDiffer.ListListener<D>() {
        @Override
        public void onCurrentListChanged(
            @NonNull List<D> previousList, @NonNull List<D> currentList) {
          JVBrecvDiffAdapter.this.onCurrentListChanged(previousList, currentList);
        }
      };

  public JVBrecvDiffAdapter() {
    mDiffer = new JAsyncListDiffer<>(new AdapterListUpdateCallback(this),
        new AsyncDifferConfig.Builder<>(new JitemDiffCallback<D>()).build());
    mDiffer.addListListener(mListener);
  }

  @SuppressWarnings("unused")
  protected JVBrecvDiffAdapter(@NonNull AsyncDifferConfig<D> config) {
    mDiffer = new JAsyncListDiffer<>(new AdapterListUpdateCallback(this), config);
    mDiffer.addListListener(mListener);
  }

  public JVBrecvDiffAdapter(OnViewClickListener<D> onViewClickListener) {
    super(onViewClickListener);
    mDiffer = new JAsyncListDiffer<>(new AdapterListUpdateCallback(this),
        new AsyncDifferConfig.Builder<>(new JitemDiffCallback<D>()).build());
    mDiffer.addListListener(mListener);
  }

  /**
   * Submits a new list to be diffed, and displayed.
   * <p>
   * If a list is already being displayed, a diff will be computed on a background thread, which
   * will dispatch Adapter.notifyItem events on the main thread.
   *
   * @param list The new list to be displayed.
   */
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

  @Override
  public List<D> getDataList() {
    return getCurrentList();
  }

  @Override
  public void addMoreList(@NonNull List<D> data) {
    mDiffer.addAll(data, this);
  }

  @Override
  public void removeItem(int position) {
    mDiffer.remove(position, this);
  }

  @Override
  public void removeItem(D item) {
    int i = getDataList().indexOf(item);
    if (i > -1) {
      mDiffer.remove(i, this);
    }
  }

  @Override
  public void refreshAllData(@NonNull List<D> data) {
    submitList(data);
  }

  @Override
  public void addItem(D data, int position) {
    mDiffer.add(data, position, this);
  }
}
