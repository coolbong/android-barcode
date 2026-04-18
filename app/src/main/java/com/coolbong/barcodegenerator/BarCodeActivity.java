package com.coolbong.barcodegenerator;

import com.coolbong.barcodegenerator.model.Code128;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;

public class BarCodeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar_code);
		drawBarcode();
	}
	
	private void drawBarcode() {
		String barcode = "7761010114033430";
		Code128 code = new Code128(this);
        code.setData(barcode);
        
        // Responsive size
        android.util.DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels - (int)(40 * metrics.density); // leave margins
        int height = (int)(width * 0.45); // Adjust ratio appropriately
        
        Bitmap bitmap = code.getBitmap(width, height);
        ImageView ivBarcode = (ImageView)findViewById(R.id.code128_barcode);
        // Let it scale properly inside ImageView
        ivBarcode.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivBarcode.setImageBitmap(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_bar_code, menu);
		return true;
	}

}
