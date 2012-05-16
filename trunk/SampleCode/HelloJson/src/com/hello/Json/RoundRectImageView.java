/**
 * 
 */
package com.hello.Json;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Austin Joe
 *
 */
public class RoundRectImageView extends ImageView {
	private int n = 5;
	
	private int rWidth = 0;
	private int rHeight = 0;
	/**
	 * @param context
	 */
	public RoundRectImageView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public RoundRectImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setRoundThickness( int n ){
		this.n = n;
	}
	
	public void setHeight( int h ){
		this.rHeight = h;
	}
	
	public void setWidth( int w ){
		this.rWidth = w;
	}
	
	@Override
	protected void onDraw( Canvas canvas ){
		super.onDraw(canvas);
		float[] outerR = new float[] { n,n,n,n,n,n,n,n };
		RectF   inset = new RectF( 5, 5, 5, 5); // Î™®ÏÑúÎ¶??•Í∑º ?¨Í∞Å???†Ïùò ?êÍªò.
		float[] innerR = new float[] { n,n,n,n,n,n,n,n };
		ShapeDrawable mDrawables = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
		mDrawables.getPaint().setColor(Color.BLACK);
		mDrawables.getPaint().setAntiAlias(true);
		mDrawables.setBounds(-2,-2,rWidth+1, rHeight+1);
		mDrawables.draw(canvas);
	}

}
