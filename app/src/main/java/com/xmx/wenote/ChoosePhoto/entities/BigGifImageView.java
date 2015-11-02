package com.xmx.wenote.ChoosePhoto.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.xmx.wenote.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BigGifImageView extends ImageView {

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private String mPath;
    private Movie mMovie;
    private Bitmap mBitmap;
    private long mMovieStart;
    private int mCurrentAnimationTime = 0;
    private float mLeft;
    private float mTop;
    private float mScale;
    private float defaultScale = 2.5f;

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
        if (movie != null) {
            mMovie = movie;
            mBitmap = null;
            requestLayout();
            postInvalidate();
        }
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
        setImageByPathLoader(path, BigGifImageLoader.Type.LIFO);
    }
    public void setImageByPathLoader(String path, BigGifImageLoader.Type type) {
        BigGifImageLoader.getInstance(3, type).loadImage(path, this);
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void setScale(int scale) {
        defaultScale = scale;
        requestLayout();
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int defaultWidth = MeasureSpec.getSize(widthMeasureSpec);
            int width;

            int mode = MeasureSpec.getMode(widthMeasureSpec);
            switch (mode) {
                case MeasureSpec.EXACTLY:
                    width = defaultWidth;
                    break;

                case MeasureSpec.AT_MOST:
                    int suitableWidth = (int) (movieWidth * defaultScale);
                    width = suitableWidth < defaultWidth ? suitableWidth : defaultWidth;
                    break;

                case MeasureSpec.UNSPECIFIED:
                    width = (int) (movieWidth * defaultScale);
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
            mLeft = (mMeasuredMovieWidth - movieWidth * mScale) / 2f;
            mTop = (mMeasuredMovieHeight - movieHeight * mScale) / 2f;
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
        } else if (mBitmap != null) {
            int bitmapWidth = mBitmap.getWidth();
            int bitmapHeight = mBitmap.getHeight();
            int defaultWidth = MeasureSpec.getSize(widthMeasureSpec);
            int width;

            int mode = MeasureSpec.getMode(widthMeasureSpec);
            switch (mode) {
                case MeasureSpec.EXACTLY:
                    width = defaultWidth;
                    break;

                case MeasureSpec.AT_MOST:
                    int suitableWidth = (int) (bitmapWidth * defaultScale);
                    width = suitableWidth < defaultWidth ? suitableWidth : defaultWidth;
                    break;

                case MeasureSpec.UNSPECIFIED:
                    width = (int) (bitmapWidth * defaultScale);
                    break;

                default:
                    width = 1;
                    break;
            }

            int mMeasuredBitmapWidth;
            int mMeasuredBitmapHeight;

            mScale = (float) width / (float) bitmapWidth;
            mMeasuredBitmapWidth = width;
            mMeasuredBitmapHeight = (int) (bitmapHeight * mScale);
            if (mode == MeasureSpec.EXACTLY) {
                mMeasuredBitmapHeight = MeasureSpec.getSize(heightMeasureSpec);
            }
            setMeasuredDimension(mMeasuredBitmapWidth, mMeasuredBitmapHeight);
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