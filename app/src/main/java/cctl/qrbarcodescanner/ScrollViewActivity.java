package cctl.qrbarcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ScrollViewActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("cctl.qrbarcodescanner");
        setContentView(R.layout.activity_scroll);

        TextView mTitleWindow = (TextView) findViewById(R.id.titleWindow);
        TextView mMessageWindow = (TextView) findViewById(R.id.messageWindow);

        mTitleWindow.setText("QR Scan Result:");


        mMessageWindow.setText(message);
    }
}
