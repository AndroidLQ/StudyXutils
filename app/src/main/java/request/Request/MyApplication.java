package request.Request;

import android.app.Application;

import org.xutils.x;

/**
 * Created by a on 2016/12/21.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
