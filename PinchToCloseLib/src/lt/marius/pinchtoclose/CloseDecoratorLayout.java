package lt.marius.pinchtoclose;

import lt.marius.pinchtoclose.PinchToClose.CustomFinishCallback;
import lt.marius.pinchtoclose.algo.AreaAlgorithm;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Layout to be inserted in the Android view hierarchy to
 * intercept touch events and control closing animation
 * @author Marius Noreikis
 * Created: Apr 3, 2014
 */
public class CloseDecoratorLayout extends FrameLayout {

	private static boolean DEBUG = true;
	private boolean drawDebug = false;
	
	private static boolean closing = false;
	private boolean closeParentActivities = false;
	private CustomFinishCallback finishCallback;
	private AreaAlgorithm algorithm;
	
	public CloseDecoratorLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CloseDecoratorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CloseDecoratorLayout(Context context) {
		super(context);
		init();
	}
	
	private static Paint paint;
	private void init() {
		closing = false;	//just created no closing
		try {
			activity = Activity.class.cast(getContext());
		} catch (ClassCastException ex) {
			//TODO throw illegal argument exception?
			activity = null;
		}
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(5.f);
		algorithm = AreaAlgorithm.TRIANGLE;
		detector = new MultiFingerGestureDetector(10, gestureListener);
		
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (closing) {
			activity.finish();
		}
		
		if (rootView == null) {
			rootView = getRootView();
		}
	}
	
	private MultiFingerGestureListener gestureListener = new CloseDetector();
	
	private Activity activity;
	
	public void setActivity(Activity a) {
		this.activity = a;
	}
	
	public void setDebugMode(boolean on) {
		DEBUG = on;
	}
	
	private MultiFingerGestureDetector detector;
	private View rootView;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (event.getPointerCount() >= 3) {	//TODO use a combination with detector and dispatch event instead
			return true;
		}
		return super.onInterceptTouchEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (DEBUG && drawDebug) {
			algorithm.visualize(canvas, paint);
		}
	}
	
	public void setAlgorithm(AreaAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	public AreaAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	private void finishActivity() {
		if (finishCallback != null) {
			finishCallback.finish(activity);
		}
		if (activity != null) {
			activity.finish();
			if (closeParentActivities) {
				closing = true;
			}
		}
	}
	

	private class CloseDetector extends MultiFingerAreaListener {
		private static final float RATIO_TO_CLOSE_AFTER_UP = 0.4f;
		private static final float RATIO_TO_SCALE_BACK = 0.8f;
		private static final float RATIO_TO_CLOSE_WHILE_SCALING = 0.33f;
		
		private float ratio = 1;
		private boolean shrinking = false;
		
		@Override
		public void onAreaChange(float deltaArea) {
			shrinking = deltaArea < 0;
		}

		private float startArea = -1;
		@Override
		public void onArea(float newArea) {
			if (DEBUG) {
				invalidate();
			}
			if (startArea == -1) {
				startArea = newArea / ratio;
			} else {
				ratio = Math.min(1, newArea / startArea);
				if (ratio <= 1) {
					ViewHelper.setScaleX(rootView, ratio);
					ViewHelper.setScaleY(rootView, ratio);
					ViewHelper.setAlpha(rootView, ratio);
				}
				if (ratio <= RATIO_TO_CLOSE_WHILE_SCALING) {	//when it is so small just close the activity
					animateClose();
				}
			}
			
		}
		
		@Override
		public void onDown(int fingerCount) {
			if (DEBUG && fingerCount >= 3) {
				drawDebug = true;
			}
			if (fingerCount >= 3) {
				startArea = -1;
			}
			if (fingerCount <= 3) {
				CloseDecoratorLayout.this.setAlgorithm(AreaAlgorithm.TRIANGLE);
			} else {
				CloseDecoratorLayout.this.setAlgorithm(AreaAlgorithm.DELAUNAY);
			}
			setAreaAlgorithm(CloseDecoratorLayout.this.getAlgorithm());
			super.onDown(fingerCount);
		}
		
		@Override
		public void onUp(int fingerCount) {
			if (DEBUG && fingerCount < 3) {
				drawDebug = false;
			}
			startArea = -1;
			
			super.onUp(fingerCount);
			if (fingerCount < 3) {
				if (ratio <= RATIO_TO_CLOSE_AFTER_UP) {
					//close activity
					animateClose();
				} else if (ratio >= RATIO_TO_SCALE_BACK) {
					animateBack();
				} else {
					if (shrinking) {
						animateClose();
					} else {
						animateBack();
					}
				}
			}
		}
		
		private AnimatorListener closeListener = new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				finishActivity();
			}
		};
		
		private void animateClose() {
			detector.setListener(null);	// prevent other actions
			Animator animX = ObjectAnimator.ofFloat(rootView, "scaleX", ratio, Math.min(ratio, 0.3f));
			Animator animY = ObjectAnimator.ofFloat(rootView, "scaleY", ratio, Math.min(ratio, 0.3f));
			Animator animA = ObjectAnimator.ofFloat(rootView, "alpha", ratio, 0.0f);
			AnimatorSet anim = new AnimatorSet();
			anim.playTogether(animX, animY, animA);
			anim.setInterpolator(new DecelerateInterpolator());
			anim.setDuration((long) (300 * ratio / 0.75));
			anim.addListener(closeListener);
			anim.start();
		}
		
		private void animateBack() {
			Animator animX = ObjectAnimator.ofFloat(rootView, "scaleX", ratio, 1f);
			Animator animY = ObjectAnimator.ofFloat(rootView, "scaleY", ratio, 1f);
			Animator animA = ObjectAnimator.ofFloat(rootView, "alpha", ratio, 1f);
			AnimatorSet anim = new AnimatorSet();
			anim.playTogether(animX, animY, animA);
			anim.setInterpolator(new DecelerateInterpolator());
			anim.setDuration((long) (300 * (1 - ratio) / 0.75));
			anim.start();
			ratio = 1;
		}

	}

	public void setCloseAll(boolean closeAll) {
		closeParentActivities = closeAll;
	}


	public void setCustomFinishCallback(CustomFinishCallback callback) {
		this.finishCallback = callback;
	}
	
}
