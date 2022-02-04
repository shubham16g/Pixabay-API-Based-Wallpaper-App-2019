package com.samcreators.wakeupwallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerateActivity extends AppCompatActivity {

    Bitmap bitmap;
    int widthPixels, heightPixels;
    ImageView imageView;
    int [] randomValues;
    ImageButton refreshButton;
    Toolbar toolbar;
    View wallpaperCard;
    LinearGradient gradient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_generate);
        setStatusBarGradiant(this);

        toolbar = findViewById(R.id.random_generator_toolbar);
        imageView = findViewById(R.id.random_image);
        refreshButton = findViewById(R.id.refresh_button);
        wallpaperCard = findViewById(R.id.wallpaper_card);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;

        refreshWallpaper();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshWallpaper();
                refreshButton.animate().scaleX(1.3f).scaleY(1.3f).rotation(180).setDuration(250);

                refreshButton.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshButton.setEnabled(true);
                        refreshButton.animate().scaleX(1).scaleY(1).rotation(0).setDuration(0);
                    }
                },250);
                refreshButton.clearAnimation();
            }
        });
        wallpaperCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RandomGenerateActivity.this,FullViewActivity.class);
                i.putExtra("gradient",randomValues);
                startActivity(i);
            }
        });

    }

    private void refreshWallpaper(){
        randomValues = getRandomValues();
        gradient = createGradient(randomValues);
        bitmap = createDynamicGradient(gradient);
        imageView.setImageBitmap(bitmap);
    }
    public int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
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

    private int [] getRandomValues(){
        Random random = new Random();
        int [] randomValues = new int[5];
        randomValues[0] = random.nextInt(1500);
        randomValues[1] = random.nextInt(1500);
        randomValues[2] = random.nextInt(1000) + 1000;
        randomValues[3] = getRandomColor();
        randomValues[4] = getRandomColor();
        return randomValues;
    }
    public LinearGradient createGradient(int [] randomValues){
        return new LinearGradient(randomValues[0], 0, randomValues[1], randomValues[2], randomValues[3], randomValues[4], Shader.TileMode.CLAMP);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_theme);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }
}
