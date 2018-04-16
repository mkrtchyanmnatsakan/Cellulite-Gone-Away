package com.wildcard.cellulite.userInterface;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.wildcard.cellulite.R;
import com.wildcard.cellulite.constantValue.Constants;
import com.wildcard.cellulite.model.DateForStatistic;
import com.wildcard.cellulite.receiver.AlarmReceiver;

import org.angmarch.views.NiceSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.vince.easysave.EasySave;

public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    private static final int FIVE_SECONDS = 5000;
    //TODO CHANGE TIME TO 6 HOUR
    private static final int TIME_ALARM_SECONDS = 60 * 60 * 6;
    private ImageView imageview;
    private RelativeLayout homeRelativeLayout;
    private LinearLayout inactiveLayout;
    private RelativeLayout statisticsRelativeLayout;
    private RelativeLayout spinnerRelative;
    private RelativeLayout playScenesRelative;
    private TextView statisticsTextView;
    private TextView homeTextView;
    private TextView infoTextView;
    private TextView videoTextView;
    private TextView timeTextView;
    private RelativeLayout infoRelativeLayout;
    private RelativeLayout videoRelativeLayout;
    private NiceSpinner spinner;
    private ArrayList<String> spinnerList;
    private  Handler handler;
    public static String TODAY_DATE;
    private MediaPlayer player;
    private CountDownTimer countDounTimer;
    private Vibrator v;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
         v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        String dataCreate = Prefs.getString(Constants.CREATE_APP_DATA,"");

       // Toast.makeText(this, dataCreate, Toast.LENGTH_SHORT).show();

        if(dataCreate == null || dataCreate.isEmpty()){
            @SuppressLint("SimpleDateFormat") String dataCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            Prefs.putString(Constants.CREATE_APP_DATA,dataCreated);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date strDate = null;
            try {
                strDate = sdf.parse(dataCreated);
            } catch (ParseException e) {
                e.printStackTrace();
            }





            //  Toast.makeText(this, "new data " + dataCreated, Toast.LENGTH_SHORT).show();
        }else {
            validateDateTime(dataCreate);
        }


        TODAY_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
      //  Toast.makeText(this, TODAY_DATE, Toast.LENGTH_SHORT).show();
        Log.e("tiem = ", TODAY_DATE);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strDate = null;
        try {
            strDate = sdf.parse("2018-04-14 02:38:27");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long defTime = System.currentTimeMillis() - strDate.getTime();

      long difDay =   defTime / (24 * 60 * 60 * 1000);


        Log.e("defTime = " , defTime + "" + " day = " + difDay);
//        if (System.currentTimeMillis() > strDate.getTime()) {
//
//        }



    }

    private void validateDateTime(String time){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strDate = null;
        try {
            strDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long defTime = System.currentTimeMillis() - strDate.getTime();

        long difDay =   defTime / (24 * 60 * 60 * 1000);

        if(difDay >= 1){
            @SuppressLint("SimpleDateFormat") String newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            @SuppressLint("SimpleDateFormat") final String newDateForSaveList = new SimpleDateFormat("MMM-dd").format(new Date());
            //Toast.makeText(this, newDateForSaveList, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<DateForStatistic> saveList = new EasySave(getBaseContext()).retrieveList(
                            Constants.SAVE_DATE_FOR_STATISTICS,DateForStatistic[].class);
                    if(saveList.size()>1){
                        saveList.add(new DateForStatistic(newDateForSaveList,saveList.get(saveList.size()-1).getValue()+5));

                    }
                }
            },150);

            Prefs.putString(Constants.CREATE_APP_DATA,newDate);
        }

    }


    private void createAlarmRecever(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, TIME_ALARM_SECONDS);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);


    }

    private void playBeep() {
        try {
            player = new MediaPlayer();

            if (player.isPlaying()) {
                player.stop();
                player.release();

            }
            AssetFileDescriptor descriptor = getAssets().openFd("ultra.mp3");
            Log.e("descriptor = " , descriptor.getFileDescriptor().toString());
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            player.prepare();
            player.setVolume(1f, 1f);
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e("printStackTrace = " , e.getMessage());
        }
    }


    public void reverseTimer(long Seconds,final TextView tv){

        scheduleVibrator();
        playBeep();

      countDounTimer = new CountDownTimer(Seconds* 1000, 1000) {

            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                long seconds =  millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText( String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }


            public void onFinish() {
                if(player != null && player.isPlaying()){
                    player.stop();
                    player.release();
                    player = null;
                }
                tv.setText("00:00");
                Prefs.putBoolean(Constants.IS_INACTIVE,true);
                spinnerRelative.setVisibility(View.GONE);
                inactiveLayout.setVisibility(View.VISIBLE);
                playScenesRelative.setClickable(false);
                playScenesRelative.setEnabled(false);

                @SuppressLint("SimpleDateFormat") String saveDateString = new SimpleDateFormat("MMM-dd").format(new java.util.Date());

                List<DateForStatistic> dateForStatisticLists = new ArrayList<>();
                dateForStatisticLists.add(new DateForStatistic(saveDateString,5f));

                new EasySave(getBaseContext()).saveList(Constants.SAVE_DATE_FOR_STATISTICS, dateForStatisticLists);

                createAlarmRecever();
                handler.removeCallbacksAndMessages(null);
            }
        }.start();
    }

    private void initViews(){

        inactiveLayout = findViewById(R.id.inactive_layout);
        spinnerRelative = findViewById(R.id.spinner_relative);
        playScenesRelative = findViewById(R.id.play_scenes_relative);
        timeTextView = findViewById(R.id.time_text_view);

        if(Prefs.getBoolean(Constants.IS_INACTIVE,false)){
            inactiveLayout.setVisibility(View.VISIBLE);
            spinnerRelative.setVisibility(View.GONE);
            playScenesRelative.setClickable(false);
            playScenesRelative.setEnabled(false);
            timeTextView.setText("00:00");
        }else {
            timeTextView.setText("01:25");
            playScenesRelative.setClickable(true);
            playScenesRelative.setEnabled(true);
            inactiveLayout.setVisibility(View.GONE);
            spinnerRelative.setVisibility(View.VISIBLE);
        }
        spinner = findViewById(R.id.nice_spinner);
         handler = new Handler();

        spinnerList = new ArrayList<>();
        spinnerList.add("Less than 70 Kg");
        spinnerList.add("More than 70 Kg");
        spinner.attachDataSource(spinnerList);
        spinner.setSelectedIndex(0);
        spinner.setBackgroundColor(getResources().getColor(R.color.transparent));
        spinner.setOnItemSelectedListener(this);



        playScenesRelative.setOnClickListener(this);
        imageview = findViewById(R.id.image_view);
        statisticsTextView = findViewById(R.id.statistics_text_view);
        homeTextView = findViewById(R.id.home_text_view);
        infoTextView = findViewById(R.id.info_text_view);
        videoTextView = findViewById(R.id.video_text_view);
        homeTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        homeRelativeLayout = findViewById(R.id.home_relative_layout);
        statisticsRelativeLayout = findViewById(R.id.statistics_relative_layout);
        infoRelativeLayout = findViewById(R.id.info_relative_layout);
        videoRelativeLayout = findViewById(R.id.video_relative_layout);

        homeRelativeLayout.setOnClickListener(this);
        statisticsRelativeLayout.setOnClickListener(this);
        infoRelativeLayout.setOnClickListener(this);
       // videoRelativeLayout.setOnClickListener(this);

        //setUpBlurEffect();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                List<DateForStatistic> saveList = new EasySave(getBaseContext()).retrieveList(Constants.SAVE_DATE_FOR_STATISTICS,DateForStatistic[].class);
                if(saveList!=null && !saveList.isEmpty() && saveList.size()>1){
                    long timeSecund =   parseStringToSecondAndSecund(timeTextView.getText().toString(),(saveList.size()-1)*5);
                    long convertMilisecond = timeSecund;

                    timeTextView.setText(convertDate(String.valueOf(convertMilisecond),"mm:ss"));
                }


            }
        },500);
    }

    private void setUpBlurEffect(){
        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap, 25);
        imageview.setImageBitmap(blurred);
    }

    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(this);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() ; i++) {
            getSupportFragmentManager().popBackStack();

        }


        homeRelativeLayout.setClickable(false);
        homeRelativeLayout.setEnabled(false);
        homeTextView.setTextColor(getResources().getColor(R.color.colorPrimary));

        statisticsRelativeLayout.setEnabled(true);
        statisticsRelativeLayout.setClickable(true);

        statisticsTextView.setTextColor(getResources().getColor(R.color.colorWhite));

        infoRelativeLayout.setEnabled(true);
        infoRelativeLayout.setClickable(true);

        infoTextView.setTextColor(getResources().getColor(R.color.colorWhite));

        videoRelativeLayout.setEnabled(true);
        videoRelativeLayout.setClickable(true);

        videoTextView.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    public void scheduleVibrator() {
        handler.postDelayed(new Runnable() {
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        v.vibrate(VibrationEffect.createOneShot(2500,VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                }else{
                    //deprecated in API 26
                    v.vibrate(2500);
                }
                //Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }


private long parseStringToSecondAndSecund(String time, long seconds){
    //@SuppressLint("SimpleDateFormat") DateFormat format=new SimpleDateFormat("mm:ss");
// format.parse(timee).getSeconds();
    //int seconds = format.parse(time).getHours()*3600+format.parse(time).getMinutes()*60+format.parse(time).getSeconds();

    String[] parts = time.split(":");
    long millis = TimeUnit.MINUTES.toSeconds(Long.parseLong(parts[0])) + Long.parseLong(parts[1]);

    System.out.println(millis+" in Seconds");

//System.out.println(seconds + " Converted to Seconds");
    long s_hour = TimeUnit.SECONDS.toHours(millis);
    long tempSec = millis - (TimeUnit.HOURS.toSeconds(s_hour));
    // long s_minute = TimeUnit.SECONDS.toMinutes(tempSec) ;
    // long tempSec1 = tempSec - (TimeUnit.MINUTES.toSeconds(s_minute));
    long s_seconds = TimeUnit.SECONDS.toSeconds(tempSec);
//        System.out.println(s_hour  + " in Hours");
//        System.out.println(tempSec+" Which is a reminder");
//        System.out.println(s_minute + " in Minutes");
//        System.out.println(tempSec1+" in Seconds");

    return s_seconds + seconds;

}

    private long parseStringToSecond(String time) throws ParseException{

       // @SuppressLint("SimpleDateFormat") DateFormat format=new SimpleDateFormat("mm:ss");
// format.parse(timee).getSeconds();
        //int seconds = format.parse(time).getHours()*3600+format.parse(time).getMinutes()*60+format.parse(time).getSeconds();

        String[] parts = time.split(":");
        long millis = TimeUnit.MINUTES.toSeconds(Long.parseLong(parts[0])) + Long.parseLong(parts[1]);

        System.out.println(millis+" in Seconds");

//System.out.println(seconds + " Converted to Seconds");
        long s_hour = TimeUnit.SECONDS.toHours(millis);
        long tempSec = millis - (TimeUnit.HOURS.toSeconds(s_hour));
       // long s_minute = TimeUnit.SECONDS.toMinutes(tempSec) ;
       // long tempSec1 = tempSec - (TimeUnit.MINUTES.toSeconds(s_minute));
        long s_seconds = TimeUnit.SECONDS.toSeconds(tempSec);
//        System.out.println(s_hour  + " in Hours");
//        System.out.println(tempSec+" Which is a reminder");
//        System.out.println(s_minute + " in Minutes");
//        System.out.println(tempSec1+" in Seconds");

        return s_seconds;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(countDounTimer!= null){
            countDounTimer.cancel();
        }



        if(handler!= null){
            handler.removeCallbacksAndMessages(null);
        }





        if(player != null && player.isPlaying()){
            player.stop();
            player.release();
            player = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();


        if(handler!= null){
            handler.removeCallbacksAndMessages(null);
        }
        if(countDounTimer!= null){
            countDounTimer.cancel();
        }
        if(player != null && player.isPlaying()){
            player.stop();
            player.release();
            player = null;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_relative_layout:
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() ; i++) {
                    getSupportFragmentManager().popBackStack();

                }

                homeRelativeLayout.setClickable(false);
                homeRelativeLayout.setEnabled(false);
                homeTextView.setTextColor(getResources().getColor(R.color.colorPrimary));

                statisticsRelativeLayout.setEnabled(true);
                statisticsRelativeLayout.setClickable(true);

                statisticsTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                infoRelativeLayout.setEnabled(true);
                infoRelativeLayout.setClickable(true);

                infoTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                 videoRelativeLayout.setEnabled(true);
                videoRelativeLayout.setClickable(true);

                videoTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                break;

            case R.id.statistics_relative_layout:

                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() ; i++) {
                    getSupportFragmentManager().popBackStack();

                }

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_to_right, R.anim.right_to_left,
                                R.anim.finish_do_not_animate, R.anim.exit)
                        .add(R.id.container_layout, StatisticsFragment.newInstance(this),
                                "statisticsFragment")
                        .addToBackStack(null)
                        .commit();

                statisticsRelativeLayout.setEnabled(false);
                statisticsRelativeLayout.setClickable(false);

                homeRelativeLayout.setClickable(true);
                homeRelativeLayout.setEnabled(true);

                  infoRelativeLayout.setClickable(true);
                infoRelativeLayout.setEnabled(true);

                  videoRelativeLayout.setClickable(true);
                videoRelativeLayout.setEnabled(true);


                statisticsTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                homeTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                infoTextView.setTextColor(getResources().getColor(R.color.colorWhite));
                videoTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                break;

            case R.id.play_scenes_relative:

                playScenesRelative.setClickable(false);
                playScenesRelative.setEnabled(false);

                spinnerRelative.setEnabled(false);
                spinnerRelative.setClickable(false);
                spinner.setClickable(false);
                spinner.setEnabled(false);

                try {

                    reverseTimer(parseStringToSecond(timeTextView.getText().toString()),timeTextView);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //
              //  handler.removeCallbacksAndMessages(null);

             //   Toast.makeText(this, "play_scenes_relative", Toast.LENGTH_SHORT).show();
                break;

            case R.id.info_relative_layout:

                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() ; i++) {
                    getSupportFragmentManager().popBackStack();

                }

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_to_right, R.anim.right_to_left,
                                R.anim.finish_do_not_animate, R.anim.exit)
                        .add(R.id.container_layout, InfoFragment.newInstance(this),
                                "InfoFragment")
                        .addToBackStack(null)
                        .commit();

                infoRelativeLayout.setEnabled(false);
                infoRelativeLayout.setClickable(false);

                infoTextView.setTextColor(getResources().getColor(R.color.colorPrimary));




                statisticsRelativeLayout.setEnabled(true);
                statisticsRelativeLayout.setClickable(true);
                statisticsTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                homeRelativeLayout.setEnabled(true);
                homeRelativeLayout.setClickable(true);
                homeTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                videoRelativeLayout.setEnabled(true);
                videoRelativeLayout.setClickable(true);
                videoTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                break;

            case R.id.video_relative_layout:

                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() ; i++) {
                    getSupportFragmentManager().popBackStack();

                }

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.left_to_right, R.anim.right_to_left,
                                R.anim.finish_do_not_animate, R.anim.exit)
                        .add(R.id.container_layout, VideoFragment.newInstance(this),
                                "InfoFragment")
                        .addToBackStack(null)
                        .commit();

                videoRelativeLayout.setEnabled(false);
                videoRelativeLayout.setClickable(false);

                videoTextView.setTextColor(getResources().getColor(R.color.colorPrimary));




                statisticsRelativeLayout.setEnabled(true);
                statisticsRelativeLayout.setClickable(true);
                statisticsTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                infoRelativeLayout.setEnabled(true);
                infoRelativeLayout.setClickable(true);
                infoTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                homeRelativeLayout.setEnabled(true);
                homeRelativeLayout.setClickable(true);
                homeTextView.setTextColor(getResources().getColor(R.color.colorWhite));

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0){
            timeTextView.setText("01:25");
        }else {
            timeTextView.setText("02:25");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                List<DateForStatistic> saveList = new EasySave(getBaseContext()).retrieveList(Constants.SAVE_DATE_FOR_STATISTICS,DateForStatistic[].class);
                long timeSecund =   parseStringToSecondAndSecund(timeTextView.getText().toString(),(saveList.size()-1)*5);
                long convertMilisecond = timeSecund;

                timeTextView.setText(convertDate(String.valueOf(convertMilisecond),"mm:ss"));

            }
        },500);
       // Toast.makeText(this, spinnerList.get(position), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public  String convertDate(String dateInMilliseconds,String dateFormat) {
        return android.text.format.DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)*1000).toString();
    }
}
