package first.lunar.yun.jadapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.View.MeasureSpec
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
import sparkj.adapter.JVBrecvAdapter
import sparkj.adapter.face.OnViewClickListener
import sparkj.adapter.holder.JViewHolder
import sparkj.adapter.vb.JViewBean
import sparkj.jadapter.R

class LayoutManagerActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_manager)
        val recycleview = findViewById<RecyclerView>(R.id.recv)

        val datas = List(20) {
            Circle(it)
        }

        val adapter = JVBrecvAdapter(datas)
//        recycleview.addItemDecoration(CenterItemDirection())
//        recycleview.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        recycleview.layoutManager = CenterLayoutManager(this)
        recycleview.adapter = adapter
//        val snapHelper = CenterSnapHelper()
//        snapHelper.attachToRecyclerView(recycleview)
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

//class CenterItemDirection : RecyclerView.ItemDecoration() {
//    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//        val position = parent.getChildAdapterPosition(view)
//        if (position == 0) {
//            outRect.set(200, 0, 0, 0)
//        }
//        println("$position == ${parent.adapter!!.itemCount}")
//        if (position == parent.adapter!!.itemCount - 1) {
//            outRect.set(0, 0, 200, 0)
//        }
//    }
//}

class CenterLayoutManager(context: Context) :
    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
    val minScale = .71F
    var currentPosition = 0
    val showedView = SparseArray<View>()
    var itemWidth: Int = 0
    var itemHeight: Int = 0

    override fun onMeasure(recycler: Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        if (state.itemCount == 0) {
            return
        }
        val item = recycler.getViewForPosition(0)
        measureChildWithMargins(item, 0, 0)
        itemWidth = getDecoratedMeasuredWidth(item)
        itemHeight = getDecoratedMeasuredHeight(item)
        setMeasuredDimension(
            widthSpec,
            MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0 || itemWidth == 0) {
            //没有Item可布局，就回收全部临时缓存 (参考自带的LinearLayoutManager)
            //这里的没有Item，是指Adapter里面的数据集，
            //可能临时被清空了，但不确定何时还会继续添加回来
            removeAndRecycleAllViews(recycler);
            return;
        }
        //暂时分离和回收全部有效的Item
        detachAndScrapAttachedViews(recycler)
        val center = width / 2
        for (i in currentPosition until state.itemCount) {
            //右边布局
            val itemCenter = center + i * itemWidth
            val halfItemWidth = itemWidth / 2
            val left = itemCenter - halfItemWidth
            val right = itemCenter + halfItemWidth
            if (left > width) {
                //右边不可见
                println("右边不可见 > $i $left $width")
                break
            }
            val view = recycler.getViewForPosition(i)
            showedView[i] = view
            addView(view)
            println("parent add view $i " + view.parent)
            layoutDecoratedWithMargins(view, left, 0, right, itemHeight)
//            val scale = ((1 - (itemCenter - center).absoluteValue / (center / (1 - minScale))))
//            view.scaleX = scale
//            view.scaleY = scale
        }

        for (i in currentPosition - 1 downTo 0) {
            //右边布局
            val itemCenter = center + i * itemWidth
            val halfItemWidth = itemWidth / 2
            val left = itemCenter - halfItemWidth
            val right = itemCenter + halfItemWidth
            if (right < 0) {
                //左边不可见
                println("左边不可见 > " + i)
                break
            }
            val view = recycler.getViewForPosition(i)
            showedView[i] = view
            addView(view)
            layoutDecoratedWithMargins(view, left, 0, right, itemHeight)
//            val scale = ((1 - (itemCenter - center).absoluteValue / (center / (1 - minScale))))
//            view.scaleX = scale
//            view.scaleY = scale
        }
//        val child = recycler.getViewForPosition(i)
//        measureChildWithMargins(child, 0, 0)
//        addView(child)
//        val left = lastRight
//        val right = left + getDecoratedMeasuredWidth(child)
//        val top = paddingTop
//        val bottom = top + getDecoratedMeasuredHeight(child)
//        layoutDecoratedWithMargins(child, left, right, top, bottom)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }


    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {

        val consume = layoutScrolling(dx, recycler, state)
        offsetChildrenHorizontal(-dx);
        tryRecycleView(recycler, dx)
//        onLayoutChildren(recycler, state)
        return consume

    }

    private fun tryRecycleView(recycler: Recycler, dx: Int) {
        if (dx > 0) {
            //尝试回收左边
            for (i in 0 until childCount) {
                if (getChildAt(i)!!.right > 0) {
                    return
                }
                removeAndRecycleViewAt(i, recycler)
            }
        } else {
            //尝试回收右边
            for (i in childCount - 1 downTo 0) {
                if (getChildAt(i)!!.left < width) {
                    return
                }
                removeAndRecycleViewAt(i, recycler)
            }
        }

    }

    fun layoutScrolling(dx: Int, recycler: Recycler, state: State): Int {
//        dx>0手指右往左滑动 回收前面的
        val center = width / 2
        if (dx > 0) {
            //右边填充
            val lastChild = getChildAt(childCount - 1)!!
            val position = getPosition(lastChild)
            val lastRight = getDecoratedRight(lastChild)
            val end = width - paddingEnd
            if (lastRight - dx > end) {
                //可见最后一个滑动后最右边还在屏幕外 不需要填充
                return dx
            }
            //需要填充
            if (position == itemCount - 1) {
                val lastCenter = (lastRight + getDecoratedLeft(lastChild)) / 2
                //没下一个了 当前可见已经是最后一个
                if (lastCenter - dx < center) {
                    //最后一个 滑动后中心小于中点需要纠正
                    return lastCenter - center
                } else {
                    return dx;
                }
            }
            val toAddPosition = position + 1
            //右边添加新的一项item
            val toAddView = recycler.getViewForPosition(toAddPosition)
            measureChildWithMargins(toAddView, 0, 0)
            addView(toAddView)
            val left = lastRight
            val right = left + getDecoratedMeasuredWidth(toAddView)
            val top = paddingTop
            val bottom = top + getDecoratedMeasuredHeight(toAddView)
            layoutDecoratedWithMargins(toAddView, left, right, top, bottom)
            return dx
        } else {
            //左边填充
            val firstChild = getChildAt(0)!!
            val position = getPosition(firstChild)
            val firstLeft = getDecoratedLeft(firstChild)
            if (firstLeft - dx < paddingStart) {
                //第一个还没完全可见 不需要添加
                return dx
            }

            if (position == 0) {
                //第一个数据
                val firstCenter = (firstLeft + getDecoratedRight(firstChild)) / 2
                if (firstCenter - dx > center) {
                    return firstCenter - center
                } else {
                    return dx
                }
            }
            //不是第一个数据 需要在左边添加item

            val toAddPosition = position - 1
            //右边添加新的一项item
            val toAddView = recycler.getViewForPosition(toAddPosition)
            measureChildWithMargins(toAddView, 0, 0)
            addView(toAddView)
            val right = firstLeft
            val left = firstLeft - getDecoratedMeasuredWidth(toAddView)
            val top = paddingTop
            val bottom = top + getDecoratedMeasuredHeight(toAddView)
            layoutDecoratedWithMargins(toAddView, left, right, top, bottom)
            return dx
        }
    }

    override fun canScrollVertically(): Boolean {
        return false
    }


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return super.generateDefaultLayoutParams()
    }


    fun layout(recycler: RecyclerView.Recycler, state: RecyclerView.State, scroll: Boolean = false) {

//        dx>0手指右往左滑动 回收前面的
        val center = width / 2
        var lastItemCenter = 0
        val halfItemWidth = itemWidth / 2
        for (i in currentPosition until state.itemCount) {
            //右边布局
            var view = showedView[i]
//            getDecoratedRight()
            if (view == null || view.parent == null) {
                val itemCenter = lastItemCenter + itemWidth
                val right = itemCenter + halfItemWidth
                val left = itemCenter - halfItemWidth
                if (left > width) {
                    println("---$i  $scollx  $currentPosition")
                    //右边不可见
                    println("右边不可见 > $i $left $width")
                    break
                }
                if (view == null) {
                    view = recycler.getViewForPosition(i)
                }
                showedView[i] = view
                addView(view)
                layoutDecoratedWithMargins(view, left, 0, right, itemHeight)
            }
            //已添加
            lastItemCenter = (view.left + view.right) / 2
//            val scale = ((1 - (lastItemCenter - center).absoluteValue / (center / (1 - minScale))))
//            view.scaleX = scale
//            view.scaleY = scale
        }

        for (i in currentPosition - 1 downTo 0) {
            //左边布局
            var view = showedView[i]
            if (view == null || view.parent == null) {
                val itemCenter = lastItemCenter - itemWidth
                val right = itemCenter + halfItemWidth
                val left = itemCenter - halfItemWidth
                if (right < 0) {
                    //左边不可见
                    println("左边不可见 > " + i)
                    break
                }
                if (view == null) {
                    view = recycler.getViewForPosition(i)
                }
                showedView[i] = view
                addView(view)
                layoutDecoratedWithMargins(view, left, 0, right, itemHeight)
            }
            //已添加
            lastItemCenter = (view.left + view.right) / 2
//            val scale = ((1 - (lastItemCenter - center).absoluteValue / (center / (1 - minScale))))
//            view.scaleX = scale
//            view.scaleY = scale
        }

//        if (scroll) {
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: return
            if (child.right < 0) {
                println("recycle left $i")
//                detachAndScrapViewAt(i,recycler)
                removeAndRecycleViewAt(i, recycler)
            } else if (child.left > width) {
//                detachAndScrapViewAt(i,recycler)
                println("recycle right $i")
                removeAndRecycleViewAt(i, recycler)
            }
        }
//        }
    }

}

data class Circle(val index: Int = 1) : JViewBean() {
    override fun onBindViewHolder(
        holder: JViewHolder,
        position: Int,
        payloads: MutableList<Any>?,
        viewClickListener: OnViewClickListener<*>?
    ) {
        println("---------onBindViewHolder---------")
        holder.itemView.background = GradientDrawable().apply {
            color = ColorStateList.valueOf(Color.RED)
            cornerRadius = 300F
        }
        holder.setText(R.id.tv, position.toString())
    }

    override fun bindLayout() = R.layout.layout_mamager_item


    override fun onViewRecycled(holder: JViewHolder) {
        super.onViewRecycled(holder)
        println("============== onViewRecycled $index")
    }

    override fun onViewAttachedToWindow(holder: JViewHolder) {
        super.onViewAttachedToWindow(holder)
        println("============ onViewAttachedToWindow $index")
    }

    override fun onViewDetachedFromWindow(holder: JViewHolder) {
        super.onViewDetachedFromWindow(holder)
        println("============ onViewDetachedFromWindow $index")
    }
}




