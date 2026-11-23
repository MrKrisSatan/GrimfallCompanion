package com.mrkrissatan.grimfallcompanion;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.webkit.WebView;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sends a small number of virtual finger contacts through WebView's native
 * touch pipeline. This lets physical sticks operate the browser client's
 * existing mobile touch controls instead of relying on synthetic JS keys.
 */
public final class VirtualTouchController {
    private final WebView target;
    private final LinkedHashMap<Integer, PointF> pointers = new LinkedHashMap<>();
    private long gestureDownTime;

    public VirtualTouchController(WebView target) {
        this.target = target;
    }

    public boolean isPointerDown(int pointerId) {
        return pointers.containsKey(pointerId);
    }

    public void pointerDown(int pointerId, float x, float y) {
        if (target == null || target.getWidth() <= 0 || target.getHeight() <= 0) {
            return;
        }
        if (pointers.containsKey(pointerId)) {
            pointerUp(pointerId);
        }

        boolean firstPointer = pointers.isEmpty();
        pointers.put(pointerId, new PointF(x, y));

        if (firstPointer) {
            gestureDownTime = SystemClock.uptimeMillis();
            dispatch(MotionEvent.ACTION_DOWN);
        } else {
            int newPointerIndex = pointers.size() - 1;
            dispatch(MotionEvent.ACTION_POINTER_DOWN
                    | (newPointerIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
        }
    }

    public void pointerMove(int pointerId, float x, float y) {
        if (!pointers.containsKey(pointerId)) {
            return;
        }
        pointers.put(pointerId, new PointF(x, y));
        dispatch(MotionEvent.ACTION_MOVE);
    }

    public void pointerUp(int pointerId) {
        if (!pointers.containsKey(pointerId)) {
            return;
        }

        int pointerIndex = indexOf(pointerId);
        int action = pointers.size() == 1
                ? MotionEvent.ACTION_UP
                : MotionEvent.ACTION_POINTER_UP
                    | (pointerIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT);

        // ACTION_POINTER_UP includes the pointer being lifted. Remove it only
        // after dispatching the event.
        dispatch(action);
        pointers.remove(pointerId);
        if (pointers.isEmpty()) {
            gestureDownTime = 0L;
        }
    }

    public void releaseAll() {
        while (!pointers.isEmpty()) {
            Integer pointerId = pointers.keySet().iterator().next();
            pointerUp(pointerId);
        }
    }

    private int indexOf(int pointerId) {
        int index = 0;
        for (Integer id : pointers.keySet()) {
            if (id == pointerId) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void dispatch(int action) {
        if (target == null || pointers.isEmpty()) {
            return;
        }

        int count = pointers.size();
        MotionEvent.PointerProperties[] properties =
                new MotionEvent.PointerProperties[count];
        MotionEvent.PointerCoords[] coordinates =
                new MotionEvent.PointerCoords[count];

        int index = 0;
        for (Map.Entry<Integer, PointF> entry : pointers.entrySet()) {
            MotionEvent.PointerProperties property =
                    new MotionEvent.PointerProperties();
            property.id = entry.getKey();
            property.toolType = MotionEvent.TOOL_TYPE_FINGER;
            properties[index] = property;

            MotionEvent.PointerCoords coordinate =
                    new MotionEvent.PointerCoords();
            coordinate.x = entry.getValue().x;
            coordinate.y = entry.getValue().y;
            coordinate.pressure = 1.0f;
            coordinate.size = 1.0f;
            coordinates[index] = coordinate;
            index++;
        }

        long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(
                gestureDownTime,
                now,
                action,
                count,
                properties,
                coordinates,
                0,
                0,
                1.0f,
                1.0f,
                0,
                0,
                InputDevice.SOURCE_TOUCHSCREEN,
                0
        );

        try {
            target.dispatchTouchEvent(event);
        } finally {
            event.recycle();
        }
    }
}
