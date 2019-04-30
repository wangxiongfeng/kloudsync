package com.kloudsync.techexcel.personal;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SearchSelectAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.ui.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class LanguageActivity extends SwipeBackActivity {

	private TextView tv_name;
	private TextView tv_back;
	private ListView lst_language;	

//	private Class ActivityClass;
	
	private SearchSelectAdapter mAdapter;
	private ArrayList<String> mList = new ArrayList<String>();

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	private int lan_select = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language);

//		ActivityClass = ActivitySlideMenu.instance.getClass();
		initView();
	}

	private void initView() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_back = (TextView) findViewById(R.id.tv_back);
		lst_language = (ListView) findViewById(R.id.lst_language);
		
		tv_name.setText(getResources().getString(R.string.language2));
		
		sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
				MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		getLanguage();

		tv_back.setOnClickListener(new myOnClick());
	}



	private void getLanguage() {
		switch (AppConfig.LANGUAGEID) {
		case 1:
			lan_select = 0;
			break;
		case 2:
			lan_select = 1;
			break;	
		default:
			break;
		}
		mList.add(getResources().getString(R.string.English));
		mList.add(getResources().getString(R.string.Chinese));
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final float density = dm.density;
		
		LayoutParams params = (LayoutParams) lst_language.getLayoutParams();
		params.height = (int) (mList.size() * 51 * density);
		
		mAdapter = new SearchSelectAdapter(getApplicationContext(), mList, lan_select);
		lst_language.setAdapter(mAdapter);
		lst_language.setOnItemClickListener(new MyOnitem());
		
	}
	
	private void RefreshLanguage(){
		tv_name.setText(getResources().getString(R.string.language));
	}
	
	private class MyOnitem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				updateLange(Locale.ENGLISH, 1);
				break;
			case 1:
				updateLange(Locale.SIMPLIFIED_CHINESE, 2);
				break;	
			default:
				break;
			}
			mAdapter.getPosition(position);
			
		}
		
	}
	
	private void updateLange(Locale locale, int language) {
		editor.putInt("language", language);
		editor.commit();
		if(AppConfig.LANGUAGEID != language){
			AppConfig.LANGUAGEID = language;
			Resources res = getResources();
			Configuration config = res.getConfiguration();
			config.locale = locale;
			DisplayMetrics dm = res.getDisplayMetrics();
			res.updateConfiguration(config, dm);
			MainActivity.RESUME = true;
		}
		RefreshLanguage();
		FinishActivity();

	}

	protected class myOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				FinishActivity();
				break;

			default:
				break;
			}
			
		}
		
	}

	private void FinishActivity() {
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			FinishActivity();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}
}
