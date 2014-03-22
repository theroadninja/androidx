package x;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class FlowLayout  extends LinearLayout {
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
	private int orientation = HORIZONTAL;
	/** do not rely on the size of this array to indicate how many rows exist */
	private ArrayList<Integer> measuredRowHeights = new ArrayList<Integer>();
	private ArrayList<Float> totalRowWeights = new ArrayList<Float>();
	private ArrayList<Integer> totalRowLengths = new ArrayList<Integer>();
	/** used by onLayout() */
	final Rect onLayoutRect = new Rect();
	/** used by onLayout() -- rect of child after gravity applied */
	final Rect onLayoutRect2 = new Rect();

	public FlowLayout(Context context) {
		super(context);
		orientation = getOrientation();
	}
	
	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		orientation = getOrientation();
		/*
		 * Parsing attributes is going to be a problem
		 * -we cant access the LinearLayout attributes
		 * -we cant define our own without using an XML resource
		 * 
		 * althought we might get away with referencing android.R.attr.*
		 */
	}
	

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#FILL_PARENT}
     * and a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * when the layout's orientation is {@link #VERTICAL}. When the orientation is
     * {@link #HORIZONTAL}, the width is set to {@link LayoutParams#WRAP_CONTENT}
     * and the height to {@link LayoutParams#WRAP_CONTENT}.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        if (orientation == HORIZONTAL) {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else if (orientation == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return null;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }


    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LinearLayout.LayoutParams;
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
		if(orientation == HORIZONTAL)
		{
		   onMeasureHorizontal(widthMeasureSpec, heightMeasureSpec);
		}
		else
	    {
			onMeasureVertical(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	private void onMeasureVertical(int widthMeasureSpec, int heightMeasureSpec){
		
		//TODO: implement padding
		final int paddingW = getPaddingLeft() + getPaddingRight();
		final int paddingH = getPaddingTop() + getPaddingBottom();
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			
		int rowIndex = 0;
		
		int currentRowHeight = 0;
		int maxRowHeight = 0;
		
		measuredRowHeights.clear();
		
		for(int i = 0; i < getChildCount(); ++i){
			View child = getChildAt(i);
			if(child.getVisibility() == View.GONE) continue;
			
			//MarginGravityLayoutParams childLp = MarginGravityLayoutParams.fromLayoutParams(child.getLayoutParams());
			LinearLayout.LayoutParams childLp = (LinearLayout.LayoutParams) child.getLayoutParams();
			
			
			//int childWidthSpec = getHorizontalChildWidthSpec(widthMode, widthSize, childLp);
			int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, paddingW, childLp.width);
			int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, paddingH, childLp.height);
			
			child.measure(childWidthSpec, childHeightSpec);
			
			
			int w = child.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin;
			int h = child.getMeasuredHeight() + childLp.bottomMargin + childLp.topMargin;
			boolean newRow = (heightMode != MeasureSpec.UNSPECIFIED
					&& currentRowHeight + h + paddingH > heightSize);
			
			
			//the +1 just makes is easier when we increment row index
			while(rowIndex+1 >= measuredRowHeights.size()){
				measuredRowHeights.add(0);
			}
			
			
			//TODO: deal with padding/spacing
			if(newRow){
				
				rowIndex++;
				currentRowHeight = h;
				measuredRowHeights.set(rowIndex, w);
			}else{
				currentRowHeight += h;
				if(w > this.measuredRowHeights.get(rowIndex)){
					measuredRowHeights.set(rowIndex, w);
				}
			}
			
			if(currentRowHeight > maxRowHeight){
				maxRowHeight = currentRowHeight;
			}
			
		} //for each child
		
		
		int finalHeight;
		if(rowIndex > 0){
			finalHeight = heightSize;
		}else{
			finalHeight = maxRowHeight;
		}
		
		
		int totalWidthAllRows = 0;
		for(int i = 0; i < this.measuredRowHeights.size(); ++i){
			totalWidthAllRows += measuredRowHeights.get(i);
		}
		
		
		int finalWidth = totalWidthAllRows + paddingW;
		if(finalWidth > widthSize || MeasureSpec.EXACTLY == widthMode){
			finalWidth = widthSize;
		}
		
		
		//log("setting measured dimensions: " + finalWidth + ", " + finalHeight);
		setMeasuredDimension(finalWidth, finalHeight);
		
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
		totalRowWeights.clear();
		totalRowLengths.clear();
		totalRowWeights.add(0.0f);
		totalRowLengths.add(0);
		
		//first pass counts up the weights, remember total space used
		for(int j = 0; j < getChildCount(); ++j){
			View child = getChildAt(j);
			if(child.getVisibility() == View.GONE) continue;
			
			LinearLayout.LayoutParams childLp = (LinearLayout.LayoutParams) child.getLayoutParams();
			totalRowWeights.set(rowIndex, childLp.weight + totalRowWeights.get(rowIndex));
			
			int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, paddingW, childLp.width);
			int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, paddingH, childLp.height);
			
			child.measure(childWidthSpec, childHeightSpec);
			
			int w = child.getMeasuredWidth() + childLp.leftMargin + childLp.rightMargin;
			boolean newRow = (widthMode != MeasureSpec.UNSPECIFIED
					&& currentRowWidth + w + paddingW > widthSize);
			
            if(newRow){
				rowIndex++;
				currentRowWidth = w;
				totalRowWeights.add(childLp.weight);
				totalRowLengths.add(rowIndex, currentRowWidth);
			}else{
				currentRowWidth += w;
				totalRowLengths.set(rowIndex, currentRowWidth);
				
			}
			
			if(currentRowWidth > maxRowWidth){
				maxRowWidth = currentRowWidth;
			}
			
		}
		

		rowIndex = 0;
		
	    currentRowWidth = 0;
		maxRowWidth = 0;		
		
		for(int i = 0; i < getChildCount(); ++i){
			View child = getChildAt(i);
			if(child.getVisibility() == View.GONE) continue;
			
			//MarginGravityLayoutParams childLp = MarginGravityLayoutParams.fromLayoutParams(child.getLayoutParams());
			LinearLayout.LayoutParams childLp = (LinearLayout.LayoutParams) child.getLayoutParams();
			
			
			
			
			//int childWidthSpec = getHorizontalChildWidthSpec(widthMode, widthSize, childLp);
			int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, paddingW, childLp.width);
			int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, paddingH, childLp.height);
			
			
			
			int childWidth = child.getMeasuredWidth();
			
			//take care of weight assignments
			if(childLp.weight > 0.0)
			{
				childWidth = (int) (childWidth + ((childLp.weight / totalRowWeights.get(rowIndex)) * 
						                         ((widthSize - paddingW)- totalRowLengths.get(rowIndex))));
			}
			
			child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), childHeightSpec);
			
			
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
		if(orientation == HORIZONTAL)
		{
			onLayoutHorizontal(changed, l, t, r, b);
		}
		else
	    {
			onLayoutVertical(changed, l, t, r, b);
		}
	}

	
	private void onLayoutVertical(boolean changed, int l, int t, int r, int b) {
				
		int parentLeft = getPaddingLeft();
		int parentRight = r - l - getPaddingRight();
		int parentTop = getPaddingTop();
		int parentBottom = b - t - getPaddingBottom();
				
		
		int x = parentLeft;
		int y = parentTop;
		int rowIndex = 0;
		
		
		for (int i = 0; i < getChildCount(); ++i){
			final View child = getChildAt(i);
			if(child.getVisibility() == View.GONE) continue;
		
			//MarginGravityLayoutParams childLp = MarginGravityLayoutParams.fromLayoutParams(child.getLayoutParams());
			LinearLayout.LayoutParams childLp = (LinearLayout.LayoutParams) child.getLayoutParams();

			if(childLp.gravity == -1)
			{
				childLp.gravity = Gravity.TOP | Gravity.START;
			}
			if(childLp.gravity == Gravity.TOP)
			{
				childLp.gravity = Gravity.TOP | Gravity.START;
			}
			if(childLp.gravity == Gravity.BOTTOM)
			{
				childLp.gravity = Gravity.BOTTOM | Gravity.START;
			}
			
			if(y + child.getMeasuredHeight() + childLp.topMargin + childLp.bottomMargin > parentBottom){
				
				x += measuredRowHeights.get(rowIndex);
				y = parentTop;
				rowIndex++;
				
			}
			
			//log("row " + rowIndex + " child measured height: " + child.getMeasuredHeight());
			
			onLayoutRect.left = x + childLp.leftMargin;
			onLayoutRect.right = x + measuredRowHeights.get(rowIndex);
			onLayoutRect.top = y + childLp.topMargin;
			onLayoutRect.bottom = onLayoutRect.top + child.getMeasuredHeight();
			
			
			
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
			
			y += childLp.topMargin + child.getMeasuredHeight() + childLp.bottomMargin;
		}
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
			LinearLayout.LayoutParams childLp = (LinearLayout.LayoutParams) child.getLayoutParams();

			if(childLp.gravity == -1)
			{
				childLp.gravity = Gravity.TOP | Gravity.START;
			}
			
			
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
	
	
}



