package request.Request;

import java.util.Map;

/**
 * Created by a on 2016/12/21.
 */

public interface IRequestManager {
    void get(String url ,Map<String, String> maps,IRequestCallback iRequestCallback);

    void post(String url ,Map<String, String> maps,IRequestCallback iRequestCallback);

    void put(String url ,Map<String, String> maps,IRequestCallback iRequestCallback);

    void delete(String url ,Map<String, String> maps,IRequestCallback iRequestCallback);
}
