package com.wr.external.gl;

import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.EGL14;

/**
 * 实现EGL核心操作，包括建立EGL环境、创建和释放Surface
 * @author 汪荣
 */
public class WrGlCore {

    public static final EGLSurface NO_SURFACE = EGL14.EGL_NO_SURFACE;
    public static final EGLContext NO_CONTEXT = EGL14.EGL_NO_CONTEXT;

    private EGLDisplay mDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLConfig mConfig;
    private EGLContext mContext = NO_CONTEXT;
    private EGLSurface mSurface = NO_SURFACE;

    public int initEGLEnv(EGLContext sharedContext) {
        final int attribs[] = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };

        int format[] = {0};
        final int eglContextAttributes[] = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};

        int major[] = {0};
        int minor[] = {0};
        int numConfig[] = {0};
        EGLConfig config[] = new EGLConfig[1];

        // 1. 获取到默认的显示设备
        mDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (EGL14.EGL_NO_DISPLAY == mDisplay) {
            return 1;
        }

        // 2. 初始化EGL环境
        if (!EGL14.eglInitialize(mDisplay, major, 0, minor, 0)) {
            return 2;
        }
        // 3. 配置显示设备的相关属性参数
        if (!EGL14.eglChooseConfig(mDisplay, attribs, 0,
                config, 0, 1,
                numConfig, 0)) {
            return 3;
        }
        mConfig = config[0];

        // 4. 创建EGLContext
        mContext = EGL14.eglCreateContext(mDisplay, mConfig, sharedContext, eglContextAttributes, 0);
        if (null == mContext) {
            return 4;
        }

        if (!EGL14.eglGetConfigAttrib(mDisplay, mConfig, EGL14.EGL_NATIVE_VISUAL_ID, format, 0)) {
            release();
            return 5;
        } else {
            return 0;
        }
    }

    public EGLSurface createSurface(Object surface) {
        final int[] attributes = {EGL14.EGL_NONE};
        if (mDisplay == EGL14.EGL_NO_DISPLAY) {
            return WrGlCore.NO_SURFACE;
        }
        // 创建EGL显示的Surface
        return EGL14.eglCreateWindowSurface(mDisplay, mConfig, surface, attributes, 0);
    }

    public void releaseSurface(EGLSurface surface) {
        if (null != mSurface && NO_SURFACE != mSurface) {
            EGL14.eglDestroySurface(mDisplay, surface);
        }
    }

    public void setViewport(int x, int y, int wid, int hei) {
        GLES20.glViewport(x, y, wid, hei);
    }

    public EGLContext getEGLContext() {
        return mContext;
    }

    public void swapBuffers() {
        EGL14.eglSwapBuffers(mDisplay, mSurface);
    }

    public void attachCurrentThread(EGLSurface surface) {
        if (surface != mSurface) {
            if (NO_SURFACE != mSurface) {
                detachCurrentThread();
            }

            mSurface = surface;
            EGL14.eglMakeCurrent(mDisplay, mSurface, mSurface, mContext);
        }
    }

    public void detachCurrentThread() {
        EGL14.eglMakeCurrent(mDisplay, NO_SURFACE, NO_SURFACE, NO_CONTEXT);
    }

    public void release() {
        EGL14.eglMakeCurrent(mDisplay, NO_SURFACE, NO_SURFACE, NO_CONTEXT);
        EGL14.eglDestroyContext(mDisplay, mContext);

        mSurface = NO_SURFACE;
        mContext = NO_CONTEXT;
        mDisplay = EGL14.EGL_NO_DISPLAY;
    }
}
