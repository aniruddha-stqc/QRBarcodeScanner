package cctl.qrbarcodescanner;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.os.Vibrator;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/*
Following class ScanActivity initializes the scanner module and performs the scanning of barcode
and further sending the scanned item code to the web server for further processing
 */
public class ScanActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    //private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }
    /*
    * Following method onDestroy is called after scanning activity is over
    * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }
    /*
    * Following method onRequestPermissionsResult handles the permission for CAMERA access
    * */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }
    /*
    Following method showMessageOKCancel is used to display a OK Cancel pop up to the user
    * */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ScanActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    /*
    Following method BeepAndVibrate plays a beep sound along with a momentary vibration
    */
    private void BeepAndVibrate(){
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // Vibrate for 150 milliseconds
        v.vibrate(150);
        //Beep Sound
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

    }
    /*
    Following method get_IP_address dynamically fetches the IP Address from the WIFI connection
    */
    private String get_IP_address() {
        //WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        //WifiManager wifiInfo = wifiManager.getConnectionInfo();
        /*int ip = wifiInfo.getIpAddress();;

        String ipString = String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
*/
        //Currently a hardcoded IP address is being used until the dynamic fetch is not implmented
        return  "192.168.43.122";
    }
    /*
    Following method lookup_Item sends the scanned item code from app to the server, and handles the
    response it receives from the server
    */
    private void lookup_Item(final String myResult) {
        RequestQueue requestQueue;
        String URL;
        StringRequest request;

        requestQueue = Volley.newRequestQueue(this);
        /*
        The URL has to be made dynamic to obtain Static IP from user defined configuration or
        Dynamic IP obtained from WIFI
        */

        URL = "http://" + get_IP_address() + "/msr/item_control.php";

        /*
        * Send a HTTP POST request from app to server
        * */
        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            /*
            Following method onResponse receives the HTTP response that was sent from the server.
            The received JSON response is parsed by the app to display messages sent by the server.
            */
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),"SUCCESS "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),ResultActivity.class));
                    }else {
                        Toast.makeText(getApplicationContext(), "ERROR " +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            /*
            Following method fetches the error response if there is any error connecting to the
            server such as connection parsing error, authorisation failure error etc.
            */
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Toast.makeText(getApplicationContext(), "ERROR " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }){
            /*
            Following method getBodyContentType specifies the content type by over ridding the
            default content type for the HTTP POST request.
            */
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            /*
            Following method getParams sends the HTTP POST parameters from client app to the server.
            IMPORTANT: the parameter key name 'item_id' needs to match identical to the server side
            PHP script.
            */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("item_id",myResult);
                return hashMap;
            }
        };

        requestQueue.add(request);
    }
    @Override
    public void handleResult(Result result) {
        final String item_id;

        //BeepAndVibrate();

        //item_id = ParseResult(result.getText());
        item_id = result.getText();
        /*
        Following method lookup_Item communicates with the server and finds out if the item is
        present in the server side database
        */
        lookup_Item(item_id);

        Log.d("QRCodeScanner", item_id);
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //scannerView.stopCamera();
                finish();
               // scannerView.resumeCameraPreview(ScanActivity.this);

            }
        });


        builder.setMessage(item_id);
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
    /*
    The following method ParseResult shall parse the information from the scanned code and
    returns only the Item Code for use in further processing
    */
    private String ParseResult(String scanned_text) {
        /*
        Code written with assumption that the scanned code will be a 3-tuple with structure
        (INTERNAL NUMBER##ITEM CODE##DESCRIPTION) assumed delimiter is '##' double hash. So, second
        element in the array stores the item code. For example the QR code info is
        '8292##EGCA #241##LENOVO THINKPAD LAPTOP' the return code will be 'EGCA #241'
        */
        String[] split_text = scanned_text.split("##");
        return  split_text [1];
    }
}


