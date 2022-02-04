package com.samcreators.wakeupwallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import uk.co.senab.photoview.PhotoView;

public class FullViewActivity extends AppCompatActivity {

    Bitmap photoBitmap, imageBitmap;
    PhotoView photoView;
    Toolbar toolbar;
    int widthPixels, heightPixels;
    ProgressBar progressBar;
    int id;
    DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        database = new DatabaseHelper(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        toolbar = findViewById(R.id.full_view_toolbar);
        photoView = findViewById(R.id.photo_view);
        progressBar = findViewById(R.id.full_progressbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);

        if (getIntent().hasExtra("url")) {
            String previewUrl = getIntent().getStringExtra("previewUrl");
            id = getIntent().getIntExtra("id",0);
            String url = getIntent().getStringExtra("url");

            if (id != 0){
                if (!database.checkIdInRecent(id)){
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMDDhhmmss");
                    String date = simpleDateFormat.format(new Date());
                    database.insertData(id, date);
                }
            }

            photoView.setZoomable(false);
            Glide.with(this).asBitmap().load(previewUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (!photoView.canZoom()){
                        imageBitmap = fastBlur(FullViewActivity.this, resource,18);
                        photoView.setImageBitmap(imageBitmap);
                    }
                }
                @Override public void onLoadCleared(@Nullable Drawable placeholder) {}
            });

            Glide.with(this).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    imageBitmap = resource;
                    photoView.setImageBitmap(imageBitmap);
                    photoView.setZoomable(true);
                    toolbar.inflateMenu(R.menu.full_view_menu);
                    progressBar.setVisibility(View.GONE);
                }
                @Override public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
        }
        else if (getIntent().hasExtra("uri")) {
            String uri = getIntent().getStringExtra("uri");
            Glide.with(this).asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    imageBitmap = getResizedBitmap(resource, 1080);
                    photoView.setImageBitmap(imageBitmap);
                    photoView.setZoomable(true);
                    toolbar.inflateMenu(R.menu.full_view_one_menu);
                    progressBar.setVisibility(View.GONE);
                }
                @Override public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
        }
        else if (getIntent().hasExtra("gradient")) {
            toolbar.inflateMenu(R.menu.full_view_menu);
            progressBar.setVisibility(View.GONE);

            widthPixels = getResources().getDisplayMetrics().widthPixels ;
            heightPixels = getResources().getDisplayMetrics().heightPixels ;

            int[] values = getIntent().getIntArrayExtra("gradient");
            if (values != null) {
                LinearGradient gradient = new LinearGradient(values[0] , 0, values[1] , values[2] , values[3], values[4], Shader.TileMode.CLAMP);
                imageBitmap = createDynamicGradient(gradient);
                photoView.setImageBitmap(imageBitmap);
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.download:
                        saveImage(imageBitmap,id);
                        break;
                    case R.id.set_wallpaper:
                        photoBitmap = photoView.getVisibleRectangleBitmap();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            String[] options = {"Home screen", "Lock screen", "Both"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(FullViewActivity.this);
                            builder.setTitle("SET WALLPAPER AS");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new setWallpaper().execute(which + 1);
                                    finish();
                                }
                            });
                            builder.show();
                        } else {
                            new setWallpaper().execute(0);
                            finish();
                        }
                        break;
                    case R.id.info:
                        Toast.makeText(FullViewActivity.this, "Info will availiable in next version..", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private Bitmap createDynamicGradient(LinearGradient gradient) {
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, widthPixels, heightPixels), p);
        return bitmap;
    }

    public static Bitmap fastBlur(Context context, Bitmap source, int radius) {
        Bitmap bitmap = source.copy(source.getConfig(), true);
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void saveImage(Bitmap bitmap, int imageId){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Wakeup Wallpaper");
        myDir.mkdirs();
        String fname;
        if (imageId == 0){
            Random random = new Random();
            fname = "image-gen-" + random.nextInt(100000) + ".jpg";
        }
        else {
            fname = "image-" + imageId + ".jpg";
        }
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Image saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class setWallpaper extends AsyncTask<Integer,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Integer... integers) {
            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (integers[0] == 1) {
                    try {
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else  if (integers[0] == 2) {
                    try {
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    try {
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                if (integers[0] == 0) {
                    try {
                        manager.setBitmap(photoBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
    }
}