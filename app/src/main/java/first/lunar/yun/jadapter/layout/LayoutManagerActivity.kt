package first.lunar.yun.jadapter.layout

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import sparkj.adapter.JVBrecvAdapter
import sparkj.adapter.face.OnViewClickListener
import sparkj.adapter.holder.JViewHolder
import sparkj.adapter.vb.JViewBean
import sparkj.jadapter.R
import java.util.*

class LayoutManagerActivity : AppCompatActivity(), sparkj.adapter.face.OnViewClickListener<JViewBean> {
    lateinit var recycleview: RecyclerView

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
        recycleview.adapter = adapter
//        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(recycleview)
    }

    override fun onItemClicked(view: View?, itemData: JViewBean) {
//        recycleview.scrollToPosition(itemData.position)
        recycleview.smoothScrollToPosition(itemData.position)
//        if ((itemData.position + 1) % 2 == 0) {
//            recycleview.smoothScrollToPosition(itemData.position)
//        } else if ((itemData.position + 1) % 3 == 0) {
//            recycleview.smoothScrollToPosition(itemData.position + 3)
//        } else {
//            recycleview.scrollToPosition(itemData.position)
//        }
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


class CenterLayoutManager(context: Context) : RecyclerView.LayoutManager(), SmoothScroller.ScrollVectorProvider {

    var currentPosition = 0

//    val layoutStrategy: LayoutStrategy = HorizotionCenter()
//    val layoutStrategy: LayoutStrategy = Horizotion(2)
//    val layoutStrategy: LayoutStrategy = Vertical(2)
//    val layoutStrategy: LayoutStrategy = VerticalCenter(2)
    val layoutStrategy: LayoutStrategy = VerticalCenter2(2)
//    val layoutStrategy: LayoutStrategy = AllCenter()

    init {
        isAutoMeasureEnabled = true
    }

    fun createItemViewWithPosition(
        recycler: Recycler,
        adapterPosition: Int,
        left: Axis? = null,
        right: Axis? = null,
        top: Axis? = null,
        bottom: Axis? = null,
    ): View {
        val view = recycler.getViewForPosition(adapterPosition)
        val param = view.layoutParams as RecyclerView.LayoutParams
        if (param.leftMargin > 0 || param.topMargin > 0 || param.rightMargin > 0 || param.bottomMargin > 0) {
            throw RuntimeException("no support margin set in child")
        }
        measureChild(view, 0, 0)
        val itemWidth = getDecoratedMeasuredWidth(view)
        val itemHeight = getDecoratedMeasuredHeight(view)
        left?.apply {
            if (center) {
                val halfWidth = itemWidth / 2
                view.left = value - halfWidth
            } else {
                view.left = value
            }
            view.right = view.left + itemWidth
        } ?: right?.apply {
            if (center) {
                val halfWidth = itemWidth / 2
                view.right = value + halfWidth
            } else {
                view.right = value
            }
            view.left = view.right - itemWidth
        }

        top?.apply {
            if (center) {
                val halfHeight = itemHeight / 2
                view.top = value - halfHeight
            } else {
                view.top = value
            }
            view.bottom = view.top + itemHeight
        } ?: bottom?.apply {
            if (center) {
                val halfHeight = itemHeight / 2
                view.bottom = value + halfHeight
            } else {
                view.bottom = value
            }
            view.top = view.bottom - itemHeight
        }
        return view
    }

    fun View.centerX() = (getDecoratedLeft(this) + getDecoratedRight(this)) / 2

    fun View.centerY() = (getDecoratedTop(this) + getDecoratedBottom(this)) / 2

    fun View.decoratedLeft() = getDecoratedLeft(this)
    fun View.decoratedRight() = getDecoratedRight(this)
    fun View.decoratedTop() = getDecoratedTop(this)
    fun View.decoratedBottom() = getDecoratedBottom(this)

    fun View.layout() = layoutDecorated(this, left, top, right, bottom)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0) {
            //没有Item可布局，就回收全部临时缓存 (参考自带的LinearLayoutManager)
            //这里的没有Item，是指Adapter里面的数据集，
            //可能临时被清空了，但不确定何时还会继续添加回来
            removeAndRecycleAllViews(recycler);
            return;
        }
        layoutStrategy.attach(this, recycler, state)
        //暂时分离全部有效的Item 到scrap ，scrap缓存中的view直接拿来显示不会调用onBindviewHolder
        // 并不会立刻触发viewDetachedFromWindow 因为接下来可能就需要立刻用到
        // 但实际会从recycleview中removeview 执行detachAndScrapAttachedViews后childCount为0
        detachAndScrapAttachedViews(recycler)
        val toShowViews = TreeMap<Int, View>()

        //第一个item的起点的中心
        layoutStrategy.layoutForEach(this, recycler, state, currentPosition) { row, right, position, left, top ->
            val view = createItemViewWithPosition(
                recycler, position,
                left = left,
                top = top
            )
            toShowViews[position] = view
            view
        }

        toShowViews.values.forEach { view ->
            addView(view)
            view.layout()
        }
        transformViewAndConsume(0, 0)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(-2,-2)

    override fun canScrollHorizontally(): Boolean {
        return (layoutStrategy.orientation() ?: RecyclerView.HORIZONTAL) == RecyclerView.HORIZONTAL
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val consume = layoutScrolling(dx, recycler, state)
        offsetChildrenHorizontal(-consume);
        tryRecycleHorizontallyView(recycler, dx)
        return consume

    }

    override fun canScrollVertically(): Boolean {
        return (layoutStrategy.orientation() ?: RecyclerView.VERTICAL) == RecyclerView.VERTICAL
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        val consume = layoutVerticalScrolling(dy, recycler, state)
        offsetChildrenVertical(-consume);
        tryRecycleVerticalView(recycler, dy)
        return consume
    }

    private fun tryRecycleVerticalView(recycler: Recycler, dy: Int) {
        if (dy > 0) {
            //视图向上滑动 尝试回收 顶部
            for (i in 0 until childCount) {
                val childAt = getChildAt(i) ?: continue
                if (childAt.decoratedBottom() > 0) {
                    continue
                }
                removeAndRecycleViewAt(i, recycler)//view回收到cache 当cache满了会recycle到pool
            }
        } else {
            //视图向下滑动 尝试回收 底部
            for (i in childCount - 1 downTo 0) {
                val childAt = getChildAt(i) ?: continue
                if (childAt.decoratedTop() < height) {
                    continue
                }
                removeAndRecycleViewAt(i, recycler)
            }
        }
    }

    private fun layoutVerticalScrolling(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        if (dy > 0) {
            //视图向上滑动 尝试补充 底部
            val bottomChild = getChildAt(childCount - 1) ?: return 0
            if (bottomChild.decoratedBottom() - dy > height) {
                return transformViewAndConsume(0, dy)
            }
            val adapterPosition = getPosition(bottomChild)
            if (layoutStrategy.isEndPosition(adapterPosition, false)) {
                val consume = layoutStrategy.forwardScrollConsume(bottomChild, dy = dy, layoutManager = this)
                return transformViewAndConsume(0, consume)
            }

            val lastChildBottom = bottomChild.decoratedBottom()
            layoutStrategy.addBlockViewsAtPosition(
                this, recycler, adapterPosition + 1,
                top = lastChildBottom.normal()
            )
        } else {
            //视图向下滑动 尝试补充 顶部
            val topchild = getChildAt(0) ?: return 0
            if (topchild.decoratedTop() - dy < 0) {
                return transformViewAndConsume(0, dy)
            }
            val adapterPosition = getPosition(topchild)
            if (layoutStrategy.isStartPosition(adapterPosition, false)) {
                val consume = layoutStrategy.reverseScrollConsume(topchild, dy = dy, layoutManager = this)
                return transformViewAndConsume(0, consume)
            }
            val lastChildTop = topchild.decoratedTop()
            layoutStrategy.addBlockViewsAtPosition(
                this, recycler, adapterPosition - 1,
                bottom = lastChildTop.normal()
            )
        }
        return transformViewAndConsume(0, dy)
    }

    private fun tryRecycleHorizontallyView(recycler: Recycler, dx: Int) {
        if (dx > 0) {
            //dx>0手指右往左滑动 回收前面的
            //尝试回收左边
            for (i in 0 until childCount) {
                val childAt = getChildAt(i) ?: continue
                if (childAt.decoratedRight() > 0) {
                    continue
                }
                removeAndRecycleViewAt(i, recycler)//view回收到cache 当cache满了会recycle到pool
            }
        } else {
            //尝试回收右边
            for (i in childCount - 1 downTo 0) {
                val childAt = getChildAt(i) ?: continue
                if (childAt.decoratedLeft() < width) {
                    continue
                }
                removeAndRecycleViewAt(i, recycler)
            }
        }
    }


    private fun tryAddViewToRight(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        //右边填充
        val visibleLastChild = getChildAt(childCount - 1) ?: return 0
        val lastRight = getDecoratedRight(visibleLastChild)
        if (lastRight - dx > width) {
            //可见最后一个滑动后最右边还在屏幕外 不需要填充
            return transformViewAndConsume(dx, 0)
        }
        //需要填充
        val position = getPosition(visibleLastChild)
        if (layoutStrategy.isEndPosition(position, true)) {
            //没下一个了 当前可见已经是最后一个
            val consume = layoutStrategy.reverseScrollConsume(visibleLastChild, dx = dx, layoutManager = this)
            return transformViewAndConsume(consume, 0)
        }

        val toAddPosition = position + 1
        //右边添加新的一项item
        layoutStrategy.addBlockViewsAtPosition(this, recycler, toAddPosition, left = lastRight.normal())
        return transformViewAndConsume(dx, 0)
    }

    private fun layoutScrolling(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
//        dx>0手指右往左滑动 回收前面的
        if (dx > 0) {
            //右边填充
            return tryAddViewToRight(dx, recycler, state)
        } else {
            return tryAddViewToLeft(dx, recycler, state)
        }
    }

    private fun tryAddViewToLeft(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        //dx<0 左边填充
        val visibleFirstChild = getChildAt(0) ?: return 0
        val adapterPosition = getPosition(visibleFirstChild)
        val firstLeft = getDecoratedLeft(visibleFirstChild)
        if (firstLeft - dx < 0) {
            //第一个还没完全可见 不需要添加
            return transformViewAndConsume(dx, 0)
        }
        if (layoutStrategy.isStartPosition(adapterPosition, true)) {
            //第一个item
            val consume = layoutStrategy.forwardScrollConsume(visibleFirstChild, dx = dx, layoutManager = this)
            return transformViewAndConsume(consume, 0)
        }
        //不是第一个数据 需要在左边添加item
        val toAddPosition = adapterPosition - 1
        //右边添加新的一项item
        layoutStrategy.addBlockViewsAtPosition(this, recycler, toAddPosition, right = firstLeft.normal())
        return transformViewAndConsume(dx, 0)
    }

    private fun transformViewAndConsume(dx: Int, dy: Int): Int {
        // dx>0手指右往左滑动 view x -dx
        layoutStrategy.transformViews(dx, dy,this)
        return if (dx == 0) dy else dx
    }


    override fun scrollToPosition(position: Int) {
        currentPosition = position
        requestLayout()
    }


    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val scroller = object : LinearSmoothScroller(recyclerView.context) {
            //当target可见的时候
            //计算view滚动到目标位置需要的距离
            override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
                val layoutManager = this.layoutManager
                return if (layoutManager != null && layoutManager.canScrollHorizontally()) {
                    val params = view!!.layoutParams as RecyclerView.LayoutParams
                    val left = layoutManager.getDecoratedLeft(view) - params.leftMargin
                    val right = layoutManager.getDecoratedRight(view) + params.rightMargin
                    //目标位置是中间
                    val box = layoutStrategy.smoothScrollToMakeVisibleBox(layoutManager, view, true)
                    calculateDtToFit(left, right, box.first, box.second, snapPreference)
                } else {
                    0
                }
            }

            override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
                val layoutManager = this.layoutManager
                return if (layoutManager != null && layoutManager.canScrollVertically()) {
                    val params = view!!.layoutParams as RecyclerView.LayoutParams
                    val top = layoutManager.getDecoratedTop(view) - params.topMargin
                    val bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin
                    //目标位置是中间
                    val box = layoutStrategy.smoothScrollToMakeVisibleBox(layoutManager, view, false)
                    calculateDtToFit(top, bottom, box.first, box.second, snapPreference)
                } else {
                    0
                }
            }
        }
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }


    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        //找到目标位置当前的position
        val position = layoutStrategy.currentAdapterPositionAtTargetLocation(this)
        return PointF(if (position < targetPosition) 1F else -1F, 0F)
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
        holder.setText(R.id.tv, (position + 1).toString())
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




