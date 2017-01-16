package com.hsg.roundindicator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private RoundIndicatorView mRIV;
    private EditText mET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRIV = ((RoundIndicatorView) findViewById(R.id.riv));
        mET = ((EditText) findViewById(R.id.et));
    }

    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.btncccc:
                String value = mET.getText().toString();
                int a  = 0;
                if (value.length()>0){
                    a = Integer.parseInt(value);
                }
                mRIV.setCurrentNum(a);
                mRIV.setCurrentNumAnim(a);
                break;
            default:
                break;
        }
    }
}
