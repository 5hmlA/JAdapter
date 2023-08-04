package first.lunar.yun.jadapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import sparkj.adapter.JVBrecvAdapter
import sparkj.adapter.face.OnViewClickListener
import sparkj.adapter.holder.JViewHolder
import sparkj.adapter.vb.JViewBean
import sparkj.jadapter.R
import java.util.*
import kotlin.math.absoluteValue

class LayoutManagerActivity : AppCompatActivity(), sparkj.adapter.face.OnViewClickListener<JViewBean> {
    lateinit var recycleview:RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_manager)
        recycleview = findViewById<RecyclerView>(R.id.recv)

        val datas = List(20) {
            Circle(it)
        }

        val adapter = JVBrecvAdapter(datas, this)
//        recycleview.addItemDecoration(CenterItemDirection())
        recycleview.layoutManager = CenterLayoutManager(this)
//        recycleview.layoutManager = StackLayoutManager(this)
//        recycleview.layoutManager = RepeatLayoutManager()
        recycleview.adapter = adapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycleview)
    }

    override fun onItemClicked(view: View?, itemData: JViewBean) {
        recycleview.scrollToPosition(itemData.position)
    }
}

class CenterSnapHelper : LinearSnapHelper() {
    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {

        println("--------------------------${layoutManager.getPosition(targetView)}")
        return super.calculateDistanceToFinalSnap(layoutManager, targetView)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        println("--------findSnapView------------------")
        return super.findSnapView(layoutManager)
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        println("--------------findTargetSnapPosition------------")
        return super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
    }
}

//https://blog.csdn.net/ccy0122/article/details/90515386
//https://juejin.cn/post/6844903924256735239?searchId=202308041034181249C1ABCBFAA26CB9CA
//https://juejin.cn/post/7159888674321072141?searchId=202308041034181249C1ABCBFAA26CB9CA
//https://www.jianshu.com/p/e54db232df62
//https://blog.csdn.net/zxt0601/article/details/52267325
//https://juejin.cn/post/6870770285247725581

class CenterLayoutManager(context: Context) : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {

    inner class LayoutHolder(val view: View, layoutIndex: Int)

    val minScale = .71F
    var currentPosition = 0
    val showedView = TreeMap<Int, View>()

//    override fun onMeasure(recycler: Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
//        if (state.itemCount == 0) {
//            return
//        }
//        val item = recycler.getViewForPosition(0)
//        measureChildWithMargins(item, 0, 0)
//        itemWidth = getDecoratedMeasuredWidth(item)
//        itemHeight = getDecoratedMeasuredHeight(item)
//        setMeasuredDimension(
//            widthSpec,
//            MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY)
//        )
//    }

    fun createItemViewWithPosition(adapterPosition: Int, recycler: Recycler) {
        val view = recycler.getViewForPosition(adapterPosition)
//        toShowViews[i] = view
        measureChildWithMargins(view, 0, 0)
        val itemWidth = getDecoratedMeasuredWidth(view)
        val itemHeight = getDecoratedMeasuredHeight(view)
        val halfItemWidth = itemWidth / 2
//        val left = itemCenter - halfItemWidth
//        val right = itemCenter + halfItemWidth
//        view.left = left
//        view.top = paddingTop
//        view.right = right
//        view.bottom = view.top + itemHeight
    }

    fun View.centerX() = (left + right) / 2

    fun View.layout() = layoutDecoratedWithMargins(this, left, top, right, bottom)

