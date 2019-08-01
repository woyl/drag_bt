package com.example.dragging

import android.R.attr.y
import android.R.attr.x
import android.opengl.ETC1.getWidth
import android.graphics.PixelFormat
import android.util.DisplayMetrics
import android.content.Context.WINDOW_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.Activity
import android.content.Context
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout


class ChatView//通过像素密度来设置按钮的大小
//屏宽
//屏高
//布局设置
// 设置window type
// 设置图片格式，效果为背景透明
// 设置Window flag
    (context: Context?) : LinearLayout(context) {
    // 悬浮栏位置
    private val LEFT = 0
    private val RIGHT = 1
    private val TOP = 3
    private val BUTTOM = 4

    private var dpi: Int
    private var screenHeight: Int
    private var screenWidth: Int
    private var wmParams: WindowManager.LayoutParams
    private var wm: WindowManager
    private var xx = 0.toFloat()
    private var yy= 0.toFloat()
    private var mTouchStartX: Float = 0.toFloat()
    private var mTouchStartY: Float = 0.toFloat()
    private var isScroll: Boolean = false
    private var activity:Activity?= null


    init {
        LayoutInflater.from(context).inflate(R.layout.view_chat, this)
//        setBackgroundResource(R.drawable.chat_btn)
        wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        activity = context as Activity?
        context.windowManager.defaultDisplay.getMetrics(dm)
        dpi = dpi(dm.densityDpi)
        screenWidth = wm.defaultDisplay.width
        screenHeight = wm.defaultDisplay.height
        wmParams = WindowManager.LayoutParams()
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION
        wmParams.format = PixelFormat.RGBA_8888
        wmParams.gravity = Gravity.LEFT or Gravity.TOP
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        wmParams.width = dpi
        wmParams.height = dpi
        wmParams.y = screenHeight - dpi shr 1
        wm.addView(this, wmParams)
        hide()
    }


    /**
     * 根据密度选择控件大小
     *
     */
    private fun dpi(densityDpi: Int): Int {
        return when {
            densityDpi <= 120 -> 36
            densityDpi <= 160 -> 48
            densityDpi <= 240 -> 72
            densityDpi <= 320 -> 96
            else -> 108
        }
    }

    fun show() {
        if (isShown) {
            return
        }
        visibility = View.VISIBLE
    }


    fun hide() {
        visibility = View.GONE
    }

    fun destory() {
        hide()
        wm.removeViewImmediate(this)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 获取相对屏幕的坐标， 以屏幕左上角为原点
        xx = event.rawX
        yy = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // setBackgroundDrawable(openDrawable);
                // invalidate();
                // 获取相对View的坐标，即以此View左上角为原点
                mTouchStartX = event.x
                mTouchStartY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (isScroll) {
                    updateViewPosition()
                } else {
                    // 当前不处于连续滑动状态 则滑动小于图标1/3则不滑动
                    if (Math.abs(mTouchStartX - event.x) > dpi / 3 || Math.abs(mTouchStartY - event.y) > dpi / 3) {
                        updateViewPosition()
                    } else {
                        return true
                    }
                }
                isScroll = true
            }
            MotionEvent.ACTION_UP -> {
                // 拖动
                if (isScroll) {
                    autoView()
                    // setBackgroundDrawable(closeDrawable);
                    // invalidate();
                } else {
                    // 当前显示功能区，则隐藏
                    // setBackgroundDrawable(openDrawable);
                    // invalidate();

                }
                isScroll = false
                mTouchStartY = 0f
                mTouchStartX = mTouchStartY
            }
        }
        return true
    }

    /**
     * 自动移动位置
     */
    private fun autoView() {
        // 得到view在屏幕中的位置
        val location = IntArray(2)
        getLocationOnScreen(location)
        //左侧
        if (location[0] < screenWidth / 2 - width / 2) {
            updateViewPosition(LEFT)
        } else {
            updateViewPosition(RIGHT)
        }
    }

    /**
     * 手指释放更新悬浮窗位置
     *
     */
    private fun updateViewPosition(l: Int) {
        when (l) {
            LEFT -> wmParams.x = 0
            RIGHT -> {
                val x = screenWidth - dpi
                wmParams.x = x
            }
            TOP -> wmParams.y = 0
            BUTTOM -> wmParams.y = screenHeight - dpi
        }
        wm.updateViewLayout(this, wmParams)
    }

    // 更新浮动窗口位置参数
    private fun updateViewPosition() {
        wmParams.x = (xx - mTouchStartX).toInt()
        //是否存在状态栏（提升滑动效果）
        // 不设置为全屏（状态栏存在） 标题栏是屏幕的1/25
        wmParams.y = (yy - mTouchStartY - (screenHeight / 25).toFloat()).toInt()
        wm.updateViewLayout(this, wmParams)
    }
}