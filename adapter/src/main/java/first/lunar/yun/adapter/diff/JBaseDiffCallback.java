package first.lunar.yun.adapter.diff;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yun.
 * @date 2017/9/21
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public abstract class JBaseDiffCallback<D> extends DiffUtil.Callback {
    private List<D> oldList = new ArrayList<>();
    private List<D> newList = new ArrayList<>();

    public JBaseDiffCallback(List<D> oldList, List<D> newList){
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize(){
        return oldList.size();
    }

    @Override
    public int getNewListSize(){
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition){
        return areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    protected abstract boolean areItemsTheSame(D oldData, D newData);

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition){
        return areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    protected abstract boolean areContentsTheSame(D oldData, D newData);

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition){
        return getChangePayload(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    protected abstract Object getChangePayload(D oldData, D newData);

}
