package se.tpr.pillerkollen.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

public class CapsuleImageView extends View {

	private Paint strokePaint;
	

	private int strokeWidth = 2;
	private int strokeColor = 0xFF000000;
	private int fillColor1 = 0xFFFF33FF;
	private int fillColor2 = 0xFF3366AA;

	private int x;
	private int y;
	private int width;
	private int height;

	public CapsuleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	private void setupDrawing() {
		strokePaint = new Paint();
		strokePaint.setColor(strokeColor);
		strokePaint.setAntiAlias(true);
		strokePaint.setStrokeWidth(strokeWidth*2);
		strokePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		strokePaint.setStrokeJoin(Paint.Join.ROUND);
		strokePaint.setStrokeCap(Paint.Cap.ROUND);
		
		x = 0;
        y = 0;
        width = 24;
        height = 40;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = (int)(0.667f * w); // make the pill 2/3 the width of the image area to compensate for rotation.
		height = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(30, width/2, height/2);
		canvas.scale(0.8f, 0.8f);
		canvas.translate(width*0.3f, 0.0f);
		drawMedicineFills1(canvas);
		drawMedicineFills2(canvas);
		drawMedicineStroke(canvas);
		
	}

	private void drawMedicineStroke(Canvas canvas) {
		float[] outR = new float[] {22,22,22,22,22,22,22,22};   
		RectF   inset = new RectF(strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        float[] innerR = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
		ShapeDrawable d = new ShapeDrawable(new RoundRectShape(outR, inset, innerR));  
		d.setIntrinsicHeight(height);  
		d.setIntrinsicWidth(width);  
		d.setBounds(0, 0, width, height);

		d.getPaint().setColor(strokeColor);  
		d.getPaint().setAntiAlias(true);
		d.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
		d.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
		d.getPaint().setStrokeWidth(strokeWidth);
		d.getPaint().setStrokeJoin(Paint.Join.ROUND);
		d.getPaint().setStrokeCap(Paint.Cap.ROUND);
		d.draw(canvas);
		
		int padding = strokeWidth;
		Path line = new Path();
		line.moveTo(0 + padding, height/2);
		line.lineTo(width - padding, height/2);
		canvas.drawPath(line, strokePaint);
		
	}
	
	
	private void drawMedicineFills1(Canvas canvas) {
		float[] outR = new float[] {12,12,12,12,0,0,0,0};   
		ShapeDrawable d = new ShapeDrawable(new RoundRectShape(outR, null, null));  
		int padding = strokeWidth;
		d.setIntrinsicHeight(height/2-padding);  
		d.setIntrinsicWidth(width-padding);
		
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
					y + height);

		d.getPaint().setColor(fillColor2);  
		d.getPaint().setStyle(Paint.Style.FILL);
		
		d.draw(canvas);
	}
}
