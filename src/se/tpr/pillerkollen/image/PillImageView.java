package se.tpr.pillerkollen.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PillImageView extends View {

	private Paint edgePaint, strokePaint, fillPaint;
	

	private int strokeWidth = 2;
	private int strokeColor = 0xFF000000;
	private int fillColor = 0xFFFF0000;

	private float x;
	private float y;
	private int width;
	private int height;
	private float radius;

	public PillImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	private void setupDrawing(){
		edgePaint = new Paint();
		edgePaint.setColor(strokeColor);
		edgePaint.setAntiAlias(true);
		edgePaint.setStrokeWidth(strokeWidth);
		edgePaint.setStyle(Paint.Style.FILL);

		strokePaint = new Paint();
		strokePaint.setColor(strokeColor);
		strokePaint.setAntiAlias(true);
		strokePaint.setStrokeWidth(strokeWidth);
		strokePaint.setStyle(Paint.Style.STROKE);

		fillPaint = new Paint();
		fillPaint.setColor(fillColor);
		fillPaint.setAntiAlias(true);
		fillPaint.setStyle(Paint.Style.FILL);
		
//		strokePaint.setStrokeJoin(Paint.Join.ROUND);
//		strokePaint.setStrokeCap(Paint.Cap.ROUND);
		
        width = 30;
        height = 30;
        
		x = width/2;
        y = height/2;
        radius = width/2;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		x = width/2;
        y = height/2;
        radius = width/2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		
//		drawMedicineFills2(canvas);
		drawMedicineEdge(canvas);
		drawMedicineFills(canvas);
		drawMedicineMiddleBorder(canvas);
	}

	private void drawMedicineEdge(Canvas canvas) {
//		float[] outR = new float[] {22,22,22,22,22,22,22,22};   
//		RectF   inset = new RectF(4, 4, 4, 4);
//        float[] innerR = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
//		ShapeDrawable d = new ShapeDrawable(new OvalShape());  
//		d.setIntrinsicHeight(height);  
//		d.setIntrinsicWidth(width);  
//		d.setBounds(0, 0, width, height);
//
//		d.getPaint().setColor(strokeColor);  
//		d.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
//		d.getPaint().setStrokeWidth(strokeWidth);
//		d.getPaint().setStrokeJoin(Paint.Join.ROUND);
//		d.getPaint().setStrokeCap(Paint.Cap.ROUND);
//		d.draw(canvas);
		
		canvas.drawCircle(x, y, radius, edgePaint);
		
	}

	private void drawMedicineMiddleBorder(Canvas canvas) {
		Path line = new Path();
		line.moveTo(0, height/2);
		line.lineTo(width, height/2);
		canvas.drawPath(line, strokePaint);
	}
	
	
	private void drawMedicineFills(Canvas canvas) {
		canvas.drawCircle(x, y, radius-strokeWidth, fillPaint);
		Log.d(this.getClass().getName(), String.format("Draw circle: x: %f, y: %f, radius: %f", x, y, radius));
		
	}
	
}
