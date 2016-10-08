package com.faendir.lightning_launcher.multitool;

import android.app.Application;
import android.content.Context;

import com.faendir.lightning_launcher.multitool.util.ResetReportPrimer;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by Lukas on 13.12.2015.
 * Main Application class
 */
@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "https://faendir.smileupps.com/acra-multitool/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "multitool",
        formUriBasicAuthPassword = "mtR3p0rt",
        reportPrimerClass = ResetReportPrimer.class,
        buildConfigClass = BuildConfig.class
)
public class MultiTool extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
