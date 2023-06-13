package sparkj.adapter.holder;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @another 江祖赟
 * @date 2017/11/17 0017.
 */
public abstract class BaseLoadMoreBinder<D> extends JRecvBaseBinder<D,RecyclerView.ViewHolder> {
    public static class LoadMoreState{
        public String state;
        public CharSequence tips;

        public LoadMoreState(){
            this.state = FOOT_STATE_LOAD_NOMORE;
        }

        public LoadMoreState(String state){
            this.state = state;
        }

        public LoadMoreState(String state, String tips){
            this.state = state;
            this.tips = tips;
        }
    }

    public static final String FOOT_STATE_LOAD_ERROR = "up2load_error";
    public static final String FOOT_STATE_LOAD_NOMORE = "up2load_nomore";
    public static final String FOOT_STATE_LOAD_FINISH = "up2load_finish";
    public static final String FOOT_STATE_LOAD_CUSTOM_TIP = "up2load_custom_tip";

    /**
     * 重新设置holder到loadmore界面和状态
     * @param tips
     */
    public abstract void onLoadMoreState(CharSequence tips);

    /**
     * 重新设置holder到loaderror界面和状态
     * @param tips
     */
    public abstract void onLoadErrorState(CharSequence tips);

    public abstract void onLoadCustomState(CharSequence msg);

    public abstract void onNomoreLoadTips(CharSequence msg);

    public abstract void bindNomoreLoadTipsIfneed(CharSequence msg);
}
