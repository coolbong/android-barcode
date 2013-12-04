package com.coolbong.barcodegenerator.model;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class EAN13 {

	private static final String TAG = EAN13.class.getSimpleName();

	private String data;

	public EAN13() {

	}

	public EAN13(String data){
		this.data = data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}


	public void init() {
		//data = null;
	}

	public byte[] initBuffer() {
		int sum = 0;

		//add start code 4byte
		sum = sum + 3;
		//add middle code 5byte
		sum = sum + 5;
		//add end code 4 byte
		sum = sum + 3;
		//add encoded data 7byte * 12
		sum = sum + (7 * 12);

		// sum = 11 + 11 + 12 + (11*dataLen);
		return new byte[sum];
	}



	public byte[] encode() {

		if(isVaildBarcodeData() == false) {
			android.util.Log.e(TAG, "invalid data length!!");
			return null;
		}
		
		int len = data.length();
		int pos = 0;

		init();
		byte[] buffer = initBuffer();

		int first_num = Integer.parseInt(data.substring(0, 1));
		byte[] patterns = EAN13Constant.FIRST_DIGIT[first_num];
		
		pos += appendData(EAN13Constant.START_PATTERN, buffer, pos, "START CODE");
		for(int i=1; i<len; i++) {
			int num = Integer.parseInt(data.substring(i, i+1));
			
			byte code = patterns[(i-1)];
			
			if(code == EAN13Constant.L_CODE) {
				pos += appendData(EAN13Constant.L_CODE_PATTERN[num], buffer, pos, "L code based number");
			} else if(code ==EAN13Constant.G_CODE) {
				pos += appendData(EAN13Constant.G_CODE_PATTERN[num], buffer, pos, "G code based number");
			} else { // R-code
				pos += appendData(EAN13Constant.R_CODE_PATTERN[num], buffer, pos, "R code based number");
			}
			
			if(i == 6) {
				pos += appendData(EAN13Constant.MIDDLE_PATTERN, buffer, pos, "MIDDLE CODE");
			}
		}
		
		pos += appendData(EAN13Constant.END_PATTERN, buffer, pos, "END CODE");

		return buffer;
	}



	public Bitmap getBitmap( int width, int height) {
		byte[] code = encode();
		
		if(code == null) {
			return null;
		}
		int inputWidth = code.length;
		// Add quiet zone on both sides
		int fullWidth = inputWidth + 6; // for empty(quiet) space
		int outputWidth = Math.max(width, fullWidth);
		int outputHeight = Math.max(1, height);

		int multiple = outputWidth / fullWidth;
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;

		//BitMatrix output = new BitMatrix(outputWidth, outputHeight);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		bgPaint.setColor(Color.rgb(255, 255, 255));

		Rect bounds = new Rect(0, 0, width, height);
		canvas.drawRect(bounds, bgPaint);

		Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setColor(Color.rgb(0, 0, 0));
		barPaint.setStrokeWidth(0);

		for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
			if (code[inputX] == 1) {
				//output.setRegion(outputX, 0, multiple, outputHeight);
				android.util.Log.e(TAG, "outputX: " + outputX + ", ouputY: 0, multiple: " + multiple + ", outputHeight: " + outputHeight);
				//canvas.drawRect(new Rect(outputX, 0, multiple, outputHeight), barPaint);
				//canvas.drawText(text, x, y, paint)
				//float left, float top, float right, float bottom
				canvas.drawRect(outputX, 0, (outputX+multiple), outputHeight, barPaint);
			}
		}
		return bitmap;
	}

	public int getSum() {
		return getSum();
	}

	public boolean isVaildBarcodeData() {
		if(data == null) {
			return false;
		}
		
		if(data.length() != 13) {
			return false;
		}
		
		if(checkNumber(data) == false) {
			return false;
		}
		
		return true;
	}


	private static boolean checkNumber(String data) {
		int len = data.length();

		for(int i=0; i<len; i++ ) {
			char ch = data.charAt(i);
			if (ch < '0' || ch > '9') {
				//if((ch < 48)  || (ch > 57)) {
				return false;
			}
		}

		return true;
	}

	private int appendData(byte[] src, byte[] dst, int pos, String debugdata) {

		System.arraycopy(src, 0, dst, pos, src.length);

		if(debugdata != null)  {
			printByteArr(debugdata, src);
		}

		return src.length;
	}

	private void printByteArr(String msg, byte[] buff) {
		if(buff == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for(byte by: buff) {
			sb.append(by);
		}
		android.util.Log.e(TAG, "char: " + msg + " barcode weight: " + sb.toString());
	}

}
