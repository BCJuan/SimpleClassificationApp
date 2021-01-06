package com.example.simpleclassificationapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

public class Utils {

    public static String writeResults(Map<String, Float> mapResults){
        Map.Entry<String, Float> entryMax = null;
        Map.Entry<String, Float> entryMax1 = null;
        Map.Entry<String, Float> entryMax2 = null;
        for(Map.Entry<String, Float> entry: mapResults.entrySet()){
            if (entryMax == null || entry.getValue().compareTo(entryMax.getValue()) > 0){
                entryMax = entry;
            } else if (entryMax1 == null || entry.getValue().compareTo(entryMax1.getValue()) > 0){
                entryMax1 = entry;
            } else if (entryMax2 == null || entry.getValue().compareTo(entryMax2.getValue()) > 0){
                entryMax2 = entry;
            }
        }
        String result = entryMax.getKey().trim() + " " + entryMax.getValue().toString() + "\n" +
                entryMax1.getKey().trim() + " " + entryMax1.getValue().toString() + "\n" +
                entryMax2.getKey().trim() + " " + entryMax2.getValue().toString() + "\n";
        return result;
    }

    public static int getImageRotation(ImageProxy image){
        int rotation = image.getImageInfo().getRotationDegrees();
        return rotation/90;
    }

    public static Bitmap toBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
