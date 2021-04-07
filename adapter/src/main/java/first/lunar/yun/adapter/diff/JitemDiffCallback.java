package first.lunar.yun.adapter.diff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import first.lunar.yun.adapter.face.IRecvDataDiff;

/**
 * @author yun.
 * @date 2017/9/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JitemDiffCallback<D extends IRecvDataDiff> extends DiffUtil.ItemCallback<D> {

    @Override
    public boolean areItemsTheSame(@NonNull D oldItem, @NonNull D newItem) {
        return oldItem.areItemsTheSame(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull D oldItem, @NonNull D newItem) {
        return oldItem.areContentsTheSame(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(@NonNull D oldItem, @NonNull D newItem) {
        return newItem.getChangePayload(oldItem);
    }
}
