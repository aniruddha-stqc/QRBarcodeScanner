package cctl.qrbarcodescanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
        //Adds a Application tool bar with will contain the settings button at the left
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //Define the main activity layout which will navigate to the scanner module when touched
        RelativeLayout r_layout = (RelativeLayout) findViewById(R.id.mainlayout);
        r_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ScanActivity.class));
            }

        });
    }
    //Adds the menu bar which contains the settings button
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }
    //Tells how to handle the settings button when clicked
    public boolean onOptionsItemSelected(MenuItem item){


        if (item.getItemId() == R.id.action_setting){
            Toast.makeText(MainActivity.this, "Clicked on Settings", Toast.LENGTH_SHORT).show();
            //on selecting select icon, the settings page is called
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }
}
