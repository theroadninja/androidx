package x;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * 
 * TODO:  horizontal vs vertical
 * 
 * @author Dave
 *
 */
public class FlowLayout extends ViewGroup {
	
	private static final String TAG = "FlowLayout";
	private void log(String message){
		Log.e(TAG, message);
	}
	

	
	
	/* TODO:
	public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
	public static final int VERTICAL = LinearLayout.VERTICAL;
	
	private int gravity = Gravity.LEFT | Gravity.TOP;
	
	private float weightSum = 0;
	
	private int orientation = HORIZONTAL;
	
	*/
	
	/** do not rely on the size of this array to indicate how many rows exist */
	private ArrayList<Integer> measuredRowHeights = new ArrayList<Integer>();
	/** used by onLayout() */
	final Rect onLayoutRect = new Rect();
	/** used by onLayout() -- rect of child after gravity applied */
	final Rect onLayoutRect2 = new Rect();

	public FlowLayout(Context context) {
		super(context);
	}
	
	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/*
		 * Parsing attributes is going to be a problem
		 * -we cant access the LinearLayout attributes
		 * -we cant define our own without using an XML resource
		 * 
		 * althought we might get away with referencing android.R.attr.*
		 */
	}
	
	
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams lp){
		return lp != null && MarginGravityLayoutParams.class.isAssignableFrom(lp.getClass());
	}
	
	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp){
		return new ViewGroup.LayoutParams(lp);
	}
	

	
	@Override
	public MarginGravityLayoutParams generateLayoutParams(AttributeSet attrs){
		
		return new MarginGravityLayoutParams(getContext(), attrs);
	}
	
	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams(){
		return new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	/**
	 * According to http://developer.android.com/reference/android/view/ViewGroup.html#onLayout%28boolean,%20int,%20int,%20int,%20int%29
	 * any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

	
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		onMeasureHorizontal(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	private void onMeasureHorizontal(int widthMeasureSpec, int heightMeasureSpec){
		
		//TODO: implement padding
		final int paddingW = getPaddingLeft() + getPaddingRight();
		final int paddingH = getPaddingTop() + getPaddingBottom();
		
		
		//figure out measure spec for children
		/*
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int childHeightSpec;
		if(MeasureSpec.AT_MOST == heightMode || MeasureSpec.EXACTLY == heightMode){
			childHeightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
			
			//wait...no.  need to take child param into account
		}else{
			
		}
		*/
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		
		
		
		
		int rowIndex = 0;
		
		int currentRowWidth = 0;
		int maxRowWidth = 0;
		
		measuredRowHeights.clear();
		
		for(int i = 0; i < getChildCount(); ++i){
			View child = getChildAt(i);
			if(child.getVisibility() == View.GONE) continue;
			
			//MarginGravityLayoutParams childLp = MarginGravityLayoutParams.fromLayoutParams(child.getLayoutParams());
			MarginGravityLayoutParams childLp = (MarginGravityLayoutParams) child.getLayoutParams();
			
			
			
			
			//int childWidthSpec = getHorizontalChildWidthSpec(widthMode, widthSize, childLp);
			int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, paddingW, childLp.width);
			int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, paddingH, childLp.height);
			
			child.measure(childWidthSpec, childHeightSpec);
			
			
			int w = child.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin;
			int h = child.getMeasuredHeight() + childLp.bottomMargin + childLp.topMargin;
			boolean newRow = (widthMode != MeasureSpec.UNSPECIFIED
					&& currentRowWidth + w + paddingW > widthSize);
			
			
			//the +1 just makes is easier when we increment row index
			while(rowIndex+1 >= measuredRowHeights.size()){
				measuredRowHeights.add(0);
			}
			
			
			//TODO: deal with padding/spacing
			if(newRow){
				
				rowIndex++;
				currentRowWidth = w;
				measuredRowHeights.set(rowIndex, h);
			}else{
				currentRowWidth += w;
				if(h > this.measuredRowHeights.get(rowIndex)){
					measuredRowHeights.set(rowIndex, h);
				}
			}
			
			if(currentRowWidth > maxRowWidth){
				maxRowWidth = currentRowWidth;
			}
			
		} //for each child
		
		
		int finalWidth;
		if(rowIndex > 0){
			finalWidth = widthSize;
		}else{
			finalWidth = maxRowWidth;
		}
		
		
		int totalHeightAllRows = 0;
		for(int i = 0; i < this.measuredRowHeights.size(); ++i){
			totalHeightAllRows += measuredRowHeights.get(i);
		}
		
		
		int finalHeight = totalHeightAllRows + paddingH;
		if(finalHeight > heightSize || MeasureSpec.EXACTLY == heightMode){
			finalHeight = heightSize;
		}
		
		
		//log("setting measured dimensions: " + finalWidth + ", " + finalHeight);
		setMeasuredDimension(finalWidth, finalHeight);
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b){
		onLayoutHorizontal(changed, l, t, r, b);
		
		//TODO: handle vertical case
	}

	
	private void onLayoutHorizontal(boolean changed, int l, int t, int r, int b) {
		
		
		
		int parentLeft = getPaddingLeft();
		int parentRight = r - l - getPaddingRight();
		int parentTop = getPaddingTop();
		//we dont really care:  int parentBottom = b - t - getPaddingBottom();
		
		
		
		
		
		int x = parentLeft;
		int y = parentTop;
		int rowIndex = 0;
		
		
		for (int i = 0; i < getChildCount(); ++i){
			final View child = getChildAt(i);
			if(child.getVisibility() == View.GONE) continue;
		
			//MarginGravityLayoutParams childLp = MarginGravityLayoutParams.fromLayoutParams(child.getLayoutParams());
			MarginGravityLayoutParams childLp = (MarginGravityLayoutParams) child.getLayoutParams();

			
			if(x + child.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin > parentRight){
				
				x = parentLeft;
				y += measuredRowHeights.get(rowIndex);
				rowIndex++;
				
			}
			
			//log("row " + rowIndex + " child measured height: " + child.getMeasuredHeight());
			
			onLayoutRect.left = x + childLp.leftMargin;
			onLayoutRect.right = onLayoutRect.left + child.getMeasuredWidth();
			onLayoutRect.top = y;
			onLayoutRect.bottom = y + measuredRowHeights.get(rowIndex);
			
			
			//log("rect1: " + onLayoutRect.toShortString());
			//log("width,height = " + child.getMeasuredWidth() + "," + child.getMeasuredHeight());
			
			//should only apply vertically
			Gravity.apply(childLp.gravity, 
					child.getMeasuredWidth(), 
					child.getMeasuredHeight(), 
					onLayoutRect, 
					onLayoutRect2);
		
			//log("rect2: " + onLayoutRect2.toShortString());
			
			child.layout(
					onLayoutRect2.left,
					onLayoutRect2.top,
					onLayoutRect2.right,
					onLayoutRect2.bottom);
			
			x += childLp.leftMargin + child.getMeasuredWidth() + childLp.rightMargin;
		}
	}
	
	/*
	private int getHorizontalChildWidthSpec(int widthMode, int widthSize, ViewGroup.LayoutParams childLp){
	
		
		int childWidthSpec;
		if(MeasureSpec.UNSPECIFIED == widthMode){
			
			if(childLp.width == ViewGroup.LayoutParams.WRAP_CONTENT
					|| childLp.width == ViewGroup.LayoutParams.MATCH_PARENT){
				
				return MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED);
			}else{
				
				return MeasureSpec.makeMeasureSpec(childLp.width, MeasureSpec.EXACTLY);
			}
			
		}else{
			
			if(childLp.width == ViewGroup.LayoutParams.WRAP_CONTENT
					|| childLp.width == ViewGroup.LayoutParams.MATCH_PARENT
					|| childLp.width > widthSize){
			
				childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
			}else{
				
				childWidthSpec = MeasureSpec.makeMeasureSpec(childLp.width, MeasureSpec.EXACTLY);
			}
			
			
		}
		return childWidthSpec;
	}
	
	private int getHorizontalChildHeightSpec(){
		
	}
	*/
	
	
}
