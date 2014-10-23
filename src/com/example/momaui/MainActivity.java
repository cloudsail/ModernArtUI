package com.example.momaui;

import java.util.ArrayList;
import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Pair;
import android.view.*;
import android.widget.*;

public class MainActivity extends ActionBarActivity {
	
	private LinearLayout row1;
	private LinearLayout row2;
	private LinearLayout row3;
	private LinearLayout row4;
	private LinearLayout row5;
	private SeekBar seekBar;
	
	private static final int rectanglesPerRow = 5;
	private static final float saturation = 0.6f;
	private static final float luminance = 0.9f;
	
	private int screenWidth;
	private int screenHeight;
	
	private ArrayList<Pair<ImageView, float[]>> coloredRectangles;
	private ArrayList<ImageView> greyRectangles;
	
	final Context mContext = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.seekBar = (SeekBar) findViewById(R.id.seekBar);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        
        this.refreshRectangles();
        
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				shiftColors(progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
		});
    }

    /**
     * Create or refresh the main view
     */
    private void refreshRectangles() {
        this.coloredRectangles = new ArrayList<Pair<ImageView, float[]>>();
        this.greyRectangles = new ArrayList<ImageView>();
        
        this.row1 = (LinearLayout) findViewById(R.id.row1);
        this.row1.removeAllViews();
        this.addRectanglesToRow(row1);
        
        this.row2 = (LinearLayout) findViewById(R.id.row2);
        this.row2.removeAllViews();
        this.addRectanglesToRow(row2);
        
        this.row3 = (LinearLayout) findViewById(R.id.row3);
        this.row3.removeAllViews();
        this.addRectanglesToRow(row3);
        
        this.row4 = (LinearLayout) findViewById(R.id.row4);
        this.row4.removeAllViews();
        this.addRectanglesToRow(row4);
        
        this.row5 = (LinearLayout) findViewById(R.id.row5);
        this.row5.removeAllViews();
        this.addRectanglesToRow(row5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.more_info) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        	builder.setMessage(R.string.dialog_text)
		        	.setPositiveButton(R.string.visit_moma, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       Intent startWeb = new Intent(MainActivity.this, WebActivity.class);
		                       startActivity(startWeb);
		                   }
		               })
		               .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       dialog.cancel();
		                   }
		               });
        	builder.create().show();
            return true;
        }
        else if (id == R.id.refresh) {
        	this.refreshRectangles();
        	this.row1.invalidate();
        	this.row2.invalidate();
        	this.row3.invalidate();
        	this.row4.invalidate();
        	this.row5.invalidate();
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Generate rectangles with random dimensions and add them to the row
     * Also store ImageView and corresponding color info in an array
     * @param row The row to add the rectangles to
     */
    private void addRectanglesToRow(LinearLayout row) {
    	int totalWidth = this.screenWidth;
    	
    	for (int i = 1; i <= rectanglesPerRow; i++) {
        	ImageView image = new ImageView(this);
        	ShapeDrawable rectangle = new ShapeDrawable(new RectShape());
        	rectangle.setIntrinsicHeight(this.screenHeight/5);
        	rectangle.setPadding(0, 0, 0, 0);
        	
        	if ((i & 1) == 0) {
        		rectangle.getPaint().setColor(Color.LTGRAY);
        		this.greyRectangles.add(image);
        	}
        	else {
        		float[] hsvColor = this.getRandomColor();
        		rectangle.getPaint().setColor(Color.HSVToColor(hsvColor));
        		this.coloredRectangles.add(new Pair<ImageView, float[]>(image, hsvColor));
        	}
        	
        	int width;
        	if (i < rectanglesPerRow) {
	        	width = (int) Math.round(Math.random() * (totalWidth/2));
	        	totalWidth -= width;
        	}
        	else
        	{
        		width = totalWidth;
        	}
        	rectangle.setIntrinsicWidth(width);
        	image.setImageDrawable(rectangle);
        	row.addView(image);
        }
    }
    
    /**
     * Return a random color in HSV format
     * @return HSV as a float array
     */
    private float[] getRandomColor() {
    	Random rand = new Random();
    	float hue = rand.nextFloat()*360;
    	return new float[]{hue, saturation, luminance};
    }
    
    /**
     * Shift all the colored rectangle hues
     * @param value The amount to shift by
     */
    private void shiftColors(int value) {
    	for (Pair<ImageView,float[]> pair : this.coloredRectangles) {
    		ImageView image = pair.first;
    		ShapeDrawable rectangle = (ShapeDrawable) image.getDrawable();
    		
    		float[] hsvValues = pair.second;
    		
    		float hue = (hsvValues[0] + value) % 360;
    		
    		rectangle.getPaint().setColor(Color.HSVToColor(new float[]{hue, hsvValues[1], hsvValues[2]}));
    		image.invalidate();
    	}
    }
}
