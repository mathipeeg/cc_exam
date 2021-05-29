package com.example.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class GameEngine extends Activity implements Runnable, TouchHandler, SensorEventListener
{

    private Thread mainLoopThread;
    private com.example.testapp.State state = com.example.testapp.State.Paused;
    private List<com.example.testapp.State> stateChanges = new ArrayList<>();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas = null;
    private com.example.testapp.Screen screen = null;
    private Bitmap offScreenSurface = null;
    private TouchHandler touchHandler;
    private com.example.testapp.TouchEventPool touchEventPool = new com.example.testapp.TouchEventPool();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();
    private List<TouchEvent> touchEventCopied = new ArrayList<>();
    private float[] accelerometer = new float[3];   //the hold the g-forces in 3 dimensions x, y, z
    private SoundPool soundPool = new SoundPool.Builder().setMaxStreams(20).build();
    private int framesPerSecond = 0;
    long currentTime = 0;
    long lastTime = 0;
    Paint paint = new Paint();
    public Music music;
    public boolean volUp = false;
    public boolean volDown = false;




    public abstract com.example.testapp.Screen createStartScreen();
    public void setScreen(com.example.testapp.Screen screen)
    {
        if (this.screen != null)
        {
            this.screen.dispose();
        }

        this.screen = screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceHolder = surfaceView.getHolder();
        screen = createStartScreen();




        touchHandler = new MultiTouchHandler(surfaceView, touchEventBuffer, touchEventPool);
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


    }

    public int getFrameBufferWidth()
    {
        return offScreenSurface.getWidth();
    }

    public int getFrameBufferHeight()
    {
        return  offScreenSurface.getHeight();
    }

    public void setOffScreenSurface(int width, int height)
    {
        if (offScreenSurface != null) offScreenSurface.recycle();

        offScreenSurface = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(offScreenSurface);

    }

    public Bitmap loadBitmap(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap = null;

        try
        {
            in = getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
            {
                throw new RuntimeException("Couldn't load bitmap from file: " + fileName);
            }
            return bitmap;
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("Couldn't load bitmap from assets: " + fileName);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }catch (IOException ioe)
                {
                    throw new RuntimeException("Couldn't close file: " + fileName);
                } // 2nd try catch
            } //If statement
        }//1st try/catch/finally
    }

    public void clearFrameBuffer(int color)
    {
        canvas.drawColor(color);
    }

    public void drawBitmap(Bitmap bitmap, int x, int y)
    {
        if (canvas != null)
        {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    Rect src = new Rect();
    Rect dst = new Rect();
    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY,
                           int srcWidth, int srcHeight)
    {
        if (canvas != null)
        {

            src.left = srcX;
            src.top = srcY;
            src.right = srcX + srcWidth;
            src.bottom = srcY + srcHeight;

            dst.left = x;
            dst.top = y;
            dst.right = x + srcWidth;
            dst.bottom = y + srcHeight;
            canvas.drawBitmap(bitmap, src, dst, null);
        }
    }

    public Sound loadSound(String filename)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(filename);
            int soundId = this.soundPool.load(assetFileDescriptor,0);
            return new Sound(soundPool, soundId);

        } catch (IOException e)
        {
            throw new RuntimeException("Could not load soundfile: " + filename);
        }
    }

    public Music loadMusic(String filename)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(filename);
            return new Music(assetFileDescriptor);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Couldn't load the file: " + filename);
        }
    }

    public Typeface loadFont(String fileName)
    {
        Typeface font = Typeface.createFromAsset(getAssets(), fileName);
        if (font == null)
        {
            throw new RuntimeException("Couldn't load the font from file " + fileName);

        }
        return font;
    }

    public void drawText(Typeface font, String text, int x, int y, int color, int size)
    {
        paint.setTypeface(font);
        paint.setColor(color);
        paint.setTextSize(size);
        canvas.drawText(text, x, y, paint);
    }

    public boolean isTouchDown(int pointer)
    {
        return touchHandler.isTouchDown(pointer);
    }

    public List<TouchEvent> getTouchEvents()
    {
        return touchEventCopied;
    }

    public int getTouchX(int pointer)
    {
        int scaledX = 0;
        scaledX = (int)((float)touchHandler.getTouchX(pointer)*(float)offScreenSurface.getWidth()
                /(float) surfaceView.getWidth());
        return scaledX;
    }

    public int getTouchY(int pointer)
    {
        int scaledY = 0;
        scaledY = (int)((float)touchHandler.getTouchY(pointer)*(float)offScreenSurface.getHeight()
                /(float) surfaceView.getHeight());
        return scaledY;
    }

    public int getRawTouchX(int pointer)
    {
        int scaledX = 0;
        scaledX = (int)((float)touchHandler.getRawTouchX(pointer)*(float)offScreenSurface.getWidth()
                /(float) surfaceView.getWidth());
        return scaledX;
    }

    public int getRawTouchY(int pointer)
    {
        int scaledY = 0;
        scaledY = (int)((float)touchHandler.getRawTouchY(pointer)*(float)offScreenSurface.getHeight()
                /(float) surfaceView.getHeight());
        return scaledY;
    }

    public float[] getAccelerometer()
    {
        return accelerometer;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void  onSensorChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, accelerometer, 0, 3); //Copy from
        //event.values array to accelerometer array from SRC position 0 to DEST position, first 3
    }

    private void fillEvents()
    {
        synchronized (touchEventBuffer)
        {
            int stop = touchEventBuffer.size();
            for (int i = 0; i < stop; i++)
            {
                touchEventCopied.add(touchEventBuffer.get(i));
            }
            touchEventBuffer.clear();
        }
    }

    public void freeEvents()
    {
        synchronized (touchEventCopied)
        {
            int stop = touchEventCopied.size();
            for (int i = 0; i < stop; i++)
            {
                touchEventPool.free(touchEventCopied.get(i));
            }
            touchEventCopied.clear();
        }
    }

    public int getFramesPerSecond()
    {
        return framesPerSecond;
    }

    public void run()
    {
        int frames = 0;
        long startTime = System.nanoTime();
        while (true)
        {
            synchronized (stateChanges)
            {
                for (int i = 0; i < stateChanges.size(); i++)
                {
                    state = stateChanges.get(i);
                    if (state == com.example.testapp.State.Disposed)
                    {
                        Log.d("GameEngine", "state changed to Disposed");
                        return;
                    }
                    if(state == com.example.testapp.State.Paused)
                    {
                        Log.d("GameEngine", "state changed to Paused");
                        return;
                    }
                    if(state == com.example.testapp.State.Resumed)
                    {
                        Log.d("GameEngine", "state changed to Resumed");
                        state = com.example.testapp.State.Running;
                    }
                } //End of the for loop
                stateChanges.clear();

                if (state == com.example.testapp.State.Running)
                {
                    //Log.d("GameEngine", "State is running");
                    if (!surfaceHolder.getSurface().isValid())
                    {
                        continue;
                    }
                    //Log.d("GameEngine", "Trying to get Canvas");


                    //All the drawing code should happens here
                    Canvas canvas = surfaceHolder.lockCanvas();
                    fillEvents();
                    currentTime = System.nanoTime();

                    if (screen != null) screen.update((currentTime - lastTime)/1000000000.0f);
                    lastTime = currentTime;

                    freeEvents();

                    //Logic screen
                    src.left = 0;
                    src.top = 0;
                    src.right = offScreenSurface.getWidth(); //Or maybe -1
                    src.bottom = offScreenSurface.getHeight();

                    //Screen
                    dst.left = 0;
                    dst.top = 0;
                    dst.right = surfaceView.getWidth();
                    dst.bottom = surfaceView.getHeight();

                    //Draw offScreenSurface
                    canvas.drawBitmap(offScreenSurface, src, dst, null);

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                frames++; // I have just drawn a new frame
                if(System.nanoTime() - startTime > 1000000000)
                {
                    framesPerSecond = frames;
                    frames = 0;
                    startTime = System.nanoTime();

                }
                touchEventCopied.clear();
            }//End of synchronised
        }//End of while loop
    }


    public void onPause()
    {
        super.onPause();
        synchronized (stateChanges)
        {
            if (!isFinishing())
            {
                stateChanges.add(stateChanges.size(), com.example.testapp.State.Paused);
            }
            else
            {
                stateChanges.add(stateChanges.size(), com.example.testapp.State.Disposed);
            }
        }
        if (isFinishing())
        {
            ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
            soundPool.release();
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    volUp = true;
                }
                if (action == KeyEvent.ACTION_UP) {
                    volUp = false;
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    volDown = true;
                }
                if (action == KeyEvent.ACTION_UP) {
                    volDown = false;
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    public void onResume()
    {
        super.onResume();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //setOffScreenSurface(480, 320);
            setOffScreenSurface(1920, 1080);
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            //setOffScreenSurface(320, 480);
            setOffScreenSurface(1080, 1920);
        }

        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        synchronized (stateChanges)
        {
            stateChanges.add(stateChanges.size(), com.example.testapp.State.Resumed);

        }
    }



}
