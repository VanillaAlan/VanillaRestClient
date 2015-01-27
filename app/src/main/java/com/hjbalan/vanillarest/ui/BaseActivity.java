package com.hjbalan.vanillarest.ui;

import com.hjbalan.vanillarest.ApplicationController;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by alan on 15/1/26.
 */
public class BaseActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ApplicationController.sIsInBackground) {
            Toast.makeText(this, "i'm back !!!!", Toast.LENGTH_SHORT).show();
            ApplicationController.sIsInBackground = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                
                break;
            
            default:
                
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
