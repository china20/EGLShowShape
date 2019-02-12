package com.wr.external.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 资源的操作辅助类
 */
public final class AssetsUtil {

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 根据资源名称获取图像资源Bitmap对象
     * @author WR
     * @param context 资源上下文
     * @param name 图像文件名
     * @return 资源目录下的图像
     */
    public static Bitmap loadImage(Context context, String name) {
        Bitmap bitmap = null;
        InputStream inStrm = loadStream(context, name);

        if (null != inStrm) {
            bitmap = BitmapFactory.decodeStream(inStrm);
            closeInputStream(inStrm);
        }

        return bitmap;
    }

    public static String loadString(Context context, String name) {
        String source = "";
        InputStream inStrm = loadStream(context, name);

        if (null != inStrm) {
            try {
                int size = inStrm.available();
                byte[] bytes = new byte[size];
                if (size == inStrm.read(bytes)) {
                    source = new String(bytes, Charset.defaultCharset());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeInputStream(inStrm);
            }
        }
        return source;
    }

    private static InputStream loadStream(Context context, String name) {
        Bitmap bitmap = null;
        // 获取资源对象（和res平级的assets目录下的资料）
        AssetManager assetMana;
        InputStream inStrm = null;

        if (context == null) {
            assetMana = getContext().getAssets();
        } else {
            assetMana = context.getAssets();
        }

        try {
            inStrm = assetMana.open(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inStrm;
    }

    private static void closeInputStream(InputStream inStrm) {
        if (null != inStrm) {
            try {
                inStrm.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
