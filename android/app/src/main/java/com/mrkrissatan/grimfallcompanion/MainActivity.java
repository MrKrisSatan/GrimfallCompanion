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
    EditText urlField;
    SharedPreferences prefs;
    String home="https://www.grimfallwow.com/";
    boolean overlayVisible=true;
    boolean debugVisible=false;
    Button restoreOverlay;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("companion",MODE_PRIVATE);
        home=prefs.getString("url",home);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512|4096);
        root=new FrameLayout(this);
        web=new WebView(this);
        web.setFocusable(true); web.setFocusableInTouchMode(true);
        WebSettings s=web.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        s.setSupportZoom(false);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient(){
            @Override public void onPageStarted(WebView view,String url,android.graphics.Bitmap favicon){setStatus("Loading: "+url);}
            @Override public void onPageFinished(WebView view,String url){setStatus("Loaded · "+url);}
            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest req){return false;}
        });
        root.addView(web,new FrameLayout.LayoutParams(-1,-1));
        buildOverlay();
        setContentView(root);
        web.loadUrl(home);
    }
    void buildOverlay(){
        panel=new LinearLayout(this); panel.setOrientation(LinearLayout.VERTICAL); panel.setPadding(10,8,10,8);
        panel.setBackground(bg(0xDD141D29,16));
        FrameLayout.LayoutParams pp=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);
        pp.setMargins(8,8,12,12); root.addView(panel,pp);
        TextView label=new TextView(this); label.setText("GRIMFALL COMPANION · 0.1.1"); label.setTextColor(Color.WHITE); panel.addView(label);
        LinearLayout row=new LinearLayout(this); panel.addView(row);
        String[] keys={"1","2","3","4","Q","E","R","F"};
        for(String k:keys){Button btn=new Button(this);btn.setText(k);btn.setTextSize(11);row.addView(btn,new LinearLayout.LayoutParams(58,50));btn.setOnClickListener(v->tapKey(k));}
        LinearLayout tools=new LinearLayout(this); panel.addView(tools);
        Button url=new Button(this);url.setText("URL");tools.addView(url);url.setOnClickListener(v->showUrlDialog());
        Button debug=new Button(this);debug.setText("Debug");tools.addView(debug);debug.setOnClickListener(v->toggleDebug());
        Button hide=new Button(this);hide.setText("Hide");tools.addView(hide);hide.setOnClickListener(v->panel.setVisibility(View.GONE));
        status=new TextView(this);status.setTextColor(0xFFB8C9D9);status.setTextSize(11);status.setMaxWidth(440);panel.addView(status);
        debugLog=new TextView(this);debugLog.setTextColor(0xFF8DD6B4);debugLog.setTextSize(10);debugLog.setMaxWidth(440);debugLog.setVisibility(View.GONE);panel.addView(debugLog);
        setStatus("Ready · controller events are experimental");
    }
    GradientDrawable bg(int c,int r){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(r);return d;}
    String quote(String s){return "'"+s.replace("\\","\\\\").replace("'","\\'")+"'";}
    void tapKey(String k){sendKey(k,true);sendKey(k,false);web.requestFocus();setStatus("Overlay key: "+k);}
    void sendKey(String k,boolean down){
        String type=down?"keydown":"keyup";
        String code=(k.length()==1&&Character.isLetter(k.charAt(0)))?"Key"+k.toUpperCase():(k.length()==1&&Character.isDigit(k.charAt(0)))?"Digit"+k:"";
        String js="(()=>{let k="+quote(k)+",c="+quote(code)+",t="+quote(type)+",e=new KeyboardEvent(t,{key:k,code:c,bubbles:true});document.dispatchEvent(e);window.dispatchEvent(e);})();";
        web.evaluateJavascript(js,null);
    }
    void setStatus(String text){if(status!=null)status.setText(text);}
    void showUrlDialog(){
        EditText input=new EditText(this);input.setSingleLine(true);input.setText(home);input.setHint("https:// Grimfall browser client URL");
        new AlertDialog.Builder(this).setTitle("Set browser client URL").setView(input)
        .setNegativeButton("Cancel",null).setNeutralButton("Homepage", (d,w)->{home="https://www.grimfallwow.com/";prefs.edit().putString("url",home).apply();web.loadUrl(home);})
        .setPositiveButton("Load",(d,w)->{String value=input.getText().toString().trim();if(value.startsWith("https://")||value.startsWith("http://")){home=value;prefs.edit().putString("url",home).apply();web.loadUrl(home);}else Toast.makeText(this,"Enter a URL beginning with https://",Toast.LENGTH_SHORT).show();}).show();
    }
    void toggleDebug(){debugVisible=!debugVisible;if(debugLog!=null)debugLog.setVisibility(debugVisible?View.VISIBLE:View.GONE);setStatus(debugVisible?"Diagnostics enabled · raw controller events":"Diagnostics disabled");}\n    void logDebug(String msg){if(debugVisible&&debugLog!=null){String old=debugLog.getText().toString();debugLog.setText((old.length()>800?old.substring(old.length()-600):old)+"\n"+msg);}}
    @Override public boolean dispatchKeyEvent(android.view.KeyEvent e){
        String k=null;int c=e.getKeyCode();
        switch(c){case android.view.KeyEvent.KEYCODE_BUTTON_A:k="1";break;case android.view.KeyEvent.KEYCODE_BUTTON_B:k="2";break;case android.view.KeyEvent.KEYCODE_BUTTON_X:k="3";break;case android.view.KeyEvent.KEYCODE_BUTTON_Y:k="4";break;case android.view.KeyEvent.KEYCODE_BUTTON_L1:k="Q";break;case android.view.KeyEvent.KEYCODE_BUTTON_R1:k="E";break;case android.view.KeyEvent.KEYCODE_BUTTON_L2:k="R";break;case android.view.KeyEvent.KEYCODE_BUTTON_R2:k="F";break;}
        if(k!=null){boolean down=e.getAction()==android.view.KeyEvent.ACTION_DOWN;sendKey(k,down);String msg=KeyEvent.actionToString(e.getAction())+" "+KeyEvent.keyCodeToString(c)+" → "+k;setStatus("Controller "+msg);logDebug(msg);return true;}
        return super.dispatchKeyEvent(e);
    }
    @Override public boolean onGenericMotionEvent(MotionEvent e){
        if((e.getSource()&InputDevice.SOURCE_JOYSTICK)==InputDevice.SOURCE_JOYSTICK&&e.getAction()==MotionEvent.ACTION_MOVE){
            float lx=e.getAxisValue(MotionEvent.AXIS_X),ly=e.getAxisValue(MotionEvent.AXIS_Y);
            String axes=String.format(java.util.Locale.US,"Stick axes · X %.2f / Y %.2f · analog mapping not yet enabled",lx,ly);setStatus(axes);logDebug(axes);
        }
        return super.onGenericMotionEvent(e);
    }
    @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}