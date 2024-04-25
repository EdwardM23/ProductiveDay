package com.edward.productiveday.accessibilities

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class KeyInterceptorService: AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                event.packageName
            }
        }
    }

    override fun onInterrupt() {

    }
}