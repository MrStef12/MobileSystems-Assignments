package dk.sdu.mmmi.assignment4_activitysense;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepFormatter;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityBroadcastListener {

    private static final String TAG = "MainActivity";

    private XYPlot activityPlot;
    private StepFormatter stepFormatter;
    private ActivityRecognitionClient client;
    private PendingIntent pendingIntent;
    GoogleApiClient apiClient;
    private BroadcastReceiver activityReceiver;
    private List<Long> obs_time;
    private List<Integer> obs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityPlot = (XYPlot) findViewById(R.id.plot);
        obs_time = new ArrayList<>();
        obs = new ArrayList<>();

        setupPlot();

        client = ActivityRecognition.getClient(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActivityRecognizedService.BROADCAST_NAME);
        activityReceiver = new ActivityReceiver(this);
        registerReceiver(activityReceiver, intentFilter);

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    private void setupPlot() {
        // create our series from our array of nums:
        XYSeries series = new SimpleXYSeries(
                obs,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Activities");

        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setColor(Color.BLUE);
        lineFill.setAlpha(220);

        stepFormatter = new StepFormatter(Color.WHITE, Color.BLUE);
        stepFormatter.setVertexPaint(null); // don't draw individual points
        stepFormatter.getLinePaint().setStrokeWidth(PixelUtils.dpToPix(3));

        stepFormatter.getLinePaint().setAntiAlias(false);
        stepFormatter.setFillPaint(lineFill);
        activityPlot.addSeries(series, stepFormatter);

        // adjust the domain/range ticks to make more sense; label per line for range and label per 5 ticks domain:
        activityPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
        activityPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        activityPlot.setLinesPerRangeLabel(1);
        activityPlot.setLinesPerDomainLabel(5);
        activityPlot.getGraph().setPaddingLeft(125);

        // get rid of decimal points in our domain labels:

        activityPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("0"));

        // create a custom getFormatter to draw our state names as range tick labels:
        activityPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo,
                                       @NonNull FieldPosition pos) {
                Number num = (Number) obj;
                switch (num.intValue()) {
                    case 0:
                        toAppendTo.append("VEHICLE");
                        break;
                    case 1:
                        toAppendTo.append("BICYCLE");
                        break;
                    case 2:
                        toAppendTo.append("FOOT");
                        break;
                    case 3:
                        toAppendTo.append("STILL");
                        break;
                    case 4:
                        toAppendTo.append("UNKNOWN");
                        break;
                    case 5:
                        toAppendTo.append("TILTING");
                        break;
                    case 7:
                        toAppendTo.append("WALKING");
                        break;
                    case 8:
                        toAppendTo.append("RUNNING");
                        break;
                    default:
                        toAppendTo.append("DEFAULT");
                        break;
                }
                return toAppendTo;
            }
            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Stopping activity updates...");
        ActivityRecognition.getClient(this).removeActivityUpdates(pendingIntent);
        unregisterReceiver(activityReceiver);
        apiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlot();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.getClient(this).requestActivityUpdates(10000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void gotNewActivity(Intent i) {
        int type = i.getIntExtra(ActivityRecognizedService.BROADCAST_EXTRA_TYPE, 10);
        long time = i.getLongExtra(ActivityRecognizedService.BROADCAST_EXTRA_TIME, 0);
        Log.d(TAG, "Got: "+type+" at time: "+time);
        obs_time.add(time);
        obs.add(type);
        updatePlot();
    }

    private void updatePlot() {
        activityPlot.clear();
        setupPlot();
        activityPlot.redraw();
    }
}
