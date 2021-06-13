package com.example.testapp;

public class TouchEvent
{
    public enum TouchEventType
    {
        Down,
        ActionDown,
        ActionUp,
        Up,
        Dragged
    }

    public TouchEventType type;     // the type of the event
    public int x;                   // the x coordinate of the event
    public int y;                   // the y coordinate of the event
    public int rawX;                // the x coordinate of the event
    public int rawY;                // the y coordinate of the event
    public int pointer;             // the pointer id (from the android system) // todo: the hell is pointerId?

}
