package se.tpr.pillerkollen.icon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

public class MedicineIconView extends View {

	private Paint strokePaint;
	

	private int strokeWidth = 4;
	private int strokeColor = 0xFF000000;
	private int fillColor1 = 0xFFFF0000;
	private int fillColor2 = 0xFF0000FF;

	private int x;
	private int y;
	private int width;
	private int height;

	public MedicineIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	private void setupDrawing(){
		strokePaint = new Paint();
		strokePaint.setColor(strokeColor);
		strokePaint.setAntiAlias(true);
		strokePaint.setStrokeWidth(strokeWidth);
		strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		strokePaint.setStrokeJoin(Paint.Join.ROUND);
		strokePaint.setStrokeCap(Paint.Cap.ROUND);
		
		x = 0;
        y = 0;
        width = 20;
        height = 50;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = (int)(0.667f * w);
		height = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawMedicineFills1(canvas);
		drawMedicineFills2(canvas);
		drawMedicineStroke(canvas);
		
	}

	private void drawMedicineStroke(Canvas canvas) {
		float[] outR = new float[] {22,22,22,22,22,22,22,22};   
		RectF   inset = new RectF(4, 4, 4, 4);
        float[] innerR = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
		ShapeDrawable d = new ShapeDrawable(new RoundRectShape(outR, inset, innerR));  
		d.setIntrinsicHeight(height);  
		d.setIntrinsicWidth(width);  
		d.setBounds(0, 0, width, height);

		d.getPaint().setColor(strokeColor);  
		d.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
		d.getPaint().setStrokeWidth(strokeWidth);
		d.getPaint().setStrokeJoin(Paint.Join.ROUND);
		d.getPaint().setStrokeCap(Paint.Cap.ROUND);
		d.draw(canvas);
		
		Path line = new Path();
		line.moveTo(0, height/2);
		line.lineTo(width, height/2);
		canvas.drawPath(line, strokePaint);
	}
	
	
	private void drawMedicineFills1(Canvas canvas) {
		float[] outR = new float[] {12,12,12,12,0,0,0,0};   
		ShapeDrawable d = new ShapeDrawable(new RoundRectShape(outR, null, null));  
		d.setIntrinsicHeight(height/2);  
		d.setIntrinsicWidth(width);
		int padding = strokeWidth/2;
		d.setBounds(x + padding, 
					y + padding, 
					x + width - padding, 
					y + height/2 - padding);

		d.getPaint().setColor(fillColor1);  
		d.getPaint().setStyle(Paint.Style.FILL);
		
		d.draw(canvas);
	}
	
	private void drawMedicineFills2(Canvas canvas) {
		float[] outR = new float[] {0,0,0,0,12,12,12,12};   
		ShapeDrawable d = new ShapeDrawable(new RoundRectShape(outR, null, null));  
		d.setIntrinsicHeight(height/2);  
		d.setIntrinsicWidth(width);
		int padding = strokeWidth/2;
		d.setBounds(x + padding, 
					y + height/2 + padding, 
					x + width - padding, 
					y + height - padding);

		d.getPaint().setColor(fillColor2);  
		d.getPaint().setStyle(Paint.Style.FILL);
		
		d.draw(canvas);
	}
}
