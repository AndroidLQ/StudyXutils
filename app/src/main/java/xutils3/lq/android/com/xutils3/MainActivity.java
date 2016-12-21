package xutils3.lq.android.com.xutils3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import request.Request.IRequestCallback;
import request.Request.IRequestManager;
import request.Request.RequestFactory;

public class MainActivity extends AppCompatActivity {

    private Button btn_get;
    private Button btn_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_get = (Button) findViewById(R.id.btn_get);
        btn_post = (Button) findViewById(R.id.btn_post);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new HashMap<String,String>();
                //测试请求
                String url = "https://api.douban.com/v2/movie/top250";
                IRequestManager iRequestManager = RequestFactory.getRequestManager();
                iRequestManager.get(url,map,new IRequestCallback<String>(){

                    @Override
                    public void sucess(String s) {
                        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void fialure(Throwable throwable) {

                    }

                    @Override
                    public void onCancelled(Callback.CancelledException cex) {

                    }

                    @Override
                    public void finish() {

                    }
                });
            }
        });



    }
}
