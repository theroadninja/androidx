package g.p.androidx.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Things that should probably be tested:
 * 
 * 1) layout margins
 * 2) layout padding
 * 3) view margins
 * 4) view padding
 * 5) all gravity values
 * 6) layout   width/height    X   wrap_content/match_parent/fixed value
 * 7) same as #6 but for views
 * 
 * @author Dave
 *
 */
public class TestFlowLayoutActivity extends Activity {
	
	
	private int[] tests = new int[]{ 
			R.layout.merge_flowlayout_test_layout_margin,
			R.layout.merge_flowlayout_test_layout_padding,
			R.layout.merge_flowlayout_test_orientation_vertical
			};
	
	private int testIndex = 0;
	
	private FrameLayout testArea;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_flow_layout);
		
		this.testArea = (FrameLayout)findViewById(R.id.layout_test_area);
		
		setTest(tests[testIndex]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test_flow_layout, menu);
		return true;
	}
	
	public void onClick(View v){
		
		if(v.getId() == R.id.button_change_test){
			testIndex++;
			if(testIndex >= tests.length){
				testIndex = 0;
			}
			setTest(tests[testIndex]);
		}
	}
	
	private void setTest(int layoutId){
		
		this.testArea.removeAllViews();
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(layoutId, testArea);
		
	}

}
