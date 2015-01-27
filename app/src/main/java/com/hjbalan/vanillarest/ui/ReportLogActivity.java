package com.hjbalan.vanillarest.ui;

import com.hjbalan.vanillarest.CustomUncaughtExceptionHandler;
import com.hjbalan.vanillarest.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class ReportLogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_log);
        String log = getIntent().getStringExtra(CustomUncaughtExceptionHandler.EXTRA_LOG);
        if (!TextUtils.isEmpty(log)) {
            ((TextView) findViewById(R.id.tv_log)).setText(log);
        }
        String logPath = getIntent().getStringExtra(CustomUncaughtExceptionHandler.EXTRA_FILE_PATH);
        sendLog(logPath);
    }

    private void sendLog(String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alan@nutspace.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "error log");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
        // do this so some email clients don't complain about empty body.
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached.");
        startActivity(intent);
    }

}
