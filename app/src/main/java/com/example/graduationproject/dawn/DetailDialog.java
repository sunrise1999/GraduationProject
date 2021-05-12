package com.example.graduationproject.dawn;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.graduationproject.R;

public class DetailDialog extends Dialog implements View.OnClickListener {
    private TextView tvMsg;
    private TextView btnOk;
    private String msg;

    public DetailDialog(Activity a, String message) {
        super(a);
        msg = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_detail);
        btnOk = (TextView) findViewById(R.id.btn_ok);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        btnOk.setOnClickListener(this);
        tvMsg.setText(msg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                dismiss();
                break;
            default:
                break;
        }
    }
}
