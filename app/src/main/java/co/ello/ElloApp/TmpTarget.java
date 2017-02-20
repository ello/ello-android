package co.ello.ElloApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class TmpTarget implements Target
{
    private final static String TAG = TmpTarget.class.getSimpleName();

    private final Context context;
    private final String fileName;

    public TmpTarget(Context context, String fileName) {
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    public void onPrepareLoad (Drawable drawable) {}

    @Override
    public void onBitmapLoaded (Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            File savedFile = context.getFileStreamPath(fileName);
            if(savedFile != null) {
                Uri webURI = Uri.fromFile(savedFile);
                Intent imageResized = new Intent(ElloPreferences.IMAGE_RESIZED);
                if (webURI != null) {
                    imageResized.putExtra("RESIZED_IMAGE_PATH", webURI.getPath());
                    context.sendBroadcast(imageResized);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBitmapFailed (Drawable drawable) {}
}