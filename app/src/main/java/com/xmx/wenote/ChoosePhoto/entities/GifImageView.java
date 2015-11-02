package com.xmx.wenote.ChoosePhoto.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private static final float defaultScale = 2.5f;
    private String mPath;
    private Movie mMovie;
    private Bitmap mBitmap;
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
        mMovie = movie;
        mBitmap = null;
        requestLayout();
        postInvalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        mMovie = null;
        requestLayout();
        postInvalidate();
    }

    public void setImageByPath(String path) {
        mPath = path;
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
        setImageByPathLoader(path, GifImageLoader.Type.LIFO);
    }

    public void setImageByPathLoader(String path, GifImageLoader.Type type) {
        GifImageLoader.getInstance(3, type).loadImage(path, this);
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int defaultWidth = MeasureSpec.getSize(widthMeasureSpec);
            int defaultHeight = MeasureSpec.getSize(heightMeasureSpec);

            boolean widthFlag = ((float) movieWidth / defaultWidth) > ((float) movieHeight / defaultHeight);

            int mMeasuredMovieWidth;
            int mMeasuredMovieHeight;

            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(widthMeasureSpec);

            switch (widthMode) {
                case MeasureSpec.EXACTLY:
                    mMeasuredMovieWidth = defaultWidth;
                    break;

                case MeasureSpec.AT_MOST:
                    int suitableWidth = (int) (movieWidth * defaultScale);
                    mMeasuredMovieWidth = suitableWidth < defaultWidth ? suitableWidth : defaultWidth;
                    break;

                case MeasureSpec.UNSPECIFIED:
                    mMeasuredMovieWidth = (int) (movieWidth * defaultScale);
                    break;

                default:
                    mMeasuredMovieWidth = 1;
                    break;
            }

            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                    mMeasuredMovieHeight = defaultHeight;
                    break;

                case MeasureSpec.AT_MOST:
                    int suitableHeight = (int) (movieHeight * defaultScale);
                    mMeasuredMovieHeight = suitableHeight < defaultHeight ? suitableHeight : defaultHeight;
                    break;

                case MeasureSpec.UNSPECIFIED:
                    mMeasuredMovieHeight = (int) (movieHeight * defaultScale);
                    break;

                default:
                    mMeasuredMovieHeight = 1;
                    break;
            }

            if (widthFlag) {
                mScale = (float) mMeasuredMovieWidth / (float) movieWidth;
                mMeasuredMovieHeight = (int) (movieHeight * mScale);
                if (heightMode == MeasureSpec.EXACTLY) {
                    mMeasuredMovieHeight = defaultHeight;
                }
            } else {
                mScale = (float) mMeasuredMovieHeight / (float) movieHeight;
                mMeasuredMovieWidth = (int) (movieWidth * mScale);
                if (widthMode == MeasureSpec.EXACTLY) {
                    mMeasuredMovieWidth = defaultWidth;
                }
            }

            mLeft = (mMeasuredMovieWidth - movieWidth * mScale) / 2f;
            mTop = (mMeasuredMovieHeight - movieHeight * mScale) / 2f;
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