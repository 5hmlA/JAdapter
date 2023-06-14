package sparkj.adapter;

public class LConsistent {
    public static final String DEFAULTSTR = "--";
    public static final String DIFF_TYPE = "doundle_typle";
    public static final String DIFF_INDEX = "doundle_index";
    public static final String BUND_TAG = "bund_extra";
    public static final int VIEWTAG_ERROR_MSG = 0x12fcde1;
    public static final String SPLIT_DOS = ", ";
    public static final String CONTACT_DOS = ",";

    public static final int DEFAULTERROR = -12306;

    public static interface LoadMoreWrapper {
        int NEED_UP2LOAD_MORE = 1;
        int NON_UP2LOAD_MORE = 0;
    }

    public interface Common {
        String DIFF_TYPE = "doundle_typle";
        String DIFF_INDEX = "doundle_index";
        String BUND_TAG = "bund_extra";
        String INTENT_URL = "intent_url";
    }

    public interface ViewTag {
        int view_tag = 0x12345678;
        int view_tag2 = 0x1234567a;
        int view_tag3 = 0x1234567b;
        int view_tag4 = 0x1234567c;
        int view_tag5 = 0x1234567d;
        int value_tag = 0x1234567d;
        int view_click = 0x7654321d;
    }

    public interface TransitionName {
        String TRANS_AVATAR = "javatar";
        String TRANS_IMG = "jimageview";
        String TRANS_IMG2 = "jimageview2";
        String TRANS_TV = "jtextview";
        String TRANS_TV2 = "jtextview2";
        String TRANS_BTN = "jbuttom";
        String TRANS_BTN2 = "jbuttom2";
    }
}
