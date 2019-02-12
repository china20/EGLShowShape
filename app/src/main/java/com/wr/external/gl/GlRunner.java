package com.wr.external.gl;

import android.opengl.EGLContext;
import android.opengl.EGLSurface;
import android.util.Log;

/**
 * 完成EGL环境的初始化，使用注意如下几点：
 * 1. 调用initEGLEnv完成EGL基本环境初始化
 * 2. 调用start启动线程等待绘制
 * 3. 调用requestRender触发绘制
 * @author 汪荣
 */
public class GlRunner {

    public static final String TAG = "Wr:GlRunner";

    private Thread mThread;
    private WrGlCore mEGLCore;
    private boolean mLoop = false;
    private int mSignTimes = 0;
    private final Object mLocker = new Object();

    private GlRunner.Render mRender;
    private GlRunner.ReadyRender mReady;

    public interface ReadyRender {
        void onReady(final GlRunner eglRender);
    }

    public interface Render {
        void onPrepare(final GlRunner eglRender);
        void onRender(final GlRunner eglRender);
        void onFinish(final GlRunner eglRender);
    }

    class GlRunnable implements Runnable {
        public void run() {
            runRender();
        }
    }

    public GlRunner() {

    }

    public void stop() {
        synchronized (mLocker) {
            if (null != mThread) {
                try {
                    mLoop = false;
                    mThread.interrupt();
                    mLocker.notifyAll();
                    mThread.join(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int start() {
        if (null == mEGLCore) {
            throw new IllegalStateException("请先调用 initEGLEnv 进行初始化！");
        }
        if (null == mThread) {
            mLoop = true;
            mThread = new Thread(new GlRunnable());
            mThread.start();
        }
        return 0;
    }

    public void setViewport(int x, int y, int wid, int hei) {
        if (null != mEGLCore) {
            mEGLCore.setViewport(x, y, wid, hei);
        }
    }

    public EGLContext getEGLContext() {
        return mEGLCore.getEGLContext();
    }

    public void setRender(GlRunner.Render r) {
        mRender = r;
    }

    public void setReadyRender(GlRunner.ReadyRender r) {
        mReady = r;
    }

    public boolean isValid() {
        return (null != mEGLCore);
    }

    public EGLSurface createSurface(Object surface) {
        return mEGLCore.createSurface(surface);
    }

    public void attachThread(EGLSurface surface) {
        mEGLCore.attachCurrentThread(surface);
    }

    public void releaseSurface(EGLSurface surface) {
        mEGLCore.releaseSurface(surface);
    }

    public void requestRender() {
        synchronized (mLocker) {
            try {
                mLocker.notifyAll();
                mSignTimes++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int init(EGLContext sharedContext) {
        int ret = 0;

        if (null != mEGLCore) {
            return ret;
        }

        mEGLCore = new WrGlCore();
        ret = mEGLCore.initEGLEnv(sharedContext);

        return ret;
    }

    public void swapBuffers() {
        mEGLCore.swapBuffers();
    }

    private int prepareRender() {
        if (null == mRender) {
            return 1;
        }

        mRender.onPrepare(this);

        if (null != mReady) {
            mReady.onReady(this);
        }

        return 0;
    }

    private void runRender() {
        if (0 != prepareRender()) {
            return;
        }

        while (mLoop) {
            synchronized (mLocker) {
                try {
                    if (mSignTimes == 0) {
                        mLocker.wait();
                    } else {
                        Log.i(TAG, "dont need to wait");
                    }
                    --mSignTimes;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if (mLoop) {
                mRender.onRender(this);
            }
        }

        mRender.onFinish(this);

        release();
        mThread = null;

        Log.i(TAG, "GlRender thread is exit!");
    }

    private void release() {
        if (null != mEGLCore) {
            mEGLCore.release();
            mEGLCore = null;
        }
    }
}