    override fun scrollToPosition(position: Int) {
        currentPosition = position
        requestLayout()
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        if (state.itemCount == 0) {
            //没有Item可布局，就回收全部临时缓存 (参考自带的LinearLayoutManager)
            //这里的没有Item，是指Adapter里面的数据集，
            //可能临时被清空了，但不确定何时还会继续添加回来
            removeAndRecycleAllViews(recycler);
            return;
        }
        //暂时分离和回收全部有效的Item
        detachAndScrapAttachedViews(recycler)
        val toShowViews = TreeMap<Int, View>()
        val center = width / 2
        for (i in currentPosition until state.itemCount) {
            //右边布局  __456
            val view = recycler.getViewForPosition(i)
            toShowViews[i] = view
            measureChildWithMargins(view, 0, 0)
            val itemWidth = getDecoratedMeasuredWidth(view)
            val itemHeight = getDecoratedMeasuredHeight(view)
            val itemCenter = center + (i - currentPosition) * itemWidth
            val halfItemWidth = itemWidth / 2
            val left = itemCenter - halfItemWidth
            val right = itemCenter + halfItemWidth
            view.left = left
            view.top = paddingTop
            view.right = right
            view.bottom = view.top + itemHeight
            if (right > width) {
                //下一个 右边不可见
                break
            }
        }

        for (i in currentPosition - 1 downTo 0) {
            //左边布局 32___
            val view = recycler.getViewForPosition(i)
            toShowViews[i] = view
            measureChildWithMargins(view, 0, 0)
            val itemWidth = getDecoratedMeasuredWidth(view)
            val itemHeight = getDecoratedMeasuredHeight(view)
            val itemCenter = center + (currentPosition - i) * itemWidth
            val halfItemWidth = itemWidth / 2
            val left = itemCenter - halfItemWidth
            val right = itemCenter + halfItemWidth
            view.left = left
            view.top = paddingTop
            view.right = right
            view.bottom = view.top + itemHeight
            if (left < 0) {
                //下一个 左边不可见
                break
            }
        }

        //这里比较做回收操作

        toShowViews.forEach { t, view ->
            addView(view)
            view.layout()
            val scale = ((1 - (view.centerX() - center).absoluteValue / (center / (1 - minScale))))
            view.scaleX = scale
            view.scaleY = scale
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val consume = layoutScrolling(dx, recycler)
        offsetChildrenHorizontal(-dx);
        tryRecycleView(recycler, dx)
        return consume

    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: RecyclerView.State?): Int {
        println("scrollVerticallyBy ------- $dy")
        return 0
    }

    private fun tryRecycleView(recycler: Recycler, dx: Int) {
        if (dx > 0) {
            //尝试回收左边
            for (i in 0 until childCount) {
                val childAt = getChildAt(i) ?: return
                if (childAt.right > 0) {
                    return
                }
                println(" --- onView 回收 左边 $i")
                removeAndRecycleViewAt(i, recycler)//view回收到cache 当cache满了会recycle到pool
            }
        } else {
            //尝试回收右边
            for (i in childCount - 1 downTo 0) {
                val childAt = getChildAt(i) ?: return
                if (childAt.left < width) {
                    return
                }
                println(" --- onView 回收 右边 $i")
                removeAndRecycleViewAt(i, recycler)
            }
        }

    }

    private fun layoutScrolling(dx: Int, recycler: Recycler): Int {
//        dx>0手指右往左滑动 回收前面的
        val center = width / 2
        if (dx > 0) {
            //右边填充
            val lastChild = getChildAt(childCount - 1)!!
            val position = getPosition(lastChild)
            val lastRight = getDecoratedRight(lastChild)
            println("$position right $lastRight ")
            val end = width - paddingEnd
            if (lastRight - dx > end) {
                //可见最后一个滑动后最右边还在屏幕外 不需要填充
                return scaleViews(dx)
            }
            //需要填充
            if (position == itemCount - 1) {
                val lastCenter = (lastRight + getDecoratedLeft(lastChild)) / 2
                //没下一个了 当前可见已经是最后一个
                if (lastCenter - dx < center) {
                    //最后一个 滑动后中心小于中点需要纠正
                    return scaleViews(lastCenter - center)
                } else {
                    return scaleViews(dx)
                }
            }
            val toAddPosition = position + 1
            //右边添加新的一项item
            val toAddView = recycler.getViewForPosition(toAddPosition)
            addView(toAddView)
            measureChildWithMargins(toAddView, 0, 0)
            val itemWidth = getDecoratedMeasuredWidth(toAddView)
            val itemHeight = getDecoratedMeasuredHeight(toAddView)
            toAddView.left = lastRight
            toAddView.right = toAddView.left + itemWidth
            toAddView.top = paddingTop
            toAddView.bottom = toAddView.top + itemHeight
            println("next $toAddPosition right ${toAddView.right} $toAddView")
            toAddView.layout()
            println("$toAddPosition right ${getChildAt(childCount - 1)!!.right} ${getChildAt(childCount - 1)}")
            return scaleViews(dx)
        } else {
            //左边填充
            val firstChild = getChildAt(0)!!
            val position = getPosition(firstChild)
            val firstLeft = getDecoratedLeft(firstChild)
            if (firstLeft - dx < paddingStart) {
                //第一个还没完全可见 不需要添加
                return scaleViews(dx)
            }

            if (position == 0) {
                //第一个数据
                val firstCenter = (firstLeft + getDecoratedRight(firstChild)) / 2
                if (firstCenter - dx > center) {
                    return scaleViews(firstCenter - center)
                } else {
                    return scaleViews(dx)
                }
            }
            //不是第一个数据 需要在左边添加item

            val toAddPosition = position - 1
            //右边添加新的一项item
            val toAddView = recycler.getViewForPosition(toAddPosition)
            addView(toAddView, 0)//添加到首位 最前面
            measureChildWithMargins(toAddView, 0, 0)
            toAddView.right = firstLeft
            toAddView.left = firstLeft - getDecoratedMeasuredWidth(toAddView)
            toAddView.top = paddingTop
            toAddView.bottom = toAddView.top + getDecoratedMeasuredHeight(toAddView)
            toAddView.layout()
            return scaleViews(dx)
        }
    }

    private fun scaleViews(dx: Int): Int {
        // dx>0手指右往左滑动 view x -dx
        val center = width / 2
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: return dx
            //基于滑动后的位置计算缩放值
            val scale = ((1 - (view.centerX() - center - dx).absoluteValue / (center / (1 - minScale))))
            view.scaleX = scale
            view.scaleY = scale
        }
        return dx
    }

}

data class Circle(val index: Int = 1) : JViewBean() {
    override fun onBindViewHolder(
        holder: JViewHolder,
        position: Int,
        payloads: MutableList<Any>?,
        viewClickListener: OnViewClickListener<*>?
    ) {
        println("onBindViewHolder -- $index")
        holder.itemView.background = GradientDrawable().apply {
            color = ColorStateList.valueOf(Color.RED)
            cornerRadius = 300F
        }
        holder.setText(R.id.tv, position.toString())
    }

    override fun bindLayout() = R.layout.layout_mamager_item

    override fun onViewRecycled(holder: JViewHolder) {
        super.onViewRecycled(holder)
        println(" --- onViewRecycled $index")
    }

    override fun onViewDetachedFromWindow(holder: JViewHolder) {
        super.onViewDetachedFromWindow(holder)
        println(" --- onViewDetachedFromWindow $index")
    }


    override fun onViewAttachedToWindow(holder: JViewHolder) {
        super.onViewAttachedToWindow(holder)
        println(" --- onViewAttachedToWindow $index")

    }
}




