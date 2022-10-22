package com.avsecam.cameraimageview;

import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Helper {

    public final static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public final static String imageExtension = ".jpeg";
    public final static String tempImageFilename = "temp";

    /**
     * Attempt to refresh the image view.
     * Will use a fallback image if the given image does not exist.
     */
    public static void refreshImageView(ImageView view, File image) {
        Picasso
                .get()
                .load(image)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.cactuar)
                .into(view);
    }

    public static void refreshImageView(ImageView view) {
        refreshImageView(view, new File(""));
    }

    public static File saveFile(File directory, String filename, byte[] jpeg) throws IOException
    {
        File savedImage = new File(directory, filename + imageExtension);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }
}
