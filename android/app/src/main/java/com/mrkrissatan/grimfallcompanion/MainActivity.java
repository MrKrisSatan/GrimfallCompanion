package com.mrkrissatan.grimfallcompanion;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class MainActivity extends Activity {
    WebView web; FrameLayout root; String home="https://www.grimfallwow.com/";
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512|4096);
        root=new FrameLayout(this);
        web=new WebView(this); web.setFocusable(true); web.setFocusableInTouchMode(true);
        WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        web.setWebChromeClient(new WebChromeClient()); web.setWebViewClient(new WebViewClient());
        root.addView(web,new FrameLayout.LayoutParams(-1,-1)); buildOverlay(); setContentView(root); web.loadUrl(home);
    }
    void buildOverlay() {
        LinearLayout panel=new LinearLayout(this); panel.setOrientation(LinearLayout.VERTICAL); panel.setPadding(10,8,10,8);
        panel.setBackground(bg(0xBB141D29,18));
        FrameLayout.LayoutParams pp=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT); pp.setMargins(8,8,16,16); root.addView(panel,pp);
        TextView label=new TextView(this); label.setText("GRIMFALL · EXPERIMENTAL KEYS"); label.setTextColor(Color.WHITE); panel.addView(label);
        LinearLayout row=new LinearLayout(this); panel.addView(row);
        String[] keys={"1","2","3","4","Q","E","R","F"};
        for(String k:keys) { Button btn=new Button(this); btn.setText(k); btn.setTextSize(12); row.addView(btn,new LinearLayout.LayoutParams(64,54)); btn.setOnClickListener(v->tapKey(k)); }
        Button hide=new Button(this); hide.setText("Hide overlay"); panel.addView(hide); hide.setOnClickListener(v->panel.setVisibility(View.GONE));
    }
    GradientDrawable bg(int c,int r) { GradientDrawable d=new GradientDrawable(); d.setColor(c); d.setCornerRadius(r); return d; }
    String jsQuote(String s){ return "'"+s.replace("\\","\\\\").replace("'","\\'")+"'"; }
    void tapKey(String k) { dispatchJsKey(k,true); dispatchJsKey(k,false); web.requestFocus(); }
    void dispatchJsKey(String k,boolean down) {
        String type=down?"keydown":"keyup";
        String code=(k.length()==1&&Character.isLetter(k.charAt(0)))?"Key"+k.toUpperCase():(k.length()==1&&Character.isDigit(k.charAt(0)))?"Digit"+k:"";
        String js="(()=>{let k="+jsQuote(k)+",c="+jsQuote(code)+",t="+jsQuote(type)+",e=new KeyboardEvent(t,{key:k,code:c,bubbles:true});document.dispatchEvent(e);window.dispatchEvent(e);})();";
        web.evaluateJavascript(js,null);
    }
    @Override public boolean dispatchKeyEvent(android.view.KeyEvent e) {
        String k=null;
        switch(e.getKeyCode()) {
            case android.view.KeyEvent.KEYCODE_BUTTON_A:k="1";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_B:k="2";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_X:k="3";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_Y:k="4";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_L1:k="Q";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_R1:k="E";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_L2:k="R";break;
            case android.view.KeyEvent.KEYCODE_BUTTON_R2:k="F";break;
        }
        if(k!=null) { dispatchJsKey(k,e.getAction()==android.view.KeyEvent.ACTION_DOWN); return true; }
        return super.dispatchKeyEvent(e);
    }
    @Override public void onBackPressed(){ if(web.canGoBack())web.goBack(); else super.onBackPressed(); }
}