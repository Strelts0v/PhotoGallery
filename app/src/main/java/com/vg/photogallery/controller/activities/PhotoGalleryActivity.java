package com.vg.photogallery.controller.activities;

import android.support.v4.app.Fragment;
import com.vg.photogallery.controller.activities.templates.SingleFragmentActivity;
import com.vg.photogallery.controller.fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
