package com.animeshdey.thewallflower;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class MainActivity extends Activity {
    private static final String PREFS = "wallflower_settings";
    private static final String URL_KEY = "pc_url";
    private static final String BACKGROUND = "#222326";
    private static final String CARD = "#303134";
    private static final String WHITE = "#F1F1F2";
    private LinearLayout setup;
    private EditText address;
    private WebView web;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        verifyCreatorSeal();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        buildScreen();
        String saved = getPreferences(MODE_PRIVATE).getString(URL_KEY, "");
        if (!saved.isEmpty()) openDashboard(saved);
    }

    private void verifyCreatorSeal() {
        try {
            String credit = getString(R.string.internal_creator_credit);
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(credit.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) hex.append(String.format("%02x", b));
            if (!hex.toString().equals(getString(R.string.internal_creator_sha256))) throw new IllegalStateException("Creator attribution integrity check failed.");
        } catch (Exception e) { throw new IllegalStateException("The Wallflower integrity check failed.", e); }
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.parseColor(BACKGROUND));
        LinearLayout top = new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL); top.setPadding(dp(16), dp(8), dp(12), dp(8)); top.setBackgroundColor(Color.parseColor(BACKGROUND));
        TextView title = new TextView(this); title.setText("The Wallflower"); title.setTextColor(Color.parseColor(WHITE)); title.setTextSize(18); title.setTypeface(null, 1); top.addView(title, new LinearLayout.LayoutParams(0, dp(42), 1));
        Button change = new Button(this); change.setText("PC"); change.setOnClickListener(v -> { setup.setVisibility(View.VISIBLE); address.requestFocus(); }); top.addView(change); root.addView(top);
        setup = new LinearLayout(this); setup.setOrientation(LinearLayout.VERTICAL); setup.setPadding(dp(16), dp(10), dp(16), dp(12)); setup.setBackgroundColor(Color.parseColor(CARD));
        TextView help = new TextView(this); help.setText("Connect to your PC dashboard over the same Wi-Fi. Start the Wallflower PC server, then enter its address."); help.setTextColor(Color.LTGRAY); help.setTextSize(13); setup.addView(help);
        address = new EditText(this); address.setSingleLine(true); address.setHint("http://192.168.x.x:8765"); address.setTextColor(Color.WHITE); address.setHintTextColor(Color.GRAY); address.setInputType( android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI); setup.addView(address);
        Button connect = new Button(this); connect.setText("Connect"); connect.setOnClickListener(v -> { String value = address.getText().toString().trim(); if (value.isEmpty()) { Toast.makeText(this, "Enter the PC dashboard address", Toast.LENGTH_SHORT).show(); return; } if (!value.startsWith("http://") && !value.startsWith("https://")) value = "http://" + value; openDashboard(value); }); setup.addView(connect); root.addView(setup);
        web = new WebView(this); web.setBackgroundColor(Color.parseColor(BACKGROUND)); WebSettings settings = web.getSettings(); settings.setJavaScriptEnabled(true); settings.setDomStorageEnabled(true); settings.setLoadsImagesAutomatically(true); web.setWebViewClient(new WebViewClient() { @Override public void onReceivedError(WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) { if (request.isForMainFrame()) Toast.makeText(MainActivity.this, "PC not reachable. Check Wi-Fi and the PC server.", Toast.LENGTH_LONG).show(); }}); web.setWebChromeClient(new WebChromeClient()); root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1)); web.setVisibility(View.GONE); setContentView(root);
    }

    private void openDashboard(String url) {
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        getPreferences(MODE_PRIVATE).edit().putString(URL_KEY, url).apply();
        setup.setVisibility(View.GONE); web.setVisibility(View.VISIBLE); web.loadUrl(url);
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
