package com.example.dragging

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var isDrag = false

        //状态栏
        val statusHeight = getStatusBarHeight(this)
        //底部导航栏
        val negHeight = getNavigationBarHeight()
        //标题高度
        val titleHeight = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        val dm = resources.displayMetrics
        //获取屏幕宽和高
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels - statusHeight - titleHeight - negHeight

        bt.setOnClickListener {
            if (isDrag){
                Toast.makeText(this, "..........", Toast.LENGTH_LONG).show()
            }
        }


        bt.setOnTouchListener(object : OnTouchListener {
            //上次view的坐标位置
            internal var lastX: Int = 0
            internal var lastY: Int = 0
            var moveX = 0
            var moveY = 0
            var btnHeight = 0
            var btnWith = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                // TODO Auto-generated method stub
                val ea = event.action
                Log.i("TAG", "Touch:$ea")

                when (ea) {
                    MotionEvent.ACTION_DOWN -> {
                        //按下记录view坐标
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                        btnHeight = bt.height
                        btnWith = bt.width
                    }

                    MotionEvent.ACTION_MOVE -> {

                        //移动时记录相对上次的坐标
                        val dx = event.rawX.toInt() - lastX
                        val dy = event.rawY.toInt() - lastY

                        moveX = dx
                        moveY = dy
                        //相对于parent 的View上下左右位置
                        var left = v.left + dx
                        var top = v.top + dy
                        var right = v.right + dx
                        var bottom = v.bottom + dy

                        //如果left < 0，则是左移，右边框上次位置加上左移部分
                        if (left < 0) {
                            left = 0
                            right = left + v.width
                        }

                        //
                        if (right > screenWidth) {
                            right = screenWidth
                            left = right - v.width
                        }

                        //如果top < 0，则是上移，下边框上次位置加上移部分
                        if (top < 0) {
                            top = 0
                            bottom = top + v.height
                        }

                        if (bottom > screenHeight) {
                            bottom = screenHeight
                            top = bottom - v.height
                        }

                        //重新layout
                        v.layout(left, top, right, bottom)

                        Log.i("", "position$left, $top, $right, $bottom")

                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        isDrag = Math.abs(moveX) == 0 && Math.abs(moveY) == 0

                        // 向四周吸附
                        var dx1 = event.rawX - lastX
                        var dy1 =event.rawY - lastY
                        var left1 = v.left + btnWith/2  + dx1
                        var top1 = v.top + dy1
                        var right1 = v .right + dx1
                        var bottom1 = v .bottom + dy1
                        if (left1 < (screenWidth / 2)) {
//                            when {
//                                top1 < 100 -> v.layout(left1.toInt(), 0, right1.toInt(), btnHeight)
//                                bottom1 > (screenHeight - 200) -> v.layout(left1.toInt(), (screenHeight - btnHeight), right1.toInt(), screenHeight)
//                                else ->
//                                    v.layout(0, top1.toInt(), btnHeight, bottom1.toInt())
//                            }
                            v.layout(0, top1.toInt(), btnWith, bottom1.toInt())
                        } else {
//                            when {
//                                top1 < 100 -> v.layout(left1.toInt(), 0, right1.toInt(), btnHeight)
//                                bottom1 > (screenHeight - 200) -> v.layout(left1.toInt(), (screenHeight - btnHeight), right1.toInt(), screenHeight)
//                                else -> v.layout((screenWidth - btnHeight), top1.toInt(), screenWidth, bottom1.toInt())
//                            }
                            v.layout((screenWidth - btnWith), top1.toInt(), screenWidth, bottom1.toInt())
                        }
                    }
                }
                return false
            }
        })
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun getNavigationBarHeight(): Int {
        val resources = this.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height = resources.getDimensionPixelSize(resourceId)
        Log.v("navigation bar>>>", "height:$height")
        return height

    }
}
