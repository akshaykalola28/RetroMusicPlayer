package code.name.monkey.retromusic.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.ColorUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import code.name.monkey.retromusic.R;
import code.name.monkey.retromusic.activities.base.AbsBaseActivity;
import code.name.monkey.retromusic.util.PreferenceUtil;
import code.name.monkey.retromusic.util.RetroColorUtil;


public class WhatsNewActivity extends AbsBaseActivity {
    WebView webView;
    Toolbar toolbar;
    AppBarLayout appBarLayout;


    private static void setChangelogRead(@NonNull Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int currentVersion = pInfo.versionCode;
            PreferenceUtil.getInstance(context).setLastChangeLogVersion(currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String colorToCSS(int color) {
        return String.format("rgb(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)); // on API 29, WebView doesn't load with hex colors
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        webView = findViewById(R.id.webView);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);

        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        appBarLayout.setBackgroundColor(ThemeStore.primaryColor(this));
        //setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        RetroColorUtil.colorBackButton(toolbar, ThemeStore.textColorSecondary(this));

        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = getAssets().open("retro-changelog.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();

            // Inject color values for WebView body background and links
            final boolean isDark = ATHUtil.isWindowBackgroundDark(this);
            final String backgroundColor = colorToCSS(ATHUtil.resolveColor(this, R.attr.md_background_color, Color.parseColor(isDark ? "#424242" : "#ffffff")));
            final String contentColor = colorToCSS(Color.parseColor(isDark ? "#ffffff" : "#000000"));
            final String changeLog = buf.toString()
                    .replace("{style-placeholder}",
                            String.format("body { background-color: %s; color: %s; }", backgroundColor, contentColor))
                    .replace("{link-color}", colorToCSS(ThemeStore.accentColor(this)))
                    .replace("{link-color-active}", colorToCSS(ColorUtil.lightenColor(ThemeStore.accentColor(this))));

            webView.loadData(changeLog, "text/html", "UTF-8");
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }
        setChangelogRead(this);
    }
}