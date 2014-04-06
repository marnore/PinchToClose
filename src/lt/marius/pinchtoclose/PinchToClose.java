package lt.marius.pinchtoclose;

import java.util.ArrayList;
import java.util.List;

import lt.marius.pinchtoclose.PinchToClose.CustomFinishCallback;

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
		init(activity, false, null, false);
	}
	
	/**
	 * Initialize PinchToClose for the specified activity. The activity
	 * will be closed by pinching at least 3 fingers on the screen
	 * @param activity Activity which should be closed by pinch
	 * @param closeAll whether to close parent activities
	 * @param callback function to be called for closing the activity. 
	 * Convenient if you want to set the activity result etc. before 
	 * closing the activity
	 */
	public static void init(Activity activity, boolean closeAll, CustomFinishCallback callback) {
		init(activity, closeAll, callback, false);
	}
	
	/**
	 * Initialize PinchToClose for the specified activity. The activity
	 * will be closed by pinching at least 3 fingers on the screen
	 * @param activity Activity which should be closed by pinch
	 * @param closeAll whether to close parent activities
	 * @param callback function to be called for closing the activity. 
	 * @param debug whether or not draw debugging information on screen
	 * Convenient if you want to set the activity result etc. before 
	 * closing the activity
	 */
	public static void init(Activity activity, boolean closeAll, CustomFinishCallback callback, boolean debug) {
		ViewGroup root = (ViewGroup)activity.findViewById(android.R.id.content);
		if (root == null) {
			throw new IllegalArgumentException("Root layout not yet initialized." +
					"This must be called after setContentView()");
		}
		CloseDecoratorLayout layout = new CloseDecoratorLayout(activity);
		layout.setDebugMode(debug);
		layout.setId(CLOSE_DECORATOR_ID);
		ViewGroup existing = (ViewGroup)root.findViewById(CLOSE_DECORATOR_ID);
		if (existing != null) {
			removeView(root, existing);	//prevent reinitialisation problems
		}
		insertView(root, layout);
		layout.setCloseAll(closeAll);
		layout.setCustomFinishCallback(callback);
	}
	
	private static final int CLOSE_DECORATOR_ID = 0x7f06ffff;
	
	private static void insertView(ViewGroup parent, ViewGroup added) {
		List<View> children = new ArrayList<View>();
		for (int i = 0, n = parent.getChildCount(); i < n; i++) {
			children.add(parent.getChildAt(i));
		}
		parent.removeAllViews();
		
		for (View v : children) {
			added.addView(v);
		}
		
		parent.addView(added);
	}
	
	private static void removeView(ViewGroup parent, ViewGroup remove) {
		List<View> children = new ArrayList<View>();
		for (int i = 0, n = remove.getChildCount(); i < n; i++) {
			children.add(remove.getChildAt(i));
		}
		remove.removeAllViews();
		
		parent.removeView(remove);
		for (View v : children) {
			parent.addView(v);
		}
		
	}	
	
	
}
