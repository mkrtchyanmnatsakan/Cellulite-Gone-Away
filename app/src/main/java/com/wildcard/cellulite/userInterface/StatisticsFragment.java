package com.wildcard.cellulite.userInterface;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.wildcard.cellulite.R;
import com.wildcard.cellulite.constantValue.Constants;
import com.wildcard.cellulite.model.DateForStatistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.vince.easysave.EasySave;

/**
 * Created by mno on 4/11/18.
 */

public class StatisticsFragment extends Fragment {

    private static BaseActivity sBaseActivity;
    private ImageView imageViewStatistics;
    private HorizontalBarChart barGraph;

    public static StatisticsFragment newInstance(BaseActivity baseActivity) {
        sBaseActivity = baseActivity;
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        initView(view);

        return view;
    }




    private void setUpBlurEffect(){
        BitmapDrawable drawable = (BitmapDrawable) imageViewStatistics.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap, 25);
        imageViewStatistics.setImageBitmap(blurred);
    }

    private void initView(View view){
        imageViewStatistics = view.findViewById(R.id.image_view_statistics);
        imageViewStatistics.setImageDrawable(sBaseActivity.getResources().getDrawable(R.drawable.cellulite_background));
      //  setUpBlurEffect();
        barGraph = view.findViewById(R.id.bar_graph);

        List<DateForStatistic> saveList = new EasySave(sBaseActivity.getBaseContext()).retrieveList(
                Constants.SAVE_DATE_FOR_STATISTICS,DateForStatistic[].class);

        if(saveList!= null && !saveList.isEmpty()){
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            ArrayList<String> dateList = new ArrayList<>();

            for (int i = 0; i < saveList.size(); i++) {
                barEntries.add(new BarEntry(saveList.get(i).getValue(),i));
                dateList.add(saveList.get(i).getDate());
            }

//        barEntries.add(new BarEntry(20f,1));
//        barEntries.add(new BarEntry(25f,2));
//        barEntries.add(new BarEntry(30f,3));
//        barEntries.add(new BarEntry(35f,4));
//        barEntries.add(new BarEntry(40f,5));
//        barEntries.add(new BarEntry(20f,6));
//        barEntries.add(new BarEntry(25f,7));
//        barEntries.add(new BarEntry(30f,8));
//        barEntries.add(new BarEntry(35f,9));
//        barEntries.add(new BarEntry(40f,10));

            Collections.reverse(barEntries);

            BarDataSet barDataSet = new BarDataSet( barEntries,sBaseActivity.getResources().getString(R.string.app_name));

//        ArrayList<String> dateList = new ArrayList<>();
//
//        dateList.add("Apr 17");
//        dateList.add("Apr 18");
//        dateList.add("Apr 19");
//        dateList.add("Apr 20");
//        dateList.add("Apr 21");
//        dateList.add("Apr 22");
//        dateList.add("Apr 23");
//        dateList.add("Apr 24");
//        dateList.add("Apr 25");
//        dateList.add("Apr 26");

            Collections.reverse(dateList);
            BarData barData = new BarData(dateList,barDataSet);

            barGraph.setData(barData);
            barGraph.setTouchEnabled(true);
            barGraph.setDragEnabled(true);
            barGraph.setScaleEnabled(true);
            barGraph.invalidate();
            barGraph.setDrawBarShadow(false);
            barGraph.setDrawValueAboveBar(true);


//        barGraph.getDescription().setEnabled(false);

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            barGraph.setMaxVisibleValueCount(7);

            // scaling can now only be done on x- and y-axis separately
            barGraph.setPinchZoom(true);
            barGraph.setBorderWidth(5f);
            barGraph.setDrawHighlightArrow(true);

            barGraph.setDrawGridBackground(false);
            barGraph.animateXY(1500,1500);
        }





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

        RenderScript renderScript = RenderScript.create(sBaseActivity);

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
}
