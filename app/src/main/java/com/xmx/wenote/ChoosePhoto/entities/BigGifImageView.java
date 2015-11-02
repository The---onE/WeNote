package com.xmx.wenote.ChoosePhoto.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.xmx.wenote.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BigGifImageView extends ImageView {

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private Movie mMovie;
    private Bitmap mBitmap;
    private long mMovieStart;
    private int mCurrentAnimationTime = 0;
    private float mLeft;
    private float mTop;
    private float mScale;
    private float defaultScale = 2.5f;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    /**
     * 屏幕的分辨率
     */
    private DisplayMetrics dm;

    /**
     * 最小缩放比例
     */
    float minScaleR = 0.5f;

    /**
     * 最大缩放比例
     */
    static final float MAX_SCALE = 15f;

    /**
     * 初始状态
     */
    static final int NONE = 0;
    /**
     * 拖动
     */
    static final int DRAG = 1;
    /**
     * 缩放
     */
    static final int ZOOM = 2;

    /**
     * 当前模式
     */
    int mode = NONE;

    /**
     * 存储float类型的x，y值，就是你点下的坐标的X和Y
     */
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    float scale = 1f;

    public BigGifImageView(Context context) {
        this(context, null);
    }

    public BigGifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
    }

    public BigGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setImageMovie(Movie movie) {
        mMovie = movie;
        mBitmap = null;
        setupMovie();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        mMovie = null;
        setupBitmap();
    }

    public void setImageByPath(String path) {
        Movie movie = null;
        try {
            movie = Movie.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (movie != null) {
            setImageMovie(movie);
        } else {
            setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }

    public void setImageByPathLoader(String path) {
        setImageByPathLoader(path, BigGifImageLoader.Type.LIFO);
    }

    public void setImageByPathLoader(String path, BigGifImageLoader.Type type) {
        BigGifImageLoader.getInstance(3, type).loadImage(path, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int defaultWidth = MeasureSpec.getSize(widthMeasureSpec);
            int defaultHeight = MeasureSpec.getSize(heightMeasureSpec);
            int width = (int) (movieWidth * defaultScale);

            if (width > defaultWidth) {
                width = defaultWidth;
                defaultScale = (float) width / (float) movieWidth;
            }
            mScale = (float) width / (float) movieWidth;
            if (movieHeight * mScale > defaultHeight) {
                mScale = defaultHeight / movieHeight;
                defaultScale = mScale;
            }

            mLeft = (defaultWidth - movieWidth * mScale) / 2f;
            mTop = (defaultHeight - movieHeight * mScale) / 2f;
            setMeasuredDimension(defaultWidth, defaultHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            updateAnimationTime();
            drawMovieFrame(canvas);
            invalidateView();
        } else {
            super.onDraw(canvas);
        }
    }

    @SuppressLint("NewApi")
    private void invalidateView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            invalidate();
        }
    }

    private void updateAnimationTime() {
        if (mMovie != null) {
            long now = android.os.SystemClock.uptimeMillis();
            // 如果第一帧，记录起始时间
            if (mMovieStart == 0) {
                mMovieStart = now;
            }
            // 取出动画的时长
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = DEFAULT_MOVIE_DURATION;
            }
            // 算出需要显示第几帧
            mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
        }
    }

    private void drawMovieFrame(Canvas canvas) {
        if (mMovie != null) {
            // 设置要显示的帧，绘制即可
            mMovie.setTime(mCurrentAnimationTime);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.scale(mScale, mScale);
            mMovie.draw(canvas, mLeft / mScale, mTop / mScale);
            canvas.restore();
        }
    }

    public void setupMovie() {
        requestLayout();
        postInvalidate();
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        prev.set(event.getX(), event.getY());
                        break;
                    // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        scale = defaultScale;
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (spacing(event) > 10f) {
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float tScale = newDist / dist;
                                defaultScale = scale * tScale;
                                requestLayout();
                                postInvalidate();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void setupBitmap() {
        Context context = getContext();
        //获取屏幕分辨率,需要根据分辨率来使用图片居中
        dm = context.getResources().getDisplayMetrics();

        //设置ScaleType为ScaleType.MATRIX，这一步很重要
        this.setScaleType(ScaleType.MATRIX);

        //bitmap为空就不调用center函数
        if (mBitmap != null) {
            int suitableWidth = (int) (mBitmap.getWidth() * defaultScale);
            int width = suitableWidth < dm.widthPixels ? suitableWidth : dm.widthPixels;

            mScale = (float) width / (float) mBitmap.getWidth();

            if (mBitmap.getHeight() * mScale > dm.heightPixels) {
                mScale = (float) dm.heightPixels / (float) mBitmap.getHeight();
            }
            matrix.setScale(mScale, mScale);
            center(true, true);
        }
        this.setImageMatrix(matrix);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        prev.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (spacing(event) > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        //savedMatrix.set(matrix);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - prev.x, event.getY()
                                    - prev.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float tScale = newDist / dist;
                                matrix.postScale(tScale, tScale, mid.x, mid.y);
                            }
                        }
                        break;
                }
                BigGifImageView.this.setImageMatrix(matrix);
                CheckView();
                return true;
            }
        });
    }


    /**
     * 横向、纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = this.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }


    /**
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                //Log.d("", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScaleR);
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                //Log.d("", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
                matrix.set(savedMatrix);
            }
        }
        center(true, true);
    }


    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}