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
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas = null;
    private Screen screen = null;
    private Bitmap offScreenSurface = null;
    private TouchHandler touchHandler;
    private TouchEventPool touchEventPool = new TouchEventPool();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();
    private List<TouchEvent> touchEventCopied = new ArrayList<>();
    private float[] accelerometer = new float[3];   //the hold the g-forces in 3 dimensions x, y, z
    private SoundPool soundPool = new SoundPool.Builder().setMaxStreams(20).build();
    private int framesPerSecond = 0;
    long currentTime = 0;
    long lastTime = 0;
    Paint paint = new Paint();
    public Music music;

    public abstract Screen createStartScreen();

    public void setScreen(Screen screen)
    {
        if (this.screen != null)
        {
            this.screen.dispose();
        }

        this.screen = screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) // todo: look into onCreate!
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
    } // todo: look into?

    public int getFrameBufferHeight()
    {
        return  offScreenSurface.getHeight();
    } // todo: look into?

    public void setOffScreenSurface(int width, int height) // todo: har at gøre med orientation?
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
                }
            }
        }
    }

    public void clearFrameBuffer(int color) // todo: useless?
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
    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, // todo: exact diff between these two / why not just this one?
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

//    public float[] getAccelerometer()
//    {
//        return accelerometer;
//    }

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
        synchronized (touchEventBuffer) // todo: look into and remember what synchronized is
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
                touchEventPool.free(touchEventCopied.get(i)); // todo: free?
            }
            touchEventCopied.clear();
        }
    }

    public int getFramesPerSecond()
    {
        return framesPerSecond;
    }

    public void run() // todo: ye i'm gonna need to go through this one.
    {
        int frames = 0;
        long startTime = System.nanoTime();
        while (true)
        {
            synchronized (stateChanges) // list with State states
            {
                for (int i = 0; i < stateChanges.size(); i++)
                {
                    state = stateChanges.get(i);
                    if (state == State.Disposed)
                    {
                        Log.d("GameEngine", "state changed to Disposed");
                        return;
                    }
                    if(state == State.Paused)
                    {
                        Log.d("GameEngine", "state changed to Paused");
                        return;
                    }
                    if(state == State.Resumed)
                    {
                        Log.d("GameEngine", "state changed to Resumed");
                        state = State.Running;
                    }
                }
                stateChanges.clear();

                if (state == State.Running)
                {
                    if (!surfaceHolder.getSurface().isValid())
                    {
                        continue;
                    }

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


    public void onPause() // todo: pause working? also wtf?
    {
        super.onPause();
        synchronized (stateChanges)
        {
            if (!isFinishing())
            {
                stateChanges.add(stateChanges.size(), State.Paused);
            }
            else
            {
                stateChanges.add(stateChanges.size(), State.Disposed);
            }
        }
        if (isFinishing())
        {
            ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
            soundPool.release();
        }

    }

    public void onResume() // todo: um... halp
    {
        super.onResume();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setOffScreenSurface(1920, 1080);
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
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
