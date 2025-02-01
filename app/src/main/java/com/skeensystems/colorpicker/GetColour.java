package com.skeensystems.colorpicker;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

import kotlin.Triple;

public class GetColour {

    /**
     * Gets colour of the pixel at the centre of the provided ImageProxy
     * @param image image to detect colour of central pixel
     * @return centre colour of image as Triple(R, G, B)
     */
    public static Triple<Double, Double, Double> getColour(ImageProxy image) {
        // Get planes of the ImageProxy
        ImageProxy.PlaneProxy[] planes = image.getPlanes();

        // Calculate centre coordinates
        int centreX = image.getWidth() / 2;
        int centreY = image.getHeight() / 2;

        // First calculate colour in YUV format, then convert to RGB
        // Y
        ByteBuffer yArr = planes[0].getBuffer();
        byte[] yArrByteArray = toByteArray(yArr);
        int yPixelStride = planes[0].getPixelStride();
        int yRowStride = planes[0].getRowStride();

        // U
        ByteBuffer uArr = planes[1].getBuffer();
        byte[] uArrByteArray = toByteArray(uArr);
        int uPixelStride = planes[1].getPixelStride();
        int uRowStride = planes[1].getRowStride();

        // V
        ByteBuffer vArr = planes[2].getBuffer();
        byte[] vArrByteArray = toByteArray(vArr);
        int vPixelStride = planes[2].getPixelStride();
        int vRowStride = planes[2].getRowStride();

        // Calculate YUV values using the previously calculated byte arrays
        int y = yArrByteArray[(centreY * yRowStride + centreX * yPixelStride)] & 255;
        int u = (uArrByteArray[(centreY * uRowStride + centreX * uPixelStride) / 2] & 255) - 128;
        int v = (vArrByteArray[(centreY * vRowStride + centreX * vPixelStride) / 2] & 255) - 128;

        // Convert YUV to RGB
        double r = y + (1.370705 * v);
        double g = y - (0.698001 * v) - (0.337633 * u);
        double b = y + (1.732446 * u);

        // Cap RGB values to avoid values outside of 0-255
        if (r < 0) r = 0;
        else if (r > 255) r = 255;
        if (g < 0) g = 0;
        else if (g > 255) g = 255;
        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        return new Triple<>(r, g, b);
    }

    /**
     * Converts ByteBuffer to byte[]
     * @param buffer ByteBuffer to convert
     * @return buffer as a byte array
     */
    private static byte[] toByteArray(ByteBuffer buffer) {
        // Rewind the buffer to zero
        buffer.rewind();
        byte[] data = new byte[buffer.remaining()];
        // Copy the buffer into a byte array
        buffer.get(data);
        // Return the byte array
        return data;
    }
}
