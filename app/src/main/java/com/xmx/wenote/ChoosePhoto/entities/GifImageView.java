package com.xmx.wenote.ChoosePhoto.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.xmx.wenote.ChoosePhoto.BigPhotoActivity;
import com.xmx.wenote.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GifImageView extends ImageView {

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private String mPath;
    private boolean mTouchable;
    private Movie mMovie;
    private long mMovieStart;
    private int mCurrentAnimationTime = 0;
    private float mLeft;
    private float mTop;
    private float mScale;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setImageMovie(Movie movie) {
        if (movie != null) {
            mMovie = movie;
            requestLayout();
            postInvalidate();
        }
    }

    public void setImageByPath(String path, boolean touchable) {
        mPath = path;
        mTouchable = touchable;
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

    public void setImagePath(String path, boolean touchable) {
        mPath = path;
        mTouchable = touchable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mTouchable && mPath != null) {
                    Intent intent = new Intent(getContext(), BigPhotoActivity.class);
                    intent.putExtra("path", mPath);
                    getContext().startActivity(intent);
                }
        }
        return mTouchable || super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int defaultWidth = MeasureSpec.getSize(widthMeasureSpec);
            float suitableScale = 2.5f;
            int width;

            int mode = MeasureSpec.getMode(widthMeasureSpec);
            switch (mode) {
                case MeasureSpec.EXACTLY:
                    width = defaultWidth;
                    break;

                case MeasureSpec.AT_MOST:
                    int suitableWidth = (int) (mMovie.width() * suitableScale);
                    width = suitableWidth < defaultWidth ? suitableWidth : defaultWidth;
                    break;

                case MeasureSpec.UNSPECIFIED:
                    width = (int) (mMovie.width() * suitableScale);
                    break;

                default:
                    width = 1;
                    break;
            }

            int mMeasuredMovieWidth;
            int mMeasuredMovieHeight;

            mScale = (float) width / (float) movieWidth;
            mMeasuredMovieWidth = width;
            mMeasuredMovieHeight = (int) (movieHeight * mScale);
            if (mode == MeasureSpec.EXACTLY) {
                mMeasuredMovieHeight = MeasureSpec.getSize(heightMeasureSpec);
            }
            mLeft = (mMeasuredMovieWidth - mMovie.width() * mScale) / 2f;
            mTop = (mMeasuredMovieHeight - mMovie.height() * mScale) / 2f;
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
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

}