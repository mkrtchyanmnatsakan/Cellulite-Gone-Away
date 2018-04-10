package com.wildcard.cellulite.userInterface;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wildcard.cellulite.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageview;
    private RelativeLayout homeRelativeLayout;
    private RelativeLayout statisticsRelativeLayout;
    private TextView statisticsTextView;
    private TextView homeTextView;
    private TextView infoTextView;
    private TextView videoTextView;
    private RelativeLayout infoRelativeLayout;
    private RelativeLayout videoRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews(){
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
        videoRelativeLayout.setOnClickListener(this);

        setUpBlurEffect();
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
}
