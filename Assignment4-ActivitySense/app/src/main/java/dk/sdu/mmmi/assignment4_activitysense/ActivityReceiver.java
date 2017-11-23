package dk.sdu.mmmi.assignment4_activitysense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActivityReceiver extends BroadcastReceiver {

    private ActivityBroadcastListener listener;

    public ActivityReceiver(ActivityBroadcastListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.gotNewActivity(intent);
    }
}
