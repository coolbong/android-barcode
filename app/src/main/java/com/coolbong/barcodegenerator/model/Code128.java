package com.coolbong.barcodegenerator.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Generates a Code 128 barcode image (Code Set B).
 * Contains logic for encoding text to Code 128 bit pattern
 * and drawing it onto an Android Bitmap.
 * Reference: http://en.wikipedia.org/wiki/Code_128
 */
public class Code128 {

    private static final String TAG = Code128.class.getSimpleName();

    // Start code B is used for generating the barcode
    private static final int CODE_START_B = 104;
    private static final int CODE_STOP = 106;
    private static final int DIVISOR = 103;

    private String data;
    private Context context;

    private int weight = 0;
    private int weightSum = 0;
    private int checkSum = 0;

    /**
     * Constructor needing context for accessing display metrics.
     * @param context Application or activity context
     */
    public Code128(Context context) {
        this.context = context;
    }

    /**
     * Initializes state before encoding.
     */
    public void init() {
        weight = 0;
        weightSum = 0;
        checkSum = 0;
    }

    /**
     * Sets the text data to encode.
     * @param data The text data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the current text data.
     * @return The text data
     */
    public String getData() {
        return data;
    }

    /**
     * Calculates the required buffer size based on the data length.
     * @param dataLen Length of the text data
     * @return An array of bytes acting as the output buffer
     */
    public byte[] initBuffer(int dataLen) {
        int sum = 0;
        // Start code requires 11 units
        sum += 11;
        // Each character requires 11 units
        sum += (dataLen * 11);
        // Checksum requires 11 units
        sum += 11;
        // Stop code requires 13 units
        sum += 13;
        
        return new byte[sum];
    }

    /**
     * Encodes the data into a binary byte array representing bars and spaces.
     * 1 represents a bar, 0 represents a space.
     * @return The encoded byte array, or null if data is missing
     */
    public byte[] encode() {
        if (data == null) {
            return null;
        }

        int len = data.length();
        init();
        byte[] buffer = initBuffer(len);
        int pos = 0;

        // Append Start Code B
        pos += appendData(Code128Constant.CODE_WEIGHT[CODE_START_B], buffer, pos);
        weightSum = CODE_START_B;

        // Append each character and calculate check sum
        for (int i = 0; i < len; i++) {
            weight++;
            char ch = data.charAt(i);

            int index = ch - ' ';
            byte[] chWeight = Code128Constant.CODE_WEIGHT[index];
            pos += appendData(chWeight, buffer, pos);
            
            weightSum += (weight * index);
        }

        // Calculate and append check sum
        checkSum = weightSum % DIVISOR;
        pos += appendData(Code128Constant.CODE_WEIGHT[checkSum], buffer, pos);
        
        // Append Stop Code
        pos += appendData(Code128Constant.CODE_WEIGHT[CODE_STOP], buffer, pos);

        return buffer;
    }

    /**
     * Draws the encoded Code 128 barcode onto a Bitmap with the specified width and height.
     * @param width The target width of the image
     * @param height The target height of the image
     * @return The generated Bitmap containing the barcode, or null if failed
     */
    public Bitmap getBitmap(int width, int height) {
        byte[] code = encode();
        if (code == null) {
            return null;
        }

        float scale = 1.0f;
        if (context != null) {
            scale = context.getResources().getDisplayMetrics().density;
        }

        int textSize = (int) (16 * scale); // 16sp text
        int topGap = (int) (10 * scale);
        int bottomGap = textSize + (int)(10 * scale);
        int inputWidth = code.length;

        // Add quiet zone
        int fullWidth = inputWidth + 6;
        int outputWidth = Math.max(width, fullWidth);
        int outputHeight = Math.max(1, height) - bottomGap;

        int multiple = outputWidth / fullWidth;
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new Rect(0, 0, width, height), paint);

        // Draw bars
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(0);

        for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
            if (code[inputX] == 1) {
                canvas.drawRect(outputX, topGap, outputX + multiple, outputHeight, paint);
            }
        }

        // Draw textual data below the barcode
        if (context != null) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);

            // Draw text centered below the barcode
            String formattedData = insertSpace(data);
            float textY = outputHeight + (bottomGap / 1.5f);
            canvas.drawText(formattedData, width / 2f, textY, paint);
        }

        return bitmap;
    }

    /**
     * Formats the text by adding a space every 4 characters.
     * @param data Original data
     * @return Spaced data string
     */
    private String insertSpace(String data) {
        if (data == null) return "";
        StringBuilder sb = new StringBuilder();
        int len = data.length();
        for (int i = 0; i < len; i++) {
            sb.append(data.charAt(i));
            if ((i + 1) % 4 == 0) {
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Helper method to append bar weights into the final bit buffer.
     * @param weights The weight pattern of the character
     * @param dst The destination bit buffer
     * @param pos The current offset in the buffer
     * @return Number of elements appended to the destination
     */
    private int appendData(byte[] weights, byte[] dst, int pos) {
        byte color = 1;
        int count = 0;
        int index = pos;
        
        for (byte weight : weights) {
            for (int i = 0; i < weight; i++) {
                dst[index] = color;
                index++;
                count++;
            }
            // Toggle between drawing bar (1) and space (0)
            color ^= 1;
        }
        return count;
    }

    /**
     * Checks if the data string contains only numeric characters.
     * @param data The data to check
     * @return True if numeric, false otherwise
     */
    public static boolean checkNumber(String data) {
        if (data == null) return false;
        int len = data.length();
        for (int i = 0; i < len; i++) {
            char ch = data.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }
}
