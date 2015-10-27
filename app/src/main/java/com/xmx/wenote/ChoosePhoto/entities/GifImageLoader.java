package com.xmx.wenote.ChoosePhoto.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

public class GifImageLoader {

    class Image {
        Bitmap bitmap;
        Movie movie;
    }

    private class ImgBeanHolder {
        GifImageView imageView;
        String path;
        Image image;
    }

    /**
     * 图片缓存的核心类
     */
    private LruCache<String, Image> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 线程池的线程数量，默认为1
     */
    private int mThreadCount = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

    private static GifImageLoader mInstance;

    /**
     * 队列的调度方式
     *
     * @author zhy
     */
    public enum Type {
        FIFO, LIFO
    }

    private GifImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static GifImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (GifImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new GifImageLoader(1, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static GifImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (GifImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new GifImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    private void init(int threadCount, Type type) {
        // loop thread
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Image>(cacheSize) {
            @Override
            protected int sizeOf(String key, Image value) {
                if (value.bitmap != null) {
                    return value.bitmap.getRowBytes() * value.bitmap.getHeight();
                } else if (value.movie != null) {
                    try {
                        File f = new File(key);
                        FileInputStream fis = new FileInputStream(f);
                        return fis.available();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return 0;
            }
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<>();
        mType = type == null ? Type.LIFO : type;

    }

    /**
     * 加载图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final GifImageView imageView, final boolean touchable) {
        // set tag
        imageView.setTag(path);
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    GifImageView imageView = holder.imageView;
                    String path = holder.path;
                    if (imageView.getTag().toString().equals(path)) {
                        Image im = holder.image;
                        if (im.movie != null) {
                            imageView.setImageMovie(im.movie);
                        } else if (im.bitmap != null) {
                            imageView.setImageBitmap(im.bitmap);
                        }
                        imageView.setImagePath(path, touchable);
                    }
                }
            };
        }

        Image im = getImageFromLruCache(path);
        if (im != null) {
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.image = im;
            holder.imageView = imageView;
            holder.path = path;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    Image im = new Image();
                    Movie movie = null;
                    try {
                        movie = Movie.decodeStream(new FileInputStream(path));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (movie != null) {
                        im.movie = movie;
                        im.bitmap = null;
                    } else {
                        im.bitmap = BitmapFactory.decodeFile(path);
                        im.movie = null;
                    }

                    addImageToLruCache(path, im);
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.image = getImageFromLruCache(path);
                    holder.imageView = imageView;
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    mHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }
    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Image getImageFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param im
     */
    private void addImageToLruCache(String key, Image im) {
        if (getImageFromLruCache(key) == null) {
            if (im != null)
                mLruCache.put(key, im);
        }
    }

}
