package dk.sdu.mmmi.assignment7_energytactic;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager != null) {
            boolean passive_active = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            boolean network_active = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean gps_active = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean energy_efficiency_required = false;

            Log.d(TAG, "Passive: "+passive_active);
            Log.d(TAG, "Network: "+network_active);
            Log.d(TAG, "GPS: "+gps_active);

            Location location = null;

            if(energy_efficiency_required) {
                if(passive_active) {
                    Log.d(TAG, "Using Passive");
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if(SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos() > 6 * Math.pow(10,10)) { // Age is over 1 minute
                        Log.d(TAG, "Passive is too old. Using Network");
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            } else {
                if(gps_active) {
                    Log.d(TAG, "Using GPS");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else {
                    Log.d(TAG, "GPS is not available");
                }
            }

            if(location != null) {
                Log.d(TAG, "LAT: "+location.getLatitude()+" LONG: "+location.getLongitude());
            }
        }

    }
}
