package com.example.administrator.volleydongnao14;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.volleydongnao14.http.download.FileDownManager;

public class MainActivity extends com.lythonliu.LinkAppCompatActivity {

    @Override
    public String getAppName(){
        return BuildConfig.APP_NAME;
    }

    public  static  final String url="http://v.juhe.cn/toutiao/index?type=top&key=29da5e8be9ba88b932394b7261092f71";
    private static final String TAG = "dongnao";
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= (TextView) findViewById(R.id.content);

    }
    public  void login(View view)
    {
        FileDownManager fileDownManager=new FileDownManager();
        fileDownManager.download("http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk");
    }
}
