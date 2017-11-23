package dk.sdu.mmmi.assignment4_activitysense;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public static final String BROADCAST_NAME = "dk.sdu.mmmi.assignment4_activitysense";
    public static final String BROADCAST_EXTRA_TYPE = "type";
    public static final String BROADCAST_EXTRA_CONFIDENCE = "confidence";
    public static final String BROADCAST_EXTRA_TIME = "time";

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity activity = result.getMostProbableActivity();

            int activityType = activity.getType();
            int activityConf = activity.getConfidence();

            Log.d("ActivityRecogition", "Got type: "+activityType);
            Log.d("ActivityRecogition", "With confidence: "+activityConf);

            Intent broadcast = new Intent(BROADCAST_NAME);
            broadcast.putExtra(BROADCAST_EXTRA_TYPE, activityType);
            broadcast.putExtra(BROADCAST_EXTRA_CONFIDENCE, activityConf);
            broadcast.putExtra(BROADCAST_EXTRA_TIME, result.getTime());

            sendBroadcast(broadcast);
        }
    }
}
