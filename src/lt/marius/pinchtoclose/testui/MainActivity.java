package lt.marius.pinchtoclose.testui;

import lt.marius.pinchtoclose.MultiFingerGestureDetector;
import lt.marius.pinchtoclose.PinchToClose;
import lt.marius.pinchtoclose.PinchToClose.CustomFinishCallback;
import lt.marius.pinchtoclose.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	MultiFingerGestureDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		ToggleButton button = (ToggleButton)findViewById(R.id.toggleButton1);
		button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PinchToClose.init(MainActivity.this, false, new CustomFinishCallback() {
					
					@Override
					public void finish(Activity activity) {
						Toast t = Toast.makeText(getApplicationContext(), "Activity closed", Toast.LENGTH_SHORT);
						t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 20);
						t.show();
						activity.finish();
					}
				},
				isChecked);
			}
		});
		
		//must be called after setContentView()
//		PinchToClose.init(this); // shorthand version

		//More extensive version
		PinchToClose.init(this, false, new CustomFinishCallback() {
			
			@Override
			public void finish(Activity activity) {
				Toast t = Toast.makeText(getApplicationContext(), "Activity closed", Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 20);
				t.show();
				activity.finish();
			}
		});
		
		
	}
	
	
}
