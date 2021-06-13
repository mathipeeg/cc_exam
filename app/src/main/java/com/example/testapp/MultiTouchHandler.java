package com.example.testapp;

import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class MultiTouchHandler implements TouchHandler, View.OnTouchListener
{
    private boolean[] isTouched =  new boolean[20]; //Store the first 20 touches
    private int[] touchX = new int[20];
    private int[] touchY = new int[20];
    private int[] touchRawX = new int[20];
    private int[] touchRawY = new int[20];

    private List<TouchEvent> touchEventBuffer;  //Buffer with touch
    private TouchEventPool touchEventPool;  //Pool with  re-usable TouchEvent objects

    public MultiTouchHandler(View view, List<TouchEvent> touchEventBuffer, TouchEventPool touchEventPool)
    {
        view.setOnTouchListener(this);
        this.touchEventBuffer = touchEventBuffer;
        this.touchEventPool = touchEventPool;
    }
    public MultiTouchHandler(View view)
    {
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) // todo: is this just like standard copy stuff? Or do i need to understand it?
    {
        TouchEvent touchEvent = null;
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);


        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchEvent = touchEventPool.obtains();
                touchEvent.type = com.example.testapp.TouchEvent.TouchEventType.Down;
                touchEvent.pointer = pointerId;

                try
                {
                    touchEvent.x = (int)event.getX(pointerId);
                    touchX[pointerId] = touchEvent.x;

                    touchEvent.y = (int)event.getY(pointerId);
                    touchY[pointerId] = touchEvent.y;
                } catch (IllegalArgumentException e)
                {
                    break;
                }

                isTouched[pointerId] = true;

                synchronized (touchEventBuffer)
                {
                    touchEventBuffer.add(touchEvent);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchEvent = touchEventPool.obtains();
                touchEvent.type = com.example.testapp.TouchEvent.TouchEventType.Up;
                touchEvent.pointer = pointerId;

                touchEvent.x = (int)event.getX();
                touchX[pointerId] = touchEvent.x;

                touchEvent.y = (int)event.getY();
                touchY[pointerId] = touchEvent.y;

                isTouched[pointerId] = false;

                synchronized (touchEventBuffer)
                {
                    touchEventBuffer.add(touchEvent);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                synchronized (touchEventBuffer)
                {
                    for (int i = 0; i < pointerCount; i++)
                    {
                        touchEvent = touchEventPool.obtains();
                        touchEvent.type = com.example.testapp.TouchEvent.TouchEventType.Dragged;
                        touchEvent.pointer = pointerId;

                        touchEvent.x = (int)event.getX(i);
                        touchX[pointerId] = touchEvent.x;

                        touchEvent.y = (int)event.getY(i);
                        touchY[pointerId] = touchEvent.y;

                        isTouched[pointerId] = true;

                        touchEventBuffer.add(touchEvent);
                    }
                }
                break;

        }
        return true; //Telling the Android system I did handle this onTouch event!
    }

    @Override
    public boolean isTouchDown(int pointer)
    {
        return isTouched[pointer];
    }

    @Override
    public int getTouchX(int pointer)
    {
        return touchX[pointer];
    }

    @Override
    public int getTouchY(int pointer)
    {
        return touchY[pointer];
    }
    @Override
    public int getRawTouchX(int pointer)
    {
        return touchRawX[pointer];
    }

    @Override
    public int getRawTouchY(int pointer)
    {
        return touchRawY[pointer];
    }
}

