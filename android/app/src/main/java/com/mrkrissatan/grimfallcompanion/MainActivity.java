package com.mrkrissatan.grimfallcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import androidx.documentfile.provider.DocumentFile;

public class MainActivity extends Activity {
    private static final int PICK_WOW_FOLDER = 9124;
    private static final int MOVE_POINTER_ID = 16;
    private static final int CAMERA_POINTER_ID = 17;
    private static final int ACTION_POINTER_BASE = 18;
    private static final float STICK_DEADZONE = 0.18f;

    private WebView web;
    private FrameLayout root;
    private LinearLayout panel;
    private Button moveButton;
    private Button revealButton;
    private TextView status;
    private TextView debugLog;
    private SharedPreferences prefs;
    private VirtualTouchController virtualTouch;
    private final Handler inputHandler = new Handler(Looper.getMainLooper());
    private final Set<String> heldKeys = new HashSet<>();

    private String home = "https://www.grimfallwow.com/play";
    private String selectedMode = "browser";
    private boolean debugVisible = false;
    private boolean gameView = false;
    private boolean touchMoveEnabled = true;
    private boolean calibrationActive = false;
    private boolean swallowCalibrationRelease = false;
    private int calibrationStep = 0;

    private boolean movePointerActive = false;
    private boolean cameraPointerActive = false;
    private float cameraStickX = 0.0f;
    private float cameraStickY = 0.0f;
    private float cameraPointerX = 0.0f;
    private float cameraPointerY = 0.0f;
    private long lastCameraTick = 0L;

    private final String[] faceKeys = {"1", "2", "3", "4"};
    private final String[] faceNames = {"A", "B", "X", "Y"};
    private final String[] calibrationNames = {
            "movement stick centre",
            "A action button centre",
            "B action button centre",
            "X action button centre",
            "Y action button centre",
            "an empty part of the 3D world for camera dragging"
    };

