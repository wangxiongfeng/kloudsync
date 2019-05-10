package com.kloudsync.techexcel.docment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class EditTeamActivity extends AppCompatActivity {

    private ImageView back;
    private TextView createbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editteam);

        initView();
    }

    private void initView() {
        createbtn = (TextView) findViewById(R.id.createbtn);
        back = (ImageView) findViewById(R.id.back);

        createbtn.setOnClickListener(new MyOnClick());
        back.setOnClickListener(new MyOnClick());
    }

    protected class MyOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    finish();
                    break;
                case R.id.createbtn:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
