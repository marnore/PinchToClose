package lt.marius.pinchtoclose;

import lt.marius.pinchtoclose.PinchToClose.CustomFinishCallback;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class CloseDecoratorLayout extends FrameLayout {

	private static final boolean DEBUG = false;
	private static boolean closing = false;
	private static int componentCount = 0;
	private boolean closeParentActivities = false;
	private CustomFinishCallback finishCallback;
	
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
	
	private void init() {
//		componentCount++;
		closing = false;	//just created no closing
		setBackgroundColor(Color.parseColor("#FE000000"));
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
		detector = new MultiFingerGestureDetector(10, gestureListener);
//		detector.setListener(gestureListener);
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (closing) {
			activity.finish();
		}
		
		if (rootView == null) {
			rootView = getRootView();
			rootView.setBackgroundColor(Color.parseColor("#FE000000"));
		}
	}
	
	private float initialLen = -1;
	private float ratio = 1;
	
	private float dist(float x, float y, float x2, float y2) {
		return (float) Math.sqrt( (x2 - x) * (x2 - x) + (y2 - y) * (y2 - y) );
	}
	
	private MultiFingerGestureListener gestureListener = new CloseDetector();
	
	private Activity activity;
	
	public void setActivity(Activity a) {
		this.activity = a;
	}
	
	private MultiFingerGestureDetector detector;
	private View rootView;
	float lines[][] = new float[4][];
	
	float startX, startY;
	float currX = 0, currY = 0;
	float dx, dy;
	boolean moving = false;
	
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
		// TODO Auto-generated method stub
//		canvas.rotate(180, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
//		canvas.translate(100, 0);
//		Bitmap bmp = Bitmap.createBitmap(getMeasuredWidth() / 2, getMeasuredHeight() / 4,
//				Bitmap.Config.ARGB_4444);
//		Canvas c = new Canvas();
//		c.setBitmap(bmp);
//		canvas.save();
//		canvas.scale(ratio, ratio, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
		super.dispatchDraw(canvas);
//		canvas.restore();
		if (DEBUG && lines != null && lines[0] != null) {
			int i, n = lines[2].length;
			for (i = 0; i < n - 1; i++) {
				canvas.drawLine(lines[2][i], lines[3][i], lines[2][i + 1], lines[3][i + 1], paint);
			}
			if (n > 2) {
				canvas.drawLine(lines[2][0], lines[3][0], lines[2][n - 1], lines[3][n - 1], paint);
			}
		}
//		if (!moving) {
//			super.dispatchDraw(canvas);
//		}
//		canvas.drawRect(new Rect(10, 10, 20, 20), paint);
//		if (moving) {
//			canvas.drawColor(Color.WHITE);
//			canvas.drawBitmap(bmp, currX, currY, paint);
//		}
	}
	
	private static Paint paint;
	
	@Override
	public void draw(Canvas canvas) {
//		canvas.rotate(180);
//		canvas.save();
		super.draw(canvas);
//		
//		canvas.restore();
//		canvas.drawRect(0, 0, 20, 20, paint);
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
	
	@Override
	protected void onDetachedFromWindow() {
		System.out.println("onDetachedFromWindow");
		super.onDetachedFromWindow();
	}

	private class CloseDetector extends AdvancedMultiFingerGestureListener {
		@Override
		public void onDeltaMove(float[] dx, float[] dy) {
		}

		@Override
		public void onMove(float[] startX, float[] startY, float[] endX,
				float[] endY) {
			super.onMove(startX, startY, endX, endY);
			lines[0] = startX;
			lines[1] = startY;
			lines[2] = endX;
			lines[3] = endY;
			invalidate();
			if (endX.length == 2 && false) {	//2 fingers
				if (initialLen == -1) {
					initialLen = dist(endX[0], endY[0], endX[1], endY[1]);
				} else {
					rootView.setScaleX(ratio);
					rootView.setScaleY(ratio);
					ratio = dist(endX[0], endY[0], endX[1], endY[1]) / initialLen; 
				}
			}
		}
		
		private boolean shrinking = false;
		@Override
		public void onAreaChange(float deltaArea) {
			// TODO Auto-generated method stub
			shrinking = deltaArea < 0;
		}

		private float startArea = -1;
		@Override
		public void onArea(float newArea) {
			// TODO Auto-generated method stub
			if (startArea == -1) {
				startArea = newArea;
			} else {
				ratio = Math.min(1, newArea / startArea);
				if (ratio <= 1) {
					rootView.setScaleX(ratio);
					rootView.setScaleY(ratio);
					rootView.setAlpha(ratio);
				}
			}
			
		}
		
		@Override
		public void onUp(int fingerCount) {
			if (ratio <= 0.4) {
				//close activity
				animateClose();
			} else if (ratio >= 0.8) {
				animateBack();
			} else {
				if (shrinking) {
					animateClose();
				} else {
					animateBack();
				}
			}
		}
		
		private AnimatorListener closeListener = new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				finishActivity();
			}
		};
		
		private void animateClose() {
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
		}

		@Override
		public void onScale(float scaleFactor) {
		}

		@Override
		public void onRotateDelta(float delatAngle) {
		}

		@Override
		public void onRotate(float angle) {
		}
	}

	public void setCloseAll(boolean closeAll) {
		closeParentActivities = closeAll;
	}

	public void setCustomFinishCallback(CustomFinishCallback callback) {
		this.finishCallback = callback;
	}
	
}