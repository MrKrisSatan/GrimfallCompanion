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
    String home="https://www.grimfallwow.com/";
    String selectedMode="browser";
    boolean debugVisible=false,gameView=false;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("companion",MODE_PRIVATE);
        home=prefs.getString("url",home);
        selectedMode=prefs.getString("last_mode","browser");
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512|4096);
        root=new FrameLayout(this);
        setContentView(root);
        showStartMenu();
    }
    void clearRoot(){root.removeAllViews();panel=null;status=null;debugLog=null;}
    TextView label(String text,int size,int color){TextView t=new TextView(this);t.setText(text);t.setTextSize(size);t.setTextColor(color);return t;}
    Button menuButton(String text,int color){Button b=new Button(this);b.setText(text);b.setTextColor(Color.WHITE);b.setBackground(bg(color,18));return b;}
    void showStartMenu(){
        gameView=false;clearRoot();
        LinearLayout screen=new LinearLayout(this);screen.setOrientation(LinearLayout.VERTICAL);screen.setGravity(Gravity.CENTER);screen.setPadding(26,22,26,22);screen.setBackgroundColor(0xFF10151F);
        root.addView(screen,new FrameLayout.LayoutParams(-1,-1));
        TextView title=label("GRIMFALL COMPANION",25,0xFFE7C77A);title.setGravity(Gravity.CENTER);screen.addView(title);
        TextView subtitle=label("Choose how you want to enter Azeroth",15,0xFFCCD5E1);subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sub=new LinearLayout.LayoutParams(-1,-2);sub.topMargin=10;sub.bottomMargin=24;screen.addView(subtitle,sub);
        Button browser=menuButton("BROWSER CLIENT\nPlay Grimfall in WebView",0xFF244D70);
        Button local=menuButton("LOCAL CLIENT\n3.3.5a + ConsolePortLK required",0xFF66502A);
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.bottomMargin=12;screen.addView(browser,bp);
        screen.addView(local,new LinearLayout.LayoutParams(-1,0,1));
        TextView foot=label("Last used: "+(selectedMode.equals("local")?"Local Client":"Browser Client")+" · Switch modes any time",11,0xFF8C9BAE);foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.topMargin=18;screen.addView(foot,fp);
        browser.setOnClickListener(v->{selectedMode="browser";prefs.edit().putString("last_mode",selectedMode).apply();launchBrowser();});
        local.setOnClickListener(v->{selectedMode="local";prefs.edit().putString("last_mode",selectedMode).apply();showLocalMode();});
    }
    void showLocalMode(){
        gameView=false;clearRoot();
        LinearLayout screen=new LinearLayout(this);screen.setOrientation(LinearLayout.VERTICAL);screen.setPadding(24,22,24,22);screen.setGravity(Gravity.CENTER_VERTICAL);screen.setBackgroundColor(0xFF10151F);
        root.addView(screen,new FrameLayout.LayoutParams(-1,-1));
        screen.addView(label("LOCAL CLIENT",24,0xFFE7C77A));
        TextView info=label("Local mode is planned around a compatible 3.3.5a client. ConsolePortLK is a mandatory bundled component. The APK does not yet contain the addon or local runtime. Future setup will select and validate your own client folder, back up AddOns, install/repair ConsolePortLK while preserving other addons, configure Thor controls and launch.",14,0xFFE0E6ED);
        info.setPadding(0,18,0,20);screen.addView(info);
        Button browser=menuButton("Switch to Browser Client",0xFF244D70),back=menuButton("Back to mode selection",0xFF343D4B);screen.addView(browser);screen.addView(back);
        browser.setOnClickListener(v->{selectedMode="browser";prefs.edit().putString("last_mode",selectedMode).apply();launchBrowser();});
        back.setOnClickListener(v->showStartMenu());
    }
    void launchBrowser(){
        gameView=true;clearRoot();web=new WebView(this);web.setFocusable(true);web.setFocusableInTouchMode(true);
        WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setSupportZoom(false);
        web.setWebChromeClient(new WebChromeClient());web.setWebViewClient(new WebViewClient(){
            @Override public void onPageStarted(WebView view,String url,android.graphics.Bitmap favicon){setStatus("Loading: "+url);}
            @Override public void onPageFinished(WebView view,String url){setStatus("Loaded: "+url);}
        });
        root.addView(web,new FrameLayout.LayoutParams(-1,-1));buildOverlay();
        Button mode=new Button(this);mode.setText("Modes");FrameLayout.LayoutParams mp=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.LEFT);mp.setMargins(8,24,8,8);root.addView(mode,mp);mode.setOnClickListener(v->showStartMenu());web.loadUrl(home);
    }
    void buildOverlay(){
        panel=new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(10,8,10,8);panel.setBackground(bg(0xDD141D29,16));
        FrameLayout.LayoutParams pp=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);pp.setMargins(8,8,12,12);root.addView(panel,pp);
        panel.addView(label("GRIMFALL · BROWSER CONTROLS",12,Color.WHITE));
        LinearLayout keys=new LinearLayout(this);panel.addView(keys);String[] keyList={"1","2","3","4","Q","E","R","F"};
        for(String key:keyList){Button b=new Button(this);b.setText(key);b.setTextSize(11);keys.addView(b,new LinearLayout.LayoutParams(58,50));b.setOnClickListener(v->tapKey(key));}
        LinearLayout tools=new LinearLayout(this);panel.addView(tools);
        Button url=new Button(this);url.setText("URL");tools.addView(url);url.setOnClickListener(v->showUrlDialog());
        Button debug=new Button(this);debug.setText("Debug");tools.addView(debug);debug.setOnClickListener(v->toggleDebug());
        Button hide=new Button(this);hide.setText("Hide");tools.addView(hide);hide.setOnClickListener(v->panel.setVisibility(View.GONE));
        Button modes=new Button(this);modes.setText("Modes");tools.addView(modes);modes.setOnClickListener(v->showStartMenu());
        status=label("Ready · browser input is experimental",11,0xFFB8C9D9);status.setMaxWidth(440);panel.addView(status);
        debugLog=label("",10,0xFF8DD6B4);debugLog.setMaxWidth(440);debugLog.setVisibility(View.GONE);panel.addView(debugLog);
    }
    GradientDrawable bg(int color,int radius){GradientDrawable shape=new GradientDrawable();shape.setColor(color);shape.setCornerRadius(radius);return shape;}
    String jsQuote(String value){return "'"+value.replace("\\","\\\\").replace("'","\\'")+"'";}
    void tapKey(String key){sendKey(key,true);sendKey(key,false);web.requestFocus();setStatus("Overlay key tap: "+key);logDebug("Overlay tap → "+key);}
    void sendKey(String key,boolean down){
        String type=down?"keydown":"keyup";
        String code=(key.length()==1&&Character.isLetter(key.charAt(0)))?"Key"+key.toUpperCase():(key.length()==1&&Character.isDigit(key.charAt(0)))?"Digit"+key:"";
        String js="(()=>{let k="+jsQuote(key)+",c="+jsQuote(code)+",t="+jsQuote(type)+",e=new KeyboardEvent(t,{key:k,code:c,bubbles:true});document.dispatchEvent(e);window.dispatchEvent(e);})();";
        web.evaluateJavascript(js,null);
    }
    void setStatus(String text){if(status!=null)status.setText(text);}
    void toggleDebug(){debugVisible=!debugVisible;if(debugLog!=null)debugLog.setVisibility(debugVisible?View.VISIBLE:View.GONE);setStatus(debugVisible?"Diagnostics enabled":"Diagnostics disabled");}
    void logDebug(String msg){if(debugVisible&&debugLog!=null){String current=debugLog.getText().toString();if(current.length()>800)current=current.substring(current.length()-600);debugLog.setText(current+"\n"+msg);}}
    void showUrlDialog(){
        EditText input=new EditText(this);input.setSingleLine(true);input.setText(home);
        new AlertDialog.Builder(this).setTitle("Browser client URL").setView(input).setNegativeButton("Cancel",null)
            .setNeutralButton("Homepage",(dialog,which)->{home="https://www.grimfallwow.com/";prefs.edit().putString("url",home).apply();web.loadUrl(home);})
            .setPositiveButton("Load",(dialog,which)->{String candidate=input.getText().toString().trim();if(candidate.startsWith("https://")||candidate.startsWith("http://")){home=candidate;prefs.edit().putString("url",home).apply();web.loadUrl(home);}else Toast.makeText(this,"Enter a valid http(s) URL",Toast.LENGTH_SHORT).show();}).show();
    }
    @Override public boolean dispatchKeyEvent(android.view.KeyEvent event){
        if(!gameView)return super.dispatchKeyEvent(event);String key=null;int code=event.getKeyCode();
        switch(code){case android.view.KeyEvent.KEYCODE_BUTTON_A:key="1";break;case android.view.KeyEvent.KEYCODE_BUTTON_B:key="2";break;case android.view.KeyEvent.KEYCODE_BUTTON_X:key="3";break;case android.view.KeyEvent.KEYCODE_BUTTON_Y:key="4";break;case android.view.KeyEvent.KEYCODE_BUTTON_L1:key="Q";break;case android.view.KeyEvent.KEYCODE_BUTTON_R1:key="E";break;case android.view.KeyEvent.KEYCODE_BUTTON_L2:key="R";break;case android.view.KeyEvent.KEYCODE_BUTTON_R2:key="F";break;}
        if(key!=null){boolean pressed=event.getAction()==android.view.KeyEvent.ACTION_DOWN;sendKey(key,pressed);String msg=(pressed?"DOWN":"UP")+" "+KeyEvent.keyCodeToString(code)+" → "+key;setStatus("Controller: "+msg);logDebug(msg);return true;}return super.dispatchKeyEvent(event);
    }
    @Override public boolean onGenericMotionEvent(MotionEvent event){
        if(gameView&&(event.getSource()&InputDevice.SOURCE_JOYSTICK)==InputDevice.SOURCE_JOYSTICK&&event.getAction()==MotionEvent.ACTION_MOVE){float x=event.getAxisValue(MotionEvent.AXIS_X),y=event.getAxisValue(MotionEvent.AXIS_Y);String axes=String.format(java.util.Locale.US,"Stick X %.2f / Y %.2f · unmapped",x,y);setStatus(axes);logDebug(axes);}return super.onGenericMotionEvent(event);
    }
    @Override public void onBackPressed(){if(gameView&&web!=null&&web.canGoBack())web.goBack();else if(gameView)showStartMenu();else super.onBackPressed();}
}