    private final Runnable cameraTick = new Runnable() {
        @Override
        public void run() {
            tickCamera();
        }
    };

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("companion", MODE_PRIVATE);
        String savedUrl = prefs.getString("url", null);
        if (savedUrl == null
                || "https://www.grimfallwow.com/".equals(savedUrl)) {
            home = "https://www.grimfallwow.com/play";
            prefs.edit().putString("url", home).apply();
        } else {
            home = savedUrl;
        }
        selectedMode = prefs.getString("last_mode", "browser");
        touchMoveEnabled = prefs.getBoolean("touch_move_enabled", true);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        root = new FrameLayout(this);
        setContentView(root);
        showStartMenu();
    }

    private void clearRoot() {
        releaseControllerInputs();
        if (root != null) {
            root.removeAllViews();
        }
        web = null;
        virtualTouch = null;
        panel = null;
        moveButton = null;
        revealButton = null;
        status = null;
        debugLog = null;
    }

    private TextView label(String value, int size, int colour) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(colour);
        return text;
    }

    private Button menuButton(String value, int colour) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackground(bg(colour, 14));
        return button;
    }

    private void showStartMenu() {
        gameView = false;
        calibrationActive = false;
        swallowCalibrationRelease = false;
        clearRoot();

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER);
        screen.setPadding(26, 22, 26, 22);
        screen.setBackgroundColor(0xFF10151F);
        root.addView(screen, new FrameLayout.LayoutParams(-1, -1));

        TextView title = label("GRIMFALL COMPANION", 25, 0xFFE7C77A);
        title.setGravity(Gravity.CENTER);
        screen.addView(title);

        TextView subtitle = label(
                "Choose how you want to enter Azeroth",
                15,
                0xFFCCD5E1
        );
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sub = new LinearLayout.LayoutParams(-1, -2);
        sub.topMargin = 10;
        sub.bottomMargin = 24;
        screen.addView(subtitle, sub);

        Button browser = menuButton(
                "BROWSER CLIENT\nPlay Grimfall in WebView",
                0xFF244D70
        );
        Button local = menuButton(
                "LOCAL CLIENT\n3.3.5a + ConsolePortLK required",
                0xFF66502A
        );
        LinearLayout.LayoutParams browserParams =
                new LinearLayout.LayoutParams(-1, 0, 1);
        browserParams.bottomMargin = 12;
        screen.addView(browser, browserParams);
        screen.addView(local, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView foot = label(
                "Last used: "
                        + (selectedMode.equals("local")
                        ? "Local Client"
                        : "Browser Client")
                        + " · Switch modes any time",
                11,
                0xFF8C9BAE
        );
        foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footParams =
                new LinearLayout.LayoutParams(-1, -2);
        footParams.topMargin = 18;
        screen.addView(foot, footParams);

        browser.setOnClickListener(view -> {
            selectedMode = "browser";
            prefs.edit().putString("last_mode", selectedMode).apply();
            launchBrowser();
        });
        local.setOnClickListener(view -> {
            selectedMode = "local";
            prefs.edit().putString("last_mode", selectedMode).apply();
            showLocalMode();
        });
    }

    private void showLocalMode() {
        gameView = false;
        clearRoot();

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(24, 22, 24, 22);
        screen.setGravity(Gravity.CENTER_VERTICAL);
        screen.setBackgroundColor(0xFF10151F);
        root.addView(screen, new FrameLayout.LayoutParams(-1, -1));

        screen.addView(label("LOCAL CLIENT", 24, 0xFFE7C77A));
        TextView info = label(
                "Local mode currently provides the ConsolePortLK installer only. "
                        + "It does not yet include the 3.3.5a game runtime. "
                        + "You must supply your own compatible client.",
                14,
                0xFFE0E6ED
        );
        info.setPadding(0, 18, 0, 20);
        screen.addView(info);

        Button install = menuButton(
                "INSTALL / REPAIR CONSOLEPORTLK",
                0xFF66502A
        );
        screen.addView(install);
        install.setOnClickListener(view -> chooseClientFolder());

        Button browser = menuButton("Switch to Browser Client", 0xFF244D70);
        Button back = menuButton("Back to mode selection", 0xFF343D4B);
        screen.addView(browser);
        screen.addView(back);
        browser.setOnClickListener(view -> {
            selectedMode = "browser";
            prefs.edit().putString("last_mode", selectedMode).apply();
            launchBrowser();
        });
        back.setOnClickListener(view -> showStartMenu());
    }

    private void chooseClientFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        startActivityForResult(intent, PICK_WOW_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_WOW_FOLDER
                && resultCode == RESULT_OK
                && data != null) {
            Uri uri = data.getData();
            int flags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                getContentResolver().takePersistableUriPermission(uri, flags);
            } catch (Exception ignored) {
                // The picker grant may already be persisted.
            }

            new AlertDialog.Builder(this)
                    .setTitle("Install ConsolePortLK?")
                    .setMessage(
                            "Select the 3.3.5a client root containing "
                                    + "Interface/AddOns. Existing ConsolePort folders "
                                    + "will be backed up before repair; other addons "
                                    + "will be left alone."
                    )
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton(
                            "Install / Repair",
                            (dialog, which) ->
                                    ConsolePortInstaller.install(this, uri)
                    )
                    .show();
        }
    }

    private void launchBrowser() {
        gameView = false;
        clearRoot();
        gameView = true;

        web = new WebView(this);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setBackgroundColor(Color.BLACK);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(
                    WebView view,
                    String url,
                    android.graphics.Bitmap favicon
            ) {
                setStatus("Loading Grimfall: " + url);
            }

            @Override
            public void onPageFinished(
                    WebView view,
                    String url
            ) {
                setStatus(
                        "Loaded. Sign in, enter the world, then calibrate controls."
                );
                web.requestFocus();
                logDebug("Page loaded: " + url);
            }

            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error
            ) {
                if (request.isForMainFrame()) {
                    setStatus("Page error: " + error.getDescription());
                }
            }
        });

        virtualTouch = new VirtualTouchController(web);
        web.setOnTouchListener((view, event) -> {
            if (calibrationActive) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    captureCalibrationPoint(event.getX(), event.getY());
                }
                return true;
            }
            if (swallowCalibrationRelease) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP
                        || event.getActionMasked()
                        == MotionEvent.ACTION_CANCEL) {
                    swallowCalibrationRelease = false;
                }
                return true;
            }
            return false;
        });

        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        buildOverlay();
        web.loadUrl(home);
        web.requestFocus();
    }

    private void buildOverlay() {
        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(8, 6, 8, 6);
        panel.setBackground(bg(0xDD141D29, 14));

        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                -2,
                -2,
                Gravity.TOP | Gravity.RIGHT
        );
        panelParams.setMargins(8, 8, 8, 8);
        root.addView(panel, panelParams);

        TextView heading = label("GRIMFALL · CONTROLLER BRIDGE", 11, Color.WHITE);
        panel.addView(heading);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        panel.addView(controls);

        Button calibrate = menuButton("Calibrate", 0xFF66502A);
        controls.addView(calibrate);
        calibrate.setOnClickListener(view -> beginCalibration());

        moveButton = menuButton(
                touchMoveEnabled ? "Move: TOUCH" : "Move: KEYS",
                0xFF244D70
        );
        controls.addView(moveButton);
        moveButton.setOnClickListener(view -> toggleMoveMode());

        Button debug = menuButton("Debug", 0xFF343D4B);
        controls.addView(debug);
        debug.setOnClickListener(view -> toggleDebug());

        Button hide = menuButton("Hide", 0xFF343D4B);
        controls.addView(hide);
        hide.setOnClickListener(view -> {
            panel.setVisibility(View.GONE);
            revealButton.setVisibility(View.VISIBLE);
        });

        LinearLayout keys = new LinearLayout(this);
        keys.setOrientation(LinearLayout.HORIZONTAL);
        panel.addView(keys);
        String[] keyList = {"1", "2", "3", "4", "Q", "E"};
        for (String key : keyList) {
            Button button = menuButton(key, 0xFF263748);
            button.setTextSize(11);
            LinearLayout.LayoutParams keyParams =
                    new LinearLayout.LayoutParams(dp(44), dp(40));
            keyParams.rightMargin = dp(3);
            keys.addView(button, keyParams);
            button.setOnClickListener(view -> tapKey(key));
        }

        status = label(
                "Ready · touch sticks need calibration on the game screen",
                10,
                0xFFB8C9D9
        );
        status.setMaxWidth(dp(360));
        panel.addView(status);

        debugLog = label("", 9, 0xFF8DD6B4);
        debugLog.setMaxWidth(dp(360));
        debugLog.setMaxLines(4);
        debugLog.setVisibility(View.GONE);
        panel.addView(debugLog);

        revealButton = menuButton("GC", 0xDD244D70);
        FrameLayout.LayoutParams revealParams = new FrameLayout.LayoutParams(
                dp(48),
                dp(42),
                Gravity.TOP | Gravity.RIGHT
        );
        revealParams.setMargins(8, 8, 8, 8);
        root.addView(revealButton, revealParams);
        revealButton.setVisibility(View.GONE);
        revealButton.setOnClickListener(view -> {
            revealButton.setVisibility(View.GONE);
            panel.setVisibility(View.VISIBLE);
        });

        Button modes = menuButton("Modes", 0xFF343D4B);
        FrameLayout.LayoutParams modeParams = new FrameLayout.LayoutParams(
                dp(70),
                dp(42),
                Gravity.TOP | Gravity.LEFT
        );
        modeParams.setMargins(8, 8, 8, 8);
        root.addView(modes, modeParams);
        modes.setOnClickListener(view -> showStartMenu());
    }

    private void beginCalibration() {
        if (web == null || web.getWidth() <= 0 || web.getHeight() <= 0) {
            setStatus("Wait for the Grimfall Play page to finish loading.");
            return;
        }
        calibrationStep = 0;
        calibrationActive = true;
        swallowCalibrationRelease = false;
        setStatus(
                "CALIBRATION 1/6: tap the " + calibrationNames[calibrationStep]
        );
        logDebug("Calibration started.");
    }

    private void captureCalibrationPoint(float x, float y) {
        String key;
        if (calibrationStep == 0) {
            key = "move";
        } else if (calibrationStep >= 1 && calibrationStep <= 4) {
            key = "action_" + faceNames[calibrationStep - 1].toLowerCase();
        } else {
            key = "camera";
        }

        savePoint(key, x, y);
        logDebug("Calibrated " + key + " at "
                + Math.round(x) + "," + Math.round(y));
        calibrationStep++;

        if (calibrationStep < calibrationNames.length) {
            setStatus(
                    "CALIBRATION " + (calibrationStep + 1) + "/6: tap the "
                            + calibrationNames[calibrationStep]
            );
        } else {
            calibrationActive = false;
            swallowCalibrationRelease = true;
            setStatus(
                    "Calibrated. Left stick moves, right stick looks, "
                            + "A/B/X/Y tap the four saved actions."
            );
            logDebug("Calibration complete.");
        }
    }

    private void savePoint(String key, float x, float y) {
        if (web == null || web.getWidth() <= 0 || web.getHeight() <= 0) {
            return;
        }
        float nx = clamp(x / web.getWidth(), 0.0f, 1.0f);
        float ny = clamp(y / web.getHeight(), 0.0f, 1.0f);
        prefs.edit()
                .putFloat(key + "_x", nx)
                .putFloat(key + "_y", ny)
                .apply();
    }

    private PointF touchPoint(
            String key,
            float defaultX,
            float defaultY
    ) {
        if (web == null || web.getWidth() <= 0 || web.getHeight() <= 0) {
            return null;
        }
        float nx = prefs.getFloat(key + "_x", defaultX);
        float ny = prefs.getFloat(key + "_y", defaultY);
        return new PointF(nx * web.getWidth(), ny * web.getHeight());
    }

    private PointF actionPoint(int index) {
        if (index < 0 || index >= faceNames.length || web == null) {
            return null;
        }
        String key = "action_" + faceNames[index].toLowerCase();
        if (!prefs.contains(key + "_x") || !prefs.contains(key + "_y")) {
            return null;
        }
        return touchPoint(key, 0.5f, 0.5f);
    }

    private void tapAction(int index) {
        PointF point = actionPoint(index);
        if (point == null || virtualTouch == null) {
            tapKey(faceKeys[index]);
            return;
        }

        int pointerId = ACTION_POINTER_BASE + index;
        if (virtualTouch.isPointerDown(pointerId)) {
            virtualTouch.pointerUp(pointerId);
        }
        virtualTouch.pointerDown(pointerId, point.x, point.y);
        setStatus("Action " + faceNames[index] + " · touch tap");
        inputHandler.postDelayed(
                () -> {
                    if (virtualTouch != null) {
                        virtualTouch.pointerUp(pointerId);
                    }
                },
                65L
        );
    }

    private void toggleMoveMode() {
        touchMoveEnabled = !touchMoveEnabled;
        prefs.edit().putBoolean("touch_move_enabled", touchMoveEnabled).apply();
        if (virtualTouch != null && movePointerActive) {
            virtualTouch.pointerUp(MOVE_POINTER_ID);
        }
        movePointerActive = false;
        setKeyPressed("W", false);
        setKeyPressed("A", false);
        setKeyPressed("S", false);
        setKeyPressed("D", false);
        if (moveButton != null) {
            moveButton.setText(
                    touchMoveEnabled ? "Move: TOUCH" : "Move: KEYS"
            );
        }
        setStatus(
                touchMoveEnabled
                        ? "Left stick: calibrated mobile movement pad"
                        : "Left stick: WASD keyboard movement"
        );
    }

    private void updateMovementStick(float x, float y) {
        float[] value = applyDeadzone(x, y);
        float magnitude = (float) Math.sqrt(
                value[0] * value[0] + value[1] * value[1]
        );

        if (!touchMoveEnabled) {
            if (movePointerActive && virtualTouch != null) {
                virtualTouch.pointerUp(MOVE_POINTER_ID);
            }
            movePointerActive = false;
            setKeyPressed("W", value[1] < -0.01f);
            setKeyPressed("S", value[1] > 0.01f);
            setKeyPressed("A", value[0] < -0.01f);
            setKeyPressed("D", value[0] > 0.01f);
            return;
        }

        if (magnitude > 0.01f) {
            PointF centre = touchPoint("move", 0.13f, 0.82f);
            if (centre == null || virtualTouch == null) {
                return;
            }
            if (!movePointerActive) {
                virtualTouch.pointerDown(
                        MOVE_POINTER_ID,
                        centre.x,
                        centre.y
                );
                movePointerActive = true;
            }
            float radius = dp(52);
            virtualTouch.pointerMove(
                    MOVE_POINTER_ID,
                    centre.x + value[0] * radius,
                    centre.y + value[1] * radius
            );
        } else if (movePointerActive && virtualTouch != null) {
            virtualTouch.pointerUp(MOVE_POINTER_ID);
            movePointerActive = false;
        }
    }

    private void updateCameraStick(float x, float y) {
        float[] value = applyDeadzone(x, y);
        float magnitude = (float) Math.sqrt(
                value[0] * value[0] + value[1] * value[1]
        );

        if (magnitude > 0.01f) {
            cameraStickX = value[0];
            cameraStickY = value[1];

            if (!cameraPointerActive) {
                PointF anchor = touchPoint("camera", 0.69f, 0.58f);
                if (anchor == null || virtualTouch == null) {
                    return;
                }
                cameraPointerX = anchor.x;
                cameraPointerY = anchor.y;
                virtualTouch.pointerDown(
                        CAMERA_POINTER_ID,
                        cameraPointerX,
                        cameraPointerY
                );
                cameraPointerActive = true;
                lastCameraTick = SystemClock.uptimeMillis();
                inputHandler.removeCallbacks(cameraTick);
                inputHandler.post(cameraTick);
                setStatus("Right stick: camera gesture");
            }
        } else {
            cameraStickX = 0.0f;
            cameraStickY = 0.0f;
            stopCameraPointer();
        }
    }

    private void tickCamera() {
        if (!cameraPointerActive || web == null || virtualTouch == null) {
            return;
        }

        long now = SystemClock.uptimeMillis();
        float deltaSeconds = Math.min(
                0.05f,
                Math.max(0.0f, (now - lastCameraTick) / 1000.0f)
        );
        lastCameraTick = now;

        // Integrate stick velocity into a continuous finger drag. A fixed
        // offset would move the virtual finger back over the same path and
        // cancel the camera rotation on many touch-driven clients.
        float speed = dp(560);
        cameraPointerX += cameraStickX * speed * deltaSeconds;
        cameraPointerY += cameraStickY * speed * deltaSeconds;

        float margin = dp(28);
        cameraPointerX = clamp(
                cameraPointerX,
                margin,
                Math.max(margin, web.getWidth() - margin)
        );
        cameraPointerY = clamp(
                cameraPointerY,
                margin,
                Math.max(margin, web.getHeight() - margin)
        );

        virtualTouch.pointerMove(
                CAMERA_POINTER_ID,
                cameraPointerX,
                cameraPointerY
        );
        inputHandler.postDelayed(cameraTick, 16L);
    }

    private void stopCameraPointer() {
        cameraStickX = 0.0f;
        cameraStickY = 0.0f;
        inputHandler.removeCallbacks(cameraTick);
        if (cameraPointerActive && virtualTouch != null) {
            virtualTouch.pointerUp(CAMERA_POINTER_ID);
        }
        cameraPointerActive = false;
    }

    private float[] applyDeadzone(float x, float y) {
        float magnitude = (float) Math.sqrt(x * x + y * y);
        if (magnitude <= STICK_DEADZONE) {
            return new float[]{0.0f, 0.0f};
        }
        float scaled = Math.min(
                1.0f,
                (magnitude - STICK_DEADZONE) / (1.0f - STICK_DEADZONE)
        );
        float factor = scaled / magnitude;
        return new float[]{x * factor, y * factor};
    }

    private float axisValue(
            MotionEvent event,
            int preferredAxis,
            int fallbackAxis
    ) {
        InputDevice device = event.getDevice();
        if (device != null
                && device.getMotionRange(preferredAxis) == null
                && device.getMotionRange(fallbackAxis) != null) {
            return event.getAxisValue(fallbackAxis);
        }
        return event.getAxisValue(preferredAxis);
    }

    private boolean handleControllerMotion(MotionEvent event) {
        if (!gameView
                || event.getActionMasked() != MotionEvent.ACTION_MOVE
                || (event.getSource() & InputDevice.SOURCE_JOYSTICK)
                != InputDevice.SOURCE_JOYSTICK) {
            return false;
        }

        float leftX = event.getAxisValue(MotionEvent.AXIS_X);
        float leftY = event.getAxisValue(MotionEvent.AXIS_Y);
        float rightX = axisValue(
                event,
                MotionEvent.AXIS_Z,
                MotionEvent.AXIS_RX
        );
        float rightY = axisValue(
                event,
                MotionEvent.AXIS_RZ,
                MotionEvent.AXIS_RY
        );

        updateMovementStick(leftX, leftY);
        updateCameraStick(rightX, rightY);

        float leftTrigger = Math.max(
                0.0f,
                axisValue(
                        event,
                        MotionEvent.AXIS_LTRIGGER,
                        MotionEvent.AXIS_BRAKE
                )
        );
        float rightTrigger = Math.max(
                0.0f,
                axisValue(
                        event,
                        MotionEvent.AXIS_RTRIGGER,
                        MotionEvent.AXIS_GAS
                )
        );
        setKeyPressed("Shift", leftTrigger > 0.55f);
        setKeyPressed("Control", rightTrigger > 0.55f);

        float hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        setKeyPressed("ArrowLeft", hatX < -0.5f);
        setKeyPressed("ArrowRight", hatX > 0.5f);
        setKeyPressed("ArrowUp", hatY < -0.5f);
        setKeyPressed("ArrowDown", hatY > 0.5f);

        if (debugVisible) {
            logDebug(String.format(
                    java.util.Locale.US,
                    "L %.2f,%.2f  R %.2f,%.2f  T %.2f,%.2f",
                    leftX,
                    leftY,
                    rightX,
                    rightY,
                    leftTrigger,
                    rightTrigger
            ));
        }
        return true;
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (handleControllerMotion(event)) {
            return true;
        }
        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (gameView && isControllerKey(event)) {
            int action = event.getAction();
            if (action != KeyEvent.ACTION_DOWN
                    && action != KeyEvent.ACTION_UP) {
                return super.dispatchKeyEvent(event);
            }

            boolean pressed = action == KeyEvent.ACTION_DOWN;
            int keyCode = event.getKeyCode();
            int actionIndex = faceActionIndex(keyCode);

            if (actionIndex >= 0) {
                if (pressed) {
                    if (event.getRepeatCount() == 0) {
                        tapAction(actionIndex);
                    }
                }
                return true;
            }

            String key = controllerKey(keyCode);
            if (key != null) {
                if (pressed && event.getRepeatCount() > 0) {
                    return true;
                }
                setKeyPressed(key, pressed);
                if (pressed) {
                    setStatus("Controller key: " + key);
                    logDebug("Button down: " + key);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isControllerKey(KeyEvent event) {
        return event.isFromSource(InputDevice.SOURCE_GAMEPAD)
                || event.isFromSource(InputDevice.SOURCE_DPAD);
    }

    private int faceActionIndex(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
                return 0;
            case KeyEvent.KEYCODE_BUTTON_B:
                return 1;
            case KeyEvent.KEYCODE_BUTTON_X:
                return 2;
            case KeyEvent.KEYCODE_BUTTON_Y:
                return 3;
            default:
                return -1;
        }
    }

    private String controllerKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_L1:
                return "Q";
            case KeyEvent.KEYCODE_BUTTON_R1:
                return "E";
            case KeyEvent.KEYCODE_BUTTON_L2:
                return "Shift";
            case KeyEvent.KEYCODE_BUTTON_R2:
                return "Control";
            case KeyEvent.KEYCODE_BUTTON_START:
                return "Escape";
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                return "Tab";
            case KeyEvent.KEYCODE_DPAD_UP:
                return "ArrowUp";
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return "ArrowDown";
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return "ArrowLeft";
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return "ArrowRight";
            default:
                return null;
        }
    }

    private void setKeyPressed(String key, boolean pressed) {
        if (web == null) {
            return;
        }
        if (pressed) {
            if (heldKeys.add(key)) {
                sendKey(key, true);
            }
        } else if (heldKeys.remove(key)) {
            sendKey(key, false);
        }
    }

    private void tapKey(String key) {
        setKeyPressed(key, true);
        setKeyPressed(key, false);
        web.requestFocus();
        setStatus("Keyboard tap: " + key);
        logDebug("Keyboard tap: " + key);
    }

    private String keyboardCode(String key) {
        if ("Shift".equals(key)) {
            return "ShiftLeft";
        }
        if ("Control".equals(key)) {
            return "ControlLeft";
        }
        if ("Escape".equals(key)) {
            return "Escape";
        }
        if ("Tab".equals(key)) {
            return "Tab";
        }
        if (key.startsWith("Arrow")) {
            return key;
        }
        if (key.length() == 1 && Character.isLetter(key.charAt(0))) {
            return "Key" + key.toUpperCase();
        }
        if (key.length() == 1 && Character.isDigit(key.charAt(0))) {
            return "Digit" + key;
        }
        return "";
    }

    private int legacyKeyCode(String key) {
        if ("Shift".equals(key)) return 16;
        if ("Control".equals(key)) return 17;
        if ("Escape".equals(key)) return 27;
        if ("Tab".equals(key)) return 9;
        if ("ArrowLeft".equals(key)) return 37;
        if ("ArrowUp".equals(key)) return 38;
        if ("ArrowRight".equals(key)) return 39;
        if ("ArrowDown".equals(key)) return 40;
        return key.length() == 1 ? key.toUpperCase().charAt(0) : 0;
    }

    private String jsQuote(String value) {
        return "'"
                + value.replace("\\", "\\\\").replace("'", "\\'")
                + "'";
    }

    private void sendKey(String key, boolean down) {
        if (web == null) {
            return;
        }
        String type = down ? "keydown" : "keyup";
        String code = keyboardCode(key);
        int legacyCode = legacyKeyCode(key);

        String js = "(()=>{"
                + "const init={key:" + jsQuote(key)
                + ",code:" + jsQuote(code)
                + ",keyCode:" + legacyCode
                + ",which:" + legacyCode
                + ",bubbles:true,cancelable:true,composed:true};"
                + "const target=document.activeElement||document.body||document;"
                + "target.dispatchEvent(new KeyboardEvent("
                + jsQuote(type) + ",init));"
                + "})();";
        web.evaluateJavascript(js, null);
    }

    private void releaseControllerInputs() {
        inputHandler.removeCallbacks(cameraTick);
        if (virtualTouch != null) {
            virtualTouch.releaseAll();
        }
        movePointerActive = false;
        cameraPointerActive = false;
        cameraStickX = 0.0f;
        cameraStickY = 0.0f;

        ArrayList<String> keys = new ArrayList<>(heldKeys);
        for (String key : keys) {
            setKeyPressed(key, false);
        }
    }

    private GradientDrawable bg(int colour, int radiusDp) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(colour);
        shape.setCornerRadius(dp(radiusDp));
        return shape;
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }

    private float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private void setStatus(String value) {
        if (status != null) {
            status.setText(value);
        }
    }

    private void toggleDebug() {
        debugVisible = !debugVisible;
        if (debugLog != null) {
            debugLog.setVisibility(
                    debugVisible ? View.VISIBLE : View.GONE
            );
        }
        setStatus(
                debugVisible
                        ? "Diagnostics enabled · move both sticks to inspect axes"
                        : "Diagnostics disabled"
        );
    }

    private void logDebug(String message) {
        if (!debugVisible || debugLog == null) {
            return;
        }
        String current = debugLog.getText().toString();
        if (current.length() > 800) {
            current = current.substring(current.length() - 600);
        }
        debugLog.setText(current + "\n" + message);
    }

    private void showUrlDialog() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(home);

        new AlertDialog.Builder(this)
                .setTitle("Browser client URL")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setNeutralButton(
                        "Grimfall Play",
                        (dialog, which) -> {
                            home = "https://www.grimfallwow.com/play";
                            prefs.edit().putString("url", home).apply();
                            web.loadUrl(home);
                        }
                )
                .setPositiveButton(
                        "Load",
                        (dialog, which) -> {
                            String candidate = input.getText()
                                    .toString()
                                    .trim();
                            if (candidate.startsWith("https://")
                                    || candidate.startsWith("http://")) {
                                home = candidate;
                                prefs.edit().putString("url", home).apply();
                                web.loadUrl(home);
                            } else {
                                Toast.makeText(
                                        this,
                                        "Enter a valid http(s) URL",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    @Override
    protected void onPause() {
        releaseControllerInputs();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releaseControllerInputs();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (calibrationActive) {
            calibrationActive = false;
            swallowCalibrationRelease = false;
            setStatus("Calibration cancelled.");
        } else if (gameView && web != null && web.canGoBack()) {
            web.goBack();
        } else if (gameView) {
            showStartMenu();
        } else {
            super.onBackPressed();
        }
    }
}
