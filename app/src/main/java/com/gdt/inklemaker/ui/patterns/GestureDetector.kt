package com.gdt.inklemaker.ui.patterns

import android.view.GestureDetector
import android.view.MotionEvent

abstract class GestureListeners : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    abstract fun onSingleTapListener(point: Point)
    abstract fun onDoubleTapListener()
    abstract fun onScrollListener(distanceX: Float, distanceY: Float)

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        onSingleTapListener(Point(e.x, e.y))
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleTapListener()
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        onScrollListener(distanceX, distanceY)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        // do nothing
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        // do nothing
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        // do nothing
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        // do nothing
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        // do nothing
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        // do nothing
    }
}