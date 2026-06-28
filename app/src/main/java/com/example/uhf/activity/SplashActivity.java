package com.example.uhf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uhf.R;

/**
 * 开屏动画（Splash Screen）
 * 应用启动时仅显示图标，图标先渐显再渐隐，动画结束后无缝跳转到主界面。
 */
public class SplashActivity extends AppCompatActivity {

    private static final long FADE_IN_DURATION = 500;   // 渐显 500ms
    private static final long FADE_OUT_DURATION = 1000;  // 渐隐 1000ms

    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = findViewById(R.id.iv_splash_logo);

        // 开启动画，动画结束后通过回调立即跳转
        startSplashAnimation();
    }

    /**
     * 启动渐显 → 渐隐动画
     */
    private void startSplashAnimation() {
        // 第一阶段：渐显（0 → 1）
        ivLogo.animate()
                .alpha(1f)
                .setDuration(FADE_IN_DURATION)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // 第二阶段：渐隐（1 → 0），完成后跳转
                        ivLogo.animate()
                                .alpha(0f)
                                .setDuration(FADE_OUT_DURATION)
                                .setInterpolator(new AccelerateInterpolator())
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        navigateToMain();
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    /**
     * 跳转到主界面，并结束当前 Activity
     */
    private void navigateToMain() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Intent intent = new Intent(SplashActivity.this, UHFMainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
