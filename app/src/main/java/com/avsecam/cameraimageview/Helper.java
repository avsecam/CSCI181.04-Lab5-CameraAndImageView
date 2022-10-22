package com.avsecam.cameraimageview;

import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class Helper {
    /**
     * Attempt to refresh the image view.
     * Will not refresh if the given image does not exist.
     */
    public static void refreshImageView(File image, ImageView view) {
        if (!image.exists()) return;
        Picasso
                .get()
                .load(image)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(view);
    }
}
