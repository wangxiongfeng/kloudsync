package com.kloudsync.techexcel.personal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;

public class AboutActivity2 extends AppCompatActivity {

    private ImageView img_notice;
    private RelativeLayout rl_ps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        img_notice = (ImageView) findViewById(R.id.img_notice);
        rl_ps = (RelativeLayout) findViewById(R.id.rl_ps);
        img_notice.setOnClickListener(new MyOnClick());
        rl_ps.setOnClickListener(new MyOnClick());
    }

    protected class MyOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_notice:
                    finish();
                    break;
                case R.id.rl_ps:
                    OpenWeb();
                    break;
                default:
                    break;
            }
        }
    }

    private void OpenWeb() {
        Uri uri = Uri.parse("http://http;//www.peertime.com/privacy-statement");
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}
