package hacklian.lian.example.come.watchingdogcontrol;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("044a5b16-3fd7-47db-b6c5-dbe4e90f2d44");
    private TextView outputTest;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView vidView = (VideoView)findViewById(R.id.myVideo);
        String vidAddress = "https://mediasvclkmn5vd19lrm9.blob.core.windows.net/asset-cb15435d-1500-80c5-6d49-f1e5d84ed36b/20160109_144411.mp4?sv=2012-02-12&sr=c&si=9483c9d6-40ca-4b18-aead-d87dcccad661&sig=DoBmVcT4CB209PdOps8ZDgGC9tyMiZQmG%2Fk8Zh4tDqk%3D&st=2016-02-21T03%3A53%3A57Z&se=2116-01-28T03%3A53%3A57Z";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        vidView.start();
    }

    protected void onResume() {
        super.onResume();
        PebbleDictionary data = new PebbleDictionary();

        boolean isConnected = PebbleKit.isWatchConnected(this);
        Toast.makeText(this, "Pebble " + (isConnected ? "is" : "is not") + " connected!", Toast.LENGTH_LONG).show();
        if (isConnected) {
            data.addString(1, "Hi Lian");
            PebbleKit.sendDataToPebble(getApplicationContext(), PEBBLE_APP_UUID, data);
        }

        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {

            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                Log.i(getLocalClassName(), "Received value=" +
                        data.getUnsignedIntegerAsLong(0).intValue() + " for key: 0");
                outputTest = (TextView) findViewById(R.id.textView);
                if (data.getUnsignedIntegerAsLong(0).intValue() == 0) {
                    outputTest.setText("The door will close!");
                    outputTest.bringToFront();
                } else if (data.getUnsignedIntegerAsLong(0).intValue() == 1) {
                    outputTest.setText("The door will unlock!");
                } else {
                    outputTest.setText("Choose an option!");
                    outputTest.bringToFront();
                }
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        PebbleKit.closeAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
                    }

                }, 5000L);

            }

        });

    }


    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString=params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());

            } catch (Exception e ) {

                System.out.println(e.getMessage());

                return e.getMessage();

            }

            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

        }

    } // end CallAPI

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void turnRight(View view)
    {
      return;
    }

    public void turnLeft(View view)
    {
        return;
    }
}
