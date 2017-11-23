package dk.sdu.mmmi.assignment4_activitysense;

import android.content.Intent;

public interface ActivityBroadcastListener {
    void gotNewActivity(Intent i);
}
