package callannna.bluelibrary.dao;

/**
 * Description
 * Created by chenqiao on 2016/6/28.
 */
public interface ClientAction<T> {

    void connect();

    void doAction(Object param1, Object param2, Object param3);

    void disconnect();

    void notifyResult(T result);

    void setResultListener(ResultListener listener);

    interface ResultListener {
        void onResultReceived(Object result);
    }
}
