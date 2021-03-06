package com.vg.photogallery.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.vg.photogallery.controller.activities.templates.SingleFragmentActivity;
import com.vg.photogallery.controller.fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
