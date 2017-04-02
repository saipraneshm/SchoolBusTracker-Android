package com.sjsu.edu.schoolbustracker.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.math.BigDecimal;
import java.util.Currency;


/**
 * Created by sai pranesh on 28-Mar-17.
 */

public class SchoolBusTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
     //   FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //To log use the following statement
        //logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
    }
}
