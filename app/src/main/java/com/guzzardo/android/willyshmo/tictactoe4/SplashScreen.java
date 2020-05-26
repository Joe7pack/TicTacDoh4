package com.guzzardo.android.willyshmo.tictactoe4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import static com.guzzardo.android.willyshmo.tictactoe4.WillyShmoApplication.getWillyShmoApplicationContext;

public class SplashScreen extends Activity implements ToastMessage {
    protected boolean mActive = true;
    private static boolean mSkipWaitCheck;
    private static int mSplashTime = 2500;
    public static ErrorHandler mErrorHandler;
    private static Resources mResources;

    private ProgressBar pgsBar;
    private int i = 0;
    private TextView txtView;
    public Handler threadHandler  = new Handler();
    private int progressIndex = 0;

    HandlerThread handlerThread;
    private Looper looper;
    private Handler looperHandler;
    final int SOMETHING_ACTION = 0;
    final int SOMETHING_ELSE_ACTION = 1;
    String MSG_KEY = "message key";

    boolean isPermitted = false;
    TextView waitText;


    /**
     * perform the action in `handleMessage` when the thread calls
     * `mHandler.sendMessage(msg)`
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString(MSG_KEY);
            final TextView myTextView = (TextView)findViewById(R.id.textView);
            myTextView.setText(string);
        }
    };

    private final Runnable mMessageSender = new Runnable() {
        public void run() {
            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString(MSG_KEY, getCurrentTime());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    };

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US);
        return dateFormat.format(new Date());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mResources = getResources();
        mErrorHandler = new ErrorHandler();
        boolean mPrizesAvailable = false;
        if ("true".equalsIgnoreCase(mResources.getString(R.string.prizesAvailable))) {
            mPrizesAvailable = true;
        }

        WillyShmoApplication.setLatitude(0);
        WillyShmoApplication.setLongitude(0);

       // handlerThread.start();

        WillyShmoApplication.setWillyShmoApplicationContext(this.getApplicationContext());

        if (mPrizesAvailable) {
            //new LoadPrizesTask().execute(SplashScreen.this, getApplicationContext(), getResources());
            //mSkipWaitCheck = true;
        }


        Context willyShmoApplicationContext = getWillyShmoApplicationContext();

        Intent myIntent = new Intent(willyShmoApplicationContext, FusedLocationActivity.class);
        startActivity(myIntent);



         // original code replaced with FusedLocationActivity above
        //Intent myIntent = new Intent(willyShmoApplicationContext, MainActivity.class);
        //startActivity(myIntent);
        //finish();


        /*
        WindowManager wm = (WindowManager) this.getSystemService(willyShmoApplicationContext.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int heightInDp = Math.round(metrics.heightPixels / (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        */

        View thisView = findViewById(android.R.id.content).getRootView();
        thisView.measure(0,0);
        int thisViewHeight = thisView.getMeasuredHeight();

        /*
        DisplayMetrics displayMetrics = willyShmoApplicationContext.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int heightInDp = Math.round(dpHeight);
        */

        TextView waitTextArea = findViewById(R.id.waitText);
        waitTextArea.measure(0, 0);
        int waitTextMeasuredHeight = waitTextArea.getMeasuredHeight();


        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.measure(0, 0);
        int progressBarMeasuredHeight = progressBar.getMeasuredHeight();

        //pgsBar = (ProgressBar) findViewById(R.id.pBar);
        //pgsBar.measure(0, 0);
        //int pgsBarMeasuredHeight = pgsBar.getMeasuredHeight(); // in pixels?

        //Object waitTextObj = waitText.getLayout();

        Layout myLayout = waitTextArea.getLayout();
        int myHeight = myLayout.getHeight();



        //int topMargin = params.topMargin;
        //int bottomMargin = params.bottomMargin;
        //int leftMargin = params.leftMargin;
        //int rightMargin = params.rightMargin;
        int topMargin = thisViewHeight - (waitTextMeasuredHeight + progressBarMeasuredHeight);



        //Object obj = waitText.getLayoutParams();



        //ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)waitTextArea.getLayoutParams();

//        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) toolbar.getLayoutParams();
        //params.setMargins(0, topMargin, 0, 0); //substitute parameters for left, top, right, bottom
        //waitTextArea.setLayoutParams(params);


        //startMyThread();
    }

    public void startMyThread() {
                progressIndex = pgsBar.getProgress();
                handlerThread = new HandlerThread("MyHandlerThread");
                handlerThread.start();
                looper = handlerThread.getLooper();
                looperHandler = new Handler(looper)  {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case SOMETHING_ACTION: {
                                doSomething();
                                break;
                            }
                            case SOMETHING_ELSE_ACTION:
                                doMoreThings();
                                break;
                            default:
                                break;
                        }
                    }
                };
            }

    public void setAsyncMessage() {
        Message msg = looperHandler.obtainMessage(SOMETHING_ACTION);
        looperHandler.sendMessage(msg);
    }

    public void setAsyncMessage2() {
        Message msg = looperHandler.obtainMessage(SOMETHING_ELSE_ACTION);
        looperHandler.sendMessage(msg);
    }

    private void  doSomething() {
        System.out.println("did something");
        //handlerThread.quit();
}

    private void doMoreThings() {
        System.out.println("did more things");
    }


    @Override
    public void onStart() {
        super.onStart();

       // waitText.setTextColor(Color.RED);


        GetConfigurationValuesFromDB getConfigurationValuesFromDB = new GetConfigurationValuesFromDB();
        getConfigurationValuesFromDB.execute(this, getApplicationContext(), getResources());

        /* no need maybe to run this as an Async task?
        Intent myIntent = new Intent(this, FusedLocationActivity.class);
        startActivity(myIntent);
        //mCallerActivity.finish();
        */


        //new LoadPrizesTask().execute(SplashScreen.this, getApplicationContext(), getResources());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setSplashActive(false);
        }
        return true;
    }

    private class ErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    }

    public void sendToastMessage(String message) {
        Message msg = SplashScreen.mErrorHandler.obtainMessage();
        msg.obj = message;
        SplashScreen.mErrorHandler.sendMessage(msg);
    }

    public void showGooglePlayError(final Integer isPlayAvailable, final String playErrorMessage) {
        try {
            AlertDialog dialog = createGooglePlayErrorDialog(isPlayAvailable, playErrorMessage);
            dialog.show();
        } catch (Exception e) {
            sendToastMessage(e.getMessage());
        }
    }

    public AlertDialog createGooglePlayErrorDialog(final Integer isPlayAvailable, final String playErrorMessage) {
        return new AlertDialog.Builder(SplashScreen.this)
                .setIcon(R.drawable.willy_shmo_small_icon)
                .setTitle(R.string.google_play_service_error)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked OK so do some stuff */
                        callGooglePlayServicesUtil(isPlayAvailable.intValue());
                        setSplashActive(false);
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked Cancel so do some stuff */
                        setSplashActive(false);
                    }
                })
                .setMessage(playErrorMessage)
                .create();
    }

    private void callGooglePlayServicesUtil(int isPlayAvailable) {
        GooglePlayServicesUtil.getErrorDialog(isPlayAvailable, SplashScreen.this, 99);
    }

    public void setSplashActive(boolean active) {
        mActive = active;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        setSplashActive(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //handlerThread.quit();
    }

}