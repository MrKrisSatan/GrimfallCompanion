package com.mrkrissatan.grimfallcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
    WebView web;
    FrameLayout root;
    LinearLayout panel;
    TextView status, debugLog;
    SharedPreferences prefs;
    String home = "https://www.grimfallwow.com/";
    boolean debugVisible = false;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("companion", MODE_PRIVATE);
        home = prefs.getString("url", home);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512|4096);
        root = new FrameLayout(this);
        web = new WebView(this);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) { setStatus("Loading: " + url); }
            @Override public void onPageFinished(WebView view, String url) { setStatus("Loaded: " + url); }
        });
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        buildOverlay();
        Button restore = new Button(this);
        restore.setText("Controls");
        FrameLayout.LayoutParams rp = new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.RIGHT);
        rp.setMargins(8,24,12,8);
        root.addView(restore,rp);
        restore.setOnClickListener(v -> panel.setVisibility(View.VISIBLE));
        setContentView(root);
        web.loadUrl(home);
    }

    void buildOverlay() {
        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(10,8,10,8);
        panel.setBackground(bg(0xDD141D29,16));
        FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);
        pp.setMargins(8,8,12,12);
        root.addView(panel,pp);
        TextView heading = new TextView(this);
        heading.setText("GRIMFALL COMPANION · 0.1.1");
        heading.setTextColor(Color.WHITE);
        panel.addView(heading);
        LinearLayout keyRow = new LinearLayout(this);
        panel.addView(keyRow);
        String[] keys = {"1","2","3","4","Q","E","R","F"};
        for(String key : keys) {
            Button button = new Button(this);
            button.setText(key);
            button.setTextSize(11);
            keyRow.addView(button,new LinearLayout.LayoutParams(58,50));
            button.setOnClickListener(v -> tapKey(key));
        }
        LinearLayout toolRow = new LinearLayout(this);
        panel.addView(toolRow);
        Button urlButton = new Button(this);
        urlButton.setText("URL");
        toolRow.addView(urlButton);
        urlButton.setOnClickListener(v -> showUrlDialog());
        Button debugButton = new Button(this);
        debugButton.setText("Debug");
        toolRow.addView(debugButton);
        debugButton.setOnClickListener(v -> toggleDebug());
        Button hideButton = new Button(this);
        hideButton.setText("Hide");
        toolRow.addView(hideButton);
        hideButton.setOnClickListener(v -> panel.setVisibility(View.GONE));
        status = new TextView(this);
        status.setTextColor(0xFFB8C9D9);
        status.setTextSize(11);
        status.setMaxWidth(440);
        panel.addView(status);
        debugLog = new TextView(this);
        debugLog.setTextColor(0xFF8DD6B4);
        debugLog.setTextSize(10);
        debugLog.setMaxWidth(440);
        debugLog.setVisibility(View.GONE);
        panel.addView(debugLog);
        setStatus("Ready · input is experimental");
    }

    GradientDrawable bg(int color,int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(radius);
        return shape;
    }
    String jsQuote(String value) {
        return "'" + value.replace("\\","\\\\").replace("'","\\'") + "'";
    }
    void tapKey(String key) {
        sendKey(key,true);
        sendKey(key,false);
        web.requestFocus();
        setStatus("Overlay key tap: " + key);
        logDebug("Overlay tap → " + key);
    }
    void sendKey(String key,boolean down) {
        String type = down ? "keydown" : "keyup";
        String code = (key.length()==1 && Character.isLetter(key.charAt(0))) ? "Key"+key.toUpperCase()
            : (key.length()==1 && Character.isDigit(key.charAt(0))) ? "Digit"+key : "";
        String js = "(()=>{let k="+jsQuote(key)+",c="+jsQuote(code)+",t="+jsQuote(type)+",e=new KeyboardEvent(t,{key:k,code:c,bubbles:true});document.dispatchEvent(e);window.dispatchEvent(e);})();";
        web.evaluateJavascript(js,null);
    }
    void setStatus(String text) { if(status!=null) status.setText(text); }
    void toggleDebug() {
        debugVisible = !debugVisible;
        if(debugLog!=null) debugLog.setVisibility(debugVisible?View.VISIBLE:View.GONE);
        setStatus(debugVisible?"Diagnostics enabled":"Diagnostics disabled");
    }
    void logDebug(String msg) {
        if(debugVisible && debugLog!=null) {
            String current = debugLog.getText().toString();
            if(current.length()>800) current=current.substring(current.length()-600);
            debugLog.setText(current + "\\n" + msg);
        }
    }
    void showUrlDialog() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(home);
        new AlertDialog.Builder(this).setTitle("Browser client URL").setView(input)
            .setNegativeButton("Cancel",null)
            .setNeutralButton("Homepage",(dialog,which)->{
                home="https://www.grimfallwow.com/";
                prefs.edit().putString("url",home).apply();
                web.loadUrl(home);
            })
            .setPositiveButton("Load",(dialog,which)->{
                String candidate=input.getText().toString().trim();
                if(candidate.startsWith("https://") || candidate.startsWith("http://")) {
                    home=candidate;
                    prefs.edit().putString("url",home).apply();
                    web.loadUrl(home);
                } else Toast.makeText(this,"Enter a valid http(s) URL",Toast.LENGTH_SHORT).show();
            }).show();
    }

    @Override public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        String key=null;
        int code=event.getKeyCode();
        switch(code) {
            case android.view.KeyEvent.KEYCODE_BUTTON_A: key="1"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_B: key="2"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_X: key="3"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_Y: key="4"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_L1: key="Q"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_R1: key="E"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_L2: key="R"; break;
            case android.view.KeyEvent.KEYCODE_BUTTON_R2: key="F"; break;
        }
        if(key!=null) {
            boolean pressed=event.getAction()==android.view.KeyEvent.ACTION_DOWN;
            sendKey(key,pressed);
            String message=KeyEvent.actionToString(event.getAction())+" "+KeyEvent.keyCodeToString(code)+" → "+key;
            setStatus("Controller: "+message);
            logDebug(message);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override public boolean onGenericMotionEvent(MotionEvent event) {
        if((event.getSource()&InputDevice.SOURCE_JOYSTICK)==InputDevice.SOURCE_JOYSTICK && event.getAction()==MotionEvent.ACTION_MOVE) {
            float x=event.getAxisValue(MotionEvent.AXIS_X);
            float y=event.getAxisValue(MotionEvent.AXIS_Y);
            String axes=String.format(java.util.Locale.US,"Stick X %.2f / Y %.2f · no movement mapping yet",x,y);
            setStatus(axes);
            logDebug(axes);
        }
        return super.onGenericMotionEvent(event);
    }
    @Override public void onBackPressed() {
        if(web!=null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}