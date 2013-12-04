package com.coolbong.barcodegenerator.model;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


//number only
//http://en.wikipedia.org/wiki/Code_128
public class Code128 {

	private static final String TAG = Code128.class.getSimpleName();

	// start code value
	//private static final int CODE_START_A 	= 103;
	//private static final int CODE_START_C 	= 105;
	
	//only make code b type
	private static final int CODE_START_B 	= 104;
	private static final int CODE_STOP 		= 106;
	private static final int DIVISOR = 103;

	private String data;
	private Context context;

	public Code128(Context context) {
		this.context = context;
	}

	public Code128(String data){
		this.data = data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	private int weight = 0;
	private int weight_sum = 0;
	private int check_sum = 0;
	
	public void init() {
		//data = null;
		weight = 0;
		weight_sum = 0;
		check_sum = 0;
	}
	
	public byte[] initBuffer(int dataLen) {
		int sum = 0;
		
		//add start code 11byte
		sum = sum + 11;
		//add encoded data 11 byte * dataLen
		sum = sum + (dataLen * 11);
		//add check sum
		sum = sum + 11;
		//add end code 12byte
		sum = sum + 13;
		
		// sum = 11 + 11 + 12 + (11*dataLen);
		return new byte[sum];
	}
	
	
	
	public byte[] encode() {
		if(data == null) {
			return null;
		}

		int len = data.length();
		int index = -1;
		int count = 0;
		
		init();
		byte[] buffer = initBuffer(len);
		int pos = 0;

		count = appendData(Code128Constant.CODE_WEIGHT[CODE_START_B], buffer, pos, "StartCode");
		pos += count;
		weight_sum = CODE_START_B;

		for(int i=0; i<len; i++) {
			weight++;
			char ch = data.charAt(i);

			index = ch - ' ';
			byte[] ch_weight = Code128Constant.CODE_WEIGHT[index];
			count = appendData(ch_weight, buffer, pos, ch + "");
			pos += count;
			
			int weightByValue = weight * index; 
			weight_sum += weightByValue;
		}

		check_sum = weight_sum % DIVISOR;

		count = appendData(Code128Constant.CODE_WEIGHT[check_sum], buffer, pos, "CheckSum");
		pos += count;
		count = appendData(Code128Constant.CODE_WEIGHT[CODE_STOP], buffer, pos, "CODE_STOP");
		pos += count;
		

		//printCode128MetaInfo();
		
		return buffer;
	}
	
	
	
	public Bitmap getBitmap( int width, int height) {
		byte[] code = encode();
		if(code == null) { 
			return null;
		}
		int TOP_GAP = 30;
		int BOTTOM_GAP = 60;
		int inputWidth = code.length;
		// Add quiet zone on both sides
		int fullWidth = inputWidth + (6);
		int outputWidth = Math.max(width, fullWidth);
		int outputHeight = Math.max(1, height) - BOTTOM_GAP;

		int multiple = outputWidth / fullWidth;
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;

		//BitMatrix output = new BitMatrix(outputWidth, outputHeight);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		bgPaint.setColor(Color.rgb(255, 255, 255));

		Rect bounds = new Rect(0, 0, width, height);
		canvas.drawRect(bounds, bgPaint);
		
		Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setColor(Color.rgb(0, 0, 0));
		barPaint.setStrokeWidth(0);

		for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
			if (code[inputX] == 1) {
				//output.setRegion(outputX, 0, multiple, outputHeight);
				//android.util.Log.e(TAG, "outputX: " + outputX + ", ouputY: 0, multiple: " + multiple + ", outputHeight: " + outputHeight);
				//canvas.drawRect(new Rect(outputX, 0, multiple, outputHeight), barPaint);
				//canvas.drawText(text, x, y, paint)
				//float left, float top, float right, float bottom
				canvas.drawRect(outputX, TOP_GAP, (outputX+multiple), outputHeight, barPaint);
			}
		}
		
		
		Resources resources = context.getResources();
		float scale = resources.getDisplayMetrics().density;
		
		bgPaint.setColor(Color.BLACK);
        
        int size = (int)(26 * scale);
		
        bgPaint.setTextSize(size);

        String str = insertSpace(data);
        
        bgPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(str, width/2, (height - 10), bgPaint);
		
		return bitmap;
	}
	
	private String insertSpace(String data) {
    	StringBuilder sb = new StringBuilder();
    	int cnt = 1;
    	int len = data.length();
    	for(int i=0; i<len; i++, cnt++) {
    		sb.append(data.charAt(i));
    		if(cnt % 4 == 0) {
    			sb.append(" ");
    		}
    	}
    	
    	return sb.toString();
    }
	



	public int getSum() {
		return getSum();
	}



	public static boolean checkNumber(String data) {
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

	private int appendData(byte[] weights, byte[] dst, int pos, String debugdata) {
		
		byte color = 1;
		int count = 0;
		
		int index = pos;
		
		for(byte weight: weights) {
			for(int i=0; i<weight; i++) {
				dst[index] = color;
				index++;
				count++;
			}
			color ^= 1;
		}
		
		//if(debugdata != null)  {
		//	printByteArr(debugdata, weights);
		//}
		
		return count;
	}
	

	public void printByteArr(String msg, byte[] buff) {
		if(buff == null) {
			return;
		}

		int color = 1;

		StringBuilder sb = new StringBuilder();
		for(byte by: buff) {
			for(int i=0; i<by; i++) {
				sb.append(color);
			}

			color ^= 1;
		}
		android.util.Log.e(TAG, "char: " + msg + " barcode weight: " + sb.toString());
	}

	public void printCode128MetaInfo() {

		android.util.Log.e(TAG, "sum: " + weight_sum);
		android.util.Log.e(TAG, "divisor: " + DIVISOR);
		android.util.Log.e(TAG, "sum/divisor: " + (weight_sum/DIVISOR));
		android.util.Log.e(TAG, "check sum value: " + check_sum);
		
	}
	
}
