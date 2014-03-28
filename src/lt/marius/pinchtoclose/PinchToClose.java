package lt.marius.pinchtoclose;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
/**
 * Wrapper static class for initializing PinchToClose
 * @author Marius Noreikis
 * Created: Mar 28, 2014
 */
public class PinchToClose {

	public interface CustomFinishCallback {
		public void finish(Activity activity);
	}
	
	/**
	 * Initialize PinchToClose for the specified activity. The activity
	 * will be closed by pinching at least 3 fingers on the screen
	 * @param activity Activity which should be closed by pinch
	 */
	public static void init(Activity activity) {
		init(activity, false, null);
	}
	
	/**
	 * Initialize PinchToClose for the specified activity. The activity
	 * will be closed by pinching at least 3 fingers on the screen
	 * @param activity Activity which should be closed by pinch
	 * @param closeAll whether to close parent activities
	 * @param callback function to be called for closing the activity. 
	 * Client is responsible for closing the activity. Convenient if you want
	 * to set the activity result etc. before closing the activity
	 */
	public static void init(Activity activity, boolean closeAll, CustomFinishCallback callback) {
		ViewGroup root = (ViewGroup)activity.findViewById(android.R.id.content);
		if (root == null) {
			throw new IllegalArgumentException("Root layout not yet initialized." +
					"This must be called after setContentView()");
		}
		List<View> children = new ArrayList<View>();
		for (int i = 0, n = root.getChildCount(); i < n; i++) {
			children.add(root.getChildAt(i));
		}
		root.removeAllViews();
		
		CloseDecoratorLayout layout = new CloseDecoratorLayout(activity);
		for (View v : children) {
			layout.addView(v);
		}
		
		root.addView(layout);
		layout.setCloseAll(closeAll);
		layout.setCustomFinishCallback(callback);
	}
	
	
	
}
