package com.ucenm.pm01e70007.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Conversión de imágenes a byte[] (BLOB) y viceversa. */
public final class ImagenUtil {

    private static final int MAX_LADO = 640;

    private ImagenUtil() {}

    public static byte[] bitmapABytes(Bitmap original) {
        if (original == null) return null;
        Bitmap bmp = original;
        int w = original.getWidth();
        int h = original.getHeight();
        int mayor = Math.max(w, h);
        if (mayor > MAX_LADO) {
            float escala = (float) MAX_LADO / mayor;
            bmp = Bitmap.createScaledBitmap(original, Math.round(w * escala), Math.round(h * escala), true);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, out);
        return out.toByteArray();
    }

    public static byte[] uriABytes(ContentResolver cr, Uri uri) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            try (InputStream in = cr.openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, o);
            }
            int sample = 1;
            while (o.outWidth / sample > 1280 || o.outHeight / sample > 1280) sample *= 2;
            o.inJustDecodeBounds = false;
            o.inSampleSize = sample;
            try (InputStream in2 = cr.openInputStream(uri)) {
                Bitmap b = BitmapFactory.decodeStream(in2, null, o);
                return bitmapABytes(b);
            }
        } catch (IOException | SecurityException e) {
            return null;
        }
    }

    public static Bitmap bytesABitmap(byte[] data) {
        if (data == null || data.length == 0) return null;
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /** Muestra la imagen o, si no hay, un ícono por defecto con relleno. */
    public static void mostrar(ImageView iv, byte[] data, int recursoDefecto, int paddingDp) {
        Bitmap b = bytesABitmap(data);
        if (b != null) {
            iv.setPadding(0, 0, 0, 0);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageBitmap(b);
        } else {
            int p = Math.round(paddingDp * iv.getResources().getDisplayMetrics().density);
            iv.setPadding(p, p, p, p);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageResource(recursoDefecto);
        }
    }
}
