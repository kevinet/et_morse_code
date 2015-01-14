package cn.wecook.et.morsecode;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private EditText mLongTime;
    private EditText mShortTime;
    private EditText mSplitTime;
    private EditText mWaitTime;
    private EditText mText;
    private TextView mCode;
    private TextView mPlainText;
    private MorseCodeUtils.MorseCodeGenerator mGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (EditText) findViewById(R.id.app_debug_text);
        mLongTime = (EditText) findViewById(R.id.app_debug_long_time);
        mShortTime = (EditText) findViewById(R.id.app_debug_short_time);
        mSplitTime = (EditText) findViewById(R.id.app_debug_split_time);
        mWaitTime = (EditText) findViewById(R.id.app_debug_wait_time);
        mCode = (TextView) findViewById(R.id.app_debug_code);
        mPlainText = (TextView) findViewById(R.id.app_debug_plaintext);

        mLongTime.setText(MorseCodeUtils.LONG_TIME + "");
        mShortTime.setText(MorseCodeUtils.SHORT_TIME + "");
        mSplitTime.setText(MorseCodeUtils.SPLIT_TIME + "");
        mWaitTime.setText(MorseCodeUtils.WAIT_TIME + "");

        findViewById(R.id.app_debug_action_vibrate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MorseCodeUtils.setLongTime(Long.parseLong(mLongTime.getText().toString()));
                MorseCodeUtils.setShortTime(Long.parseLong(mShortTime.getText().toString()));
                MorseCodeUtils.setSplitTime(Long.parseLong(mSplitTime.getText().toString()));
                MorseCodeUtils.setWaitTime(Long.parseLong(mWaitTime.getText().toString()));
                MorseCodeUtils.vibrateMorseCode(MainActivity.this, mText.getText().toString());
                mCode.setText(MorseCodeUtils.translateToCode(mText.getText().toString()));
            }
        });

        findViewById(R.id.app_debug_action_morse).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MorseCodeUtils.setLongTime(Long.parseLong(mLongTime.getText().toString()));
                MorseCodeUtils.setShortTime(Long.parseLong(mShortTime.getText().toString()));
                MorseCodeUtils.setSplitTime(Long.parseLong(mSplitTime.getText().toString()));
                MorseCodeUtils.setWaitTime(Long.parseLong(mWaitTime.getText().toString()));
                if (mGenerator == null) {
                    mGenerator = new MorseCodeUtils.MorseCodeGenerator();
                    mGenerator.start();
                }

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mGenerator.clickDown(MainActivity.this);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mGenerator.clickUp(MainActivity.this);
                        break;
                }

                mPlainText.setText(mGenerator.getCode());
                return false;
            }
        });
        mPlainText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPlainText.setText("");
                mGenerator.start();
                return true;
            }
        });
    }

}
