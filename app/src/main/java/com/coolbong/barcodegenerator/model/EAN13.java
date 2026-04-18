package com.coolbong.barcodegenerator.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Generates an EAN-13 barcode image.
 * Contains logic for deciding L-code, G-code, and R-code patterns,
 * and converting data into barcode binary forms.
 */
public class EAN13 {

    private static final String TAG = EAN13.class.getSimpleName();

    private String data;
    private android.content.Context context;

    public EAN13(android.content.Context context) {
        this.context = context;
    }

    public EAN13(android.content.Context context, String data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Sets the numeric data to encode.
     * @param data The 13-digit data string
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the currently set data.
     * @return The 13-digit data string
     */
    public String getData() {
        return data;
    }

    /**
     * Calculates the required buffer size.
     * @return An array of bytes acting as the bit buffer
     */
    public byte[] initBuffer() {
        int sum = 0;

        // Start code: 3 modules
        sum += 3;
        // Middle code: 5 modules
        sum += 5;
        // End code: 3 modules
        sum += 3;
        // Encoded data: 12 digits * 7 modules
        sum += (7 * 12);

        return new byte[sum];
    }

    /**
     * Encodes the data into a binary byte array.
     * @return The byte array where each element specifies width unit of bar/space, null on invalid data
     */
    public byte[] encode() {
        if (!isValidBarcodeData()) {
            android.util.Log.e(TAG, "invalid data length or content!!");
            return null;
        }

        int len = data.length();
        int pos = 0;
        byte[] buffer = initBuffer();

        // Determine left-hand encoding structure from the very first digit
        int firstNum = Integer.parseInt(data.substring(0, 1));
        byte[] patterns = EAN13Constant.FIRST_DIGIT[firstNum];
        
        // Append Start Code
        pos += appendData(EAN13Constant.START_PATTERN, buffer, pos);

        // Process following 12 characters
        for (int i = 1; i < len; i++) {
            int num = Integer.parseInt(data.substring(i, i + 1));
            byte code = patterns[(i - 1)];
            
            if (code == EAN13Constant.L_CODE) {
                // Left-hand L code
                pos += appendData(EAN13Constant.L_CODE_PATTERN[num], buffer, pos);
            } else if (code == EAN13Constant.G_CODE) {
                // Left-hand G code
                pos += appendData(EAN13Constant.G_CODE_PATTERN[num], buffer, pos);
            } else {
                // Right-hand R code
                pos += appendData(EAN13Constant.R_CODE_PATTERN[num], buffer, pos);
            }
            
            // Insert Middle Code after the 6th encoded digit
            if (i == 6) {
                pos += appendData(EAN13Constant.MIDDLE_PATTERN, buffer, pos);
            }
        }
        
        // Append End Code
        pos += appendData(EAN13Constant.END_PATTERN, buffer, pos);

        return buffer;
    }

    /**
     * Generates a Bitmap containing the generated EAN-13 barcode.
     * @param width The target width
     * @param height The target height
     * @return A generated Bitmap or null if encoding failed
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

        // Quiet zones
        int fullWidth = inputWidth + 6; 
        int outputWidth = Math.max(width, fullWidth);
        int outputHeight = Math.max(1, height) - bottomGap;

        int multiple = outputWidth / fullWidth;
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new Rect(0, 0, width, height), paint);

        // Bars
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(0);

        for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
            if (code[inputX] == 1) {
                // Determine whether to stretch down (for guard bars: first 3, middle 5, last 3)
                // Actually EAN13 guard bars are slightly longer, but for simplicity we draw equal height here
                // unless fully implementing EAN13 varying bar heights.
                canvas.drawRect(outputX, topGap, outputX + multiple, outputHeight, paint);
            }
        }
        
        // Draw textual data below the barcode
        if (context != null) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);

            // Draw text centered below the barcode
            float textY = outputHeight + (bottomGap / 1.5f);
            canvas.drawText(data, width / 2f, textY, paint);
        }
        
        return bitmap;
    }

    /**
     * Validates whether the provided data is suitable for EAN-13.
     * Must be exactly 13 digits and strictly numbers.
     * @return True if valid, false otherwise
     */
    public boolean isValidBarcodeData() {
        if (data == null) {
            return false;
        }
        if (data.length() != 13) {
            return false;
        }
        return checkNumber(data);
    }

    /**
     * Checks if strings only contain digits.
     * @param data Target string
     * @return True if strict integer
     */
    private static boolean checkNumber(String data) {
        int len = data.length();
        for (int i = 0; i < len; i++) {
            char ch = data.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Copies source bit pattern to the destination buffer.
     * @param src Pattern definition byte array
     * @param dst Bit buffer 
     * @param pos Offset in the bit buffer
     * @return Length appended
     */
    private int appendData(byte[] src, byte[] dst, int pos) {
        System.arraycopy(src, 0, dst, pos, src.length);
        return src.length;
    }
}
