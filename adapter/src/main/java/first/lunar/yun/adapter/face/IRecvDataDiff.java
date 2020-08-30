package first.lunar.yun.adapter.face;

import androidx.annotation.Keep;

/**
 * @another 江祖赟
 * @date 2017/7/5.
 */
@Keep
public interface IRecvDataDiff extends IRecvData {

    int compare(IRecvDataDiff newData);
    /**
     * 增删 判断的主要依据
     * <p>检查id之类</p>
     * @param newData
     * @return
     */
    public boolean areItemsTheSame(IRecvDataDiff newData);

    /**
     * areItemsTheSame为true才判断 areContentsTheSame
     * @param newData
     * @return
     */
    public boolean areContentsTheSame(IRecvDataDiff newData);

    public Object getChangePayload(IRecvDataDiff oldData);

}
