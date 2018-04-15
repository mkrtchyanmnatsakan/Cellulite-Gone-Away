package com.wildcard.cellulite.userInterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wildcard.cellulite.R;
import com.wildcard.cellulite.utile.NotificationsUtils;

/**
 * Created by mno on 4/11/18.
 */

public class InfoFragment extends Fragment {

    private static BaseActivity sBaseActivity;
    private ImageView imageViewInfo;
    private Switch switchView;
    private View positiveAction;
    private View negativeAction;

    public static InfoFragment newInstance(BaseActivity baseActivity) {
        sBaseActivity = baseActivity;
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        initView(view);

        return view;
    }

    @Override
    public void onResume() {


        super.onResume();
    }

    private void setUpBlurEffect(){
        BitmapDrawable drawable = (BitmapDrawable) imageViewInfo.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap, 25);
        imageViewInfo.setImageBitmap(blurred);
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

    private void initView(View view) {
        imageViewInfo = view.findViewById(R.id.image_view_info);
        imageViewInfo.setImageDrawable(sBaseActivity.getResources().getDrawable(R.drawable.cellulite_background));
       // setUpBlurEffect();

        switchView = view.findViewById(R.id.switch_view);
        switchView.setChecked(NotificationsUtils.isNotificationEnabled(sBaseActivity));

        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog =    new MaterialDialog.Builder(sBaseActivity)
                        .title("Warning")
                        .content("You are about going to settings to change notification status.Do you want to continue")
                        .positiveText("Yes")
                        .negativeText("No")
                        .show();

                positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                negativeAction = dialog.getActionButton(DialogAction.NEGATIVE);
                positiveAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                        intent.setData(uri);
                        getContext().startActivity(intent);
                        dialog.dismiss();
                        switchView.setChecked(NotificationsUtils.isNotificationEnabled(sBaseActivity));
                    }
                });
                negativeAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        switchView.setChecked(NotificationsUtils.isNotificationEnabled(sBaseActivity));
                    }
                });
            }
        });

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



            }
        });

    }
}
