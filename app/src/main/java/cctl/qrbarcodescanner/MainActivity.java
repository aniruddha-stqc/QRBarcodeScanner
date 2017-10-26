package cctl.qrbarcodescanner;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

/*
Following class MainActivity launches the app with a welcome screen. The scanning module is launched
once any part of the screen is touched. This feature has been included to minimize battery consumption
due to the camera being on during scanning activity.
 */
public class MainActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        RelativeLayout r_layout = (RelativeLayout) findViewById(R.id.mainlayout);
        r_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ScanActivity.class));
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_setting){
            Toast.makeText(MainActivity.this, "Clicked on Settings", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
