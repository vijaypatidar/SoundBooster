package com.vkpapps.soundbooster.utils;

import android.app.Activity;
import android.content.IntentSender;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.vkpapps.soundbooster.R;

public class UpdateManager {
    private boolean auto;

    public UpdateManager(boolean auto) {
        this.auto = auto;
    }

    public void checkForUpdate(boolean isCheck, Activity context) {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (isCheck) {

                    Snackbar snackbar = Snackbar.make(
                            context.findViewById(R.id.container),
                            "update available",
                            Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Update", view -> checkForUpdate(false, context));
                    snackbar.setActionTextColor(
                            context.getResources().getColor(R.color.colorAccent));
                    snackbar.show();
                } else {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                context,
                                // Include a request code to later monitor this update request.
                                123);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                if (!auto) {
                    Toast.makeText(context, "Already updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
