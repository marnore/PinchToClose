package lt.marius.pinchtoclose.testui;

import lt.marius.pinchtoclose.PinchToClose;
import lt.marius.pinchtoclose.PinchToClose.CustomFinishCallback;
import lt.marius.pinchtoclose.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
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
