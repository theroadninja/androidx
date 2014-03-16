package x;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * TODO:  also support weight
 * 
 * @author Dave
 *
 */
public class MarginGravityLayoutParams extends ViewGroup.MarginLayoutParams {

	public static MarginGravityLayoutParams fromLayoutParams(ViewGroup.LayoutParams lp){
		
		if(lp != null && MarginGravityLayoutParams.class.isAssignableFrom(lp.getClass())){
			return (MarginGravityLayoutParams) lp;
		}else{
			return new MarginGravityLayoutParams(lp);
		}
	}
	
	public int gravity = Gravity.TOP | Gravity.START;

	public MarginGravityLayoutParams(Context c, AttributeSet attrs){
		super(c, attrs);
		
		
		//cant think of a better way that still avoids creating a res xml
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(c, attrs);
		
		//http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/1.5_r4/android/widget/LinearLayout.java#LinearLayout.LayoutParams
		if(ll.gravity != -1){
			gravity = ll.gravity;
		}
		
		
		
		

	}
	
	public MarginGravityLayoutParams(int width, int height) {
		super(width, height);
	}
	
	public MarginGravityLayoutParams(ViewGroup.LayoutParams layoutParams){
		super(layoutParams);
		
		if(layoutParams == null){
			return;
		}
		
		if(MarginGravityLayoutParams.class.isAssignableFrom(layoutParams.getClass())){
			this.gravity = ((MarginGravityLayoutParams)layoutParams).gravity;
		}else if(LinearLayout.LayoutParams.class.isAssignableFrom(layoutParams.getClass())){
			this.gravity = ((LinearLayout.LayoutParams)layoutParams).gravity;
		}
	}

}
