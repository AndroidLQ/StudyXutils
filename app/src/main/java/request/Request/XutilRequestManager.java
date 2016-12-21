package request.Request;

import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by a on 2016/12/21.
 * 使用xutls3一定要在application初始化 x.Ext.init(this);
 *
 // 加到url里的参数, http://xxxx/s?wd=xUtils
 // params.addQueryStringParameter("wd", "xUtils");

 // 添加到请求body体的参数, 只有POST, PUT, PATCH, DELETE请求支持.
 // params.addBodyParameter("wd", "xUtils");

 // 使用multipart表单上传文件
 //        params.setMultipart(true);
 //        params.addBodyParameter(
 //                "file",
 //                new File("/sdcard/test.jpg"),
 //                null); // 如果文件没有扩展名, 最好设置contentType参数.
 //        try {
 //            params.addBodyParameter(
 //                    "file2",
 //                    new FileInputStream(new File("/sdcard/test2.jpg")),
 //                    "image/jpeg",
 //                    // 测试中文文件名
 //                    "你+& \" 好.jpg"); // InputStream参数获取不到文件名, 最好设置, 除非服务端不关心这个参数.
 //        } catch (FileNotFoundException ex) {
 //            ex.printStackTrace();
 //        }
 *
 */

public class XutilRequestManager implements IRequestManager {
    private static final int GET_CONNECT_TIME_OUT = 5 * 1000;//5秒连接超时
    private static final int MAX_RETRY_COUNT = 3;//最大请求次数----3次
    //请求参数
    private RequestParams params;
    private Callback.Cancelable cancelable;

    private static volatile XutilRequestManager xutilRequestManager;

    private XutilRequestManager() {};

    public static XutilRequestManager getInstance() {
        if (xutilRequestManager == null) {
            synchronized (XutilRequestManager.class) {
                if (xutilRequestManager == null) {
                    xutilRequestManager = new XutilRequestManager();
                }
            }
        }
        return xutilRequestManager;
    }

    /**
     * 添加头部信息
     *
     * @param key   key
     * @param value value
     */
    private void addHeader(String key, String value) {
        params.addHeader(key, value);
    }

    /**
     *
     * @param url
     * @param maps
     */

    private void setRequestParams(String url, Map<String, String> maps,boolean paramsType) {
        params = new RequestParams(url);
        params.setConnectTimeout(GET_CONNECT_TIME_OUT);
        params.setMaxRetryCount(MAX_RETRY_COUNT);
        if (maps != null) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                if(paramsType){
                    params.addQueryStringParameter(entry.getKey(), entry.getValue());
                }else {
                    params.addBodyParameter(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Override
    public void get(String url, Map<String, String> maps, final IRequestCallback iRequestCallback) {
        setRequestParams(url, maps,true);
        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                iRequestCallback.sucess(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                iRequestCallback.fialure(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                iRequestCallback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                iRequestCallback.finish();
            }
        });
    }

    @Override
    public void post(String url, Map<String, String> maps, final IRequestCallback iRequestCallback) {
        //设置参数
        setRequestParams(url, maps,false);
        if (params == null) {
            return ;
        }
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                iRequestCallback.sucess(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                iRequestCallback.fialure(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                iRequestCallback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                iRequestCallback.finish();
            }
        });

    }

    @Override
    public void put(String url, Map<String, String> maps, IRequestCallback iRequestCallback) {

    }

    @Override
    public void delete(String url, Map<String, String> maps, IRequestCallback iRequestCallback) {

    }


    //上传多文件
    private void uploadFile(final IRequestCallback iRequestCallback){
        RequestParams params = new RequestParams("http://192.168.0.13:8080/upload");
        // 加到url里的参数, http://xxxx/s?wd=xUtils
        params.addQueryStringParameter("wd", "xUtils");
        // 添加到请求body体的参数, 只有POST, PUT, PATCH, DELETE请求支持.
        // params.addBodyParameter("wd", "xUtils");

        // 使用multipart表单上传文件
        params.setMultipart(true);
        params.addBodyParameter(
                "file",
                new File("/sdcard/test.jpg"),
                null); // 如果文件没有扩展名, 最好设置contentType参数.
        try {
            params.addBodyParameter(
                    "file2",
                    new FileInputStream(new File("/sdcard/test2.jpg")),
                    "image/jpeg",
                    // 测试中文文件名
                    "你+& \" 好.jpg"); // InputStream参数获取不到文件名, 最好设置, 除非服务端不关心这个参数.
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                iRequestCallback.sucess(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                iRequestCallback.fialure(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                iRequestCallback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                iRequestCallback.finish();
            }
        });
    }


    //缓存示例
    private void onCache(final IRequestCallback iRequestCallback){
        RequestParams params = new RequestParams("http://192.168.0.13:8080/upload");
        // 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)
        params.setCacheMaxAge(1000 * 60);
        Callback.Cancelable cancelable
                // 使用CacheCallback, xUtils将为该请求缓存数据.
                = x.http().get(params, new Callback.CacheCallback<String>() {

            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                // 得到缓存数据, 缓存过期后不会进入这个方法.
                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
                //
                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
                //
                // * 如果信任该缓存返回 true, 将不再请求网络;
                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
                //
                this.result = result;
                iRequestCallback.sucess(result);
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
                iRequestCallback.sucess(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();

                } else { // 其他错误

                }

                iRequestCallback.fialure(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                iRequestCallback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                }
                iRequestCallback.finish();
            }
    }


}
