package first.lunar.yun.jadapter.layout

import android.graphics.PointF
import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import java.lang.RuntimeException
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt


data class Axis(val value: Int, val center: Boolean)


fun RecyclerView.LayoutManager.newView(
    recycler: Recycler,
    adapterPosition: Int,
    widthUsed: Int = 0,
    heightUsed: Int = 0,
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
    measureChild(view, widthUsed, heightUsed)
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

fun Int.center() = Axis(this, (true))

fun Int.normal() = Axis(this, false)


interface LayoutStrategy {

    fun orientation(): Int?

    fun attach(layoutManager: RecyclerView.LayoutManager, recycler: Recycler, state: RecyclerView.State)

    /**
     * 转为第一行的个数
     */
    fun isStartPosition(apapterPosition: Int, horizotion: Boolean): Boolean
    fun isEndPosition(apapterPosition: Int, horizotion: Boolean): Boolean
    fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler, state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    )

    fun addBlockViewsAtPosition(
        layoutManager: CenterLayoutManager,
        recycler: Recycler,
        position: Int,
        left: Axis? = null, right: Axis? = null,
        top: Axis? = null, bottom: Axis? = null,
    )

    fun forwardScrollConsume(firstView: View, dx: Int = 0, dy: Int = 0, layoutManager: CenterLayoutManager): Int
    fun reverseScrollConsume(lastView: View, dx: Int = 0, dy: Int = 0, layoutManager: CenterLayoutManager): Int

    fun smoothScrollToMakeVisibleBox(layoutManager: RecyclerView.LayoutManager, view: View, horizotion: Boolean): Pair<Int, Int>

    fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int

    fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager)
}


open class Vertical(

    /**
     * 几列  纵向滚动的时候用到
     */
    val column: Int = 1
) : LayoutStrategy {

    var rawItemCount = 0
    lateinit var orientationHelper: OrientationHelper
    override fun orientation() = RecyclerView.VERTICAL

    override fun attach(layoutManager: RecyclerView.LayoutManager, recycler: Recycler, state: RecyclerView.State) {
        if (rawItemCount > 0) {
            return
        }
        rawItemCount = state.itemCount / column
        orientationHelper = OrientationHelper.createOrientationHelper(layoutManager, RecyclerView.VERTICAL)
    }

    override fun isStartPosition(apapterPosition: Int, horizotion: Boolean) = apapterPosition % rawItemCount == 0

    override fun isEndPosition(apapterPosition: Int, horizotion: Boolean) = apapterPosition % rawItemCount == rawItemCount - 1

    override fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler, state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    ) {
        //1 5
        //2 6
        //3 7
        //4 8
        val rowStartPosition = startPosition % rawItemCount
        var start = 0
        var width = 0
        var top = 0
        for (column in 0 until column) {
            top = 0
            val columnIndex = column * rawItemCount
            for (row in rowStartPosition until rawItemCount) {
                val adapterPosition = columnIndex + row
                val view = action(row, column, adapterPosition, start.normal(), top.normal())
                top += view.height
                width = view.width
                if (top > layoutManager.height) {
                    break
                }
            }
            start += width
        }
        if (top < layoutManager.height) {
            //纠正 当设置startPosition为最后一个时要纠正最后一个必须在底部
            layoutManager.detachAndScrapAttachedViews(recycler)
            layoutForEach(layoutManager, recycler, state, startPosition - 1, action)
        }
    }

    override fun addBlockViewsAtPosition(
        layoutManager: CenterLayoutManager,
        recycler: Recycler,
        position: Int,
        left: Axis?, right: Axis?,
        top: Axis?, bottom: Axis?,
    ) {
        //0   3
        //1   4
        //2   5
        val rowPosition = position % rawItemCount
        //在顶部或者底部补充
        var start = 0
        with(layoutManager) {
            for (column in 0 until column) {
                val adapterPosition = column * rawItemCount + rowPosition
                val toAddView = createItemViewWithPosition(
                    recycler, adapterPosition,
                    left = start.normal(),
                    top = top, bottom = bottom
                )
                if (top == null) {
                    //顶部添加
                    addView(toAddView, 0)
                } else {
                    addView(toAddView)
                }
                toAddView.layout()
                start += toAddView.width
            }

        }
    }

    /**
     * 正向滚动
     * ^
     * 视图从下向上滚动
     * 下边补充 上边回收
     * @param dx >0
     *
     */
    override fun forwardScrollConsume(firstView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val end = height - paddingBottom
            val maxDy = firstView.decoratedBottom() - end
            return dy.coerceAtMost(maxDy)//取小值
        }
    }


    /**
     * 反向滚动
     * <----------
     * 视图上向下移动 最后一个item可移动距离纠正实际想要移动距离dx
     * 上边补充 下边回收
     * @param dx <0
     */
    override fun reverseScrollConsume(lastView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val maxDy = lastView.decoratedTop() - paddingTop //到中心可移动的最大距离
            return dy.coerceAtLeast(maxDy)//负值 绝对值越小反而越大
        }
    }

    /**
     * 计算view滑动到的目标区域 横向滑动 start to end
     * 点击 移动到第一个
     */
    override fun smoothScrollToMakeVisibleBox(
        layoutManager: RecyclerView.LayoutManager,
        view: View,
        horizotion: Boolean
    ): Pair<Int, Int> {
        return 0 to view.height
    }


    /**
     * 横轴上 目标位置当前显示的是第几个item
     * 然后根据要滑动到的item和当前显示的item判断左滑动还是右滑动
     * 目标位置上 显示的是4 想要第9个item显示到目标位置
     * 那么就需要向左滑动
     */
    override fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int {
        //找到顶部位置的item
        with(layoutManager) {
            for (i in 0 until rawItemCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                if (view.decoratedTop() <= paddingTop && view.decoratedBottom() > paddingTop) {
                    return getPosition(view)
                }
            }
        }
        return layoutManager.width
    }

    override fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager) {
//        with(layoutManager) {
//            val center = width / 2
//            for (i in 0 until childCount) {
//                val view = getChildAt(i) ?: continue
//                if (!view.isAttachedToWindow) {
//                    continue
//                }
//                //基于滑动后的位置计算缩放值
//                val scale = ((1 - (view.centerX() - center - dx).absoluteValue / (center / (1 - .7F))))
//                view.scaleX = scale
//                view.scaleY = scale
//            }
//        }
    }
}


class VerticalCenter(column: Int = 2) : Vertical(column) {
    override fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler,
        state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    ) {
        //1 5
        //2 6
        //3 7
        //4 8
        //先纵向布局
        //一列一列布局
        val rowStartPosition = startPosition % rawItemCount
        var start = 0
        var width = 0
        var height = 0
        var top = 0
        //中间开始 下半部
        for (column in 0 until column) {//有几列
            top = layoutManager.height / 2
            val columnIndex = column * rawItemCount
            //一列中每行
            for (row in rowStartPosition until rawItemCount) {
                val adapterPosition = columnIndex + row
                val view = action(row, column, adapterPosition, start.normal(), top.center())
                top += view.height
                width = view.width
                height = view.height
                if (top > layoutManager.height) {
                    break
                }
            }
            start += width
        }
        if (rowStartPosition == 0) {
            return
        }
        start = 0
        //中间开始 上半部
        for (column in 0 until column) {
            top = layoutManager.height / 2 - height
            val columnIndex = column * rawItemCount
            for (row in rowStartPosition - 1 downTo 0) {
                val adapterPosition = columnIndex + row
                val view = action(row, column, adapterPosition, start.normal(), top.center())
                top -= view.height
                if (top < 0) {
                    break
                }
            }
            start += width
        }
    }

    override fun forwardScrollConsume(firstView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val maxDy = firstView.centerY() - height / 2
            return dy.coerceAtMost(maxDy)//取小值
        }
    }


    override fun reverseScrollConsume(lastView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val maxDy = lastView.centerY() - height / 2 //到中心可移动的最大距离
            return dy.coerceAtLeast(maxDy)//负值 绝对值越小反而越大
        }
    }

    override fun smoothScrollToMakeVisibleBox(
        layoutManager: RecyclerView.LayoutManager,
        view: View,
        horizotion: Boolean
    ): Pair<Int, Int> {
        val center = layoutManager.height / 2
        val halfHeight = view.height / 2
        return center - halfHeight to center + halfHeight
    }

    override fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int {
        //找到中间位置的item
        with(layoutManager) {
            val center = height / 2
            for (i in 0 until rawItemCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                if (view.decoratedTop() <= center && view.decoratedBottom() > center) {
                    return getPosition(view)
                }
            }
        }
        return layoutManager.width
    }

    override fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager) {
        with(layoutManager) {
            val center = height / 2
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                //基于滑动后的位置计算缩放值
                val scale = ((1 - (view.centerY() - center - dx).absoluteValue / (center / (1 - .7F))))
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    }
}


class VerticalCenter2(column: Int = 2) : Vertical(column) {

    fun Int.toRowIndex() = this / column
    override fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler,
        state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    ) {
        //1 2
        //3 4
        //5 6
        //7 8
        var height = 0
        var top = layoutManager.height / 2
        //中间开始 下半部
        //先横向布局 一行一行布局
        val totalRow = state.itemCount / column
        //01=0,23=1,45=2,67=3
        val rowPosition = startPosition.toRowIndex()
        for (row in rowPosition until totalRow) {
            val rowIndex = column * row
            var start = 0
            for (column in 0 until column) {
                val adapterPosition = rowIndex + column
                val view = action(row, column, adapterPosition, start.normal(), top.center())
                start += view.width
                height = view.height
            }
            top += height
            if (top > layoutManager.height) {
                break
            }
        }
        if (rowPosition == 0) {
            return
        }
        //中间开始 上半部
        top = layoutManager.height / 2 - height
        for (row in rowPosition - 1 downTo 0) {
            val rowIndex = column * row
            var start = 0
            for (column in 0 until column) {
                val adapterPosition = rowIndex + column
                val view = action(row, column, adapterPosition, start.normal(), top.center())
                start += view.width
                height = view.height
            }
            top -= height
            if (top < 0) {
                break
            }
        }
    }


    override fun addBlockViewsAtPosition(
        layoutManager: CenterLayoutManager,
        recycler: Recycler,
        position: Int,
        left: Axis?,
        right: Axis?,
        top: Axis?,
        bottom: Axis?
    ) {
        println("toadd $position")
        with(layoutManager) {
            var start = 0
            if (top == null) {
                //顶部增加
                //一行都是从左边开始布局的 传进来的要加入的位置是最右边的 先转为最左边
                val toAddPosition = position - column + 1
                for (column in 0 until column) {
                    val adapterPosition = toAddPosition + column
                    val toAddView = createItemViewWithPosition(
                        recycler, adapterPosition,
                        left = start.normal(),
                        top = top, bottom = bottom
                    )
                    addView(toAddView, 0)
                    toAddView.layout()
                    start += toAddView.width
                }
            } else {
                //底部添加
                for (column in 0 until column) {
                    val adapterPosition = position + column
                    val toAddView = createItemViewWithPosition(
                        recycler, adapterPosition,
                        left = start.normal(),
                        top = top, bottom = bottom
                    )
                    addView(toAddView)
                    toAddView.layout()
                    start += toAddView.width
                }
            }
        }
    }

    override fun isStartPosition(apapterPosition: Int, horizotion: Boolean): Boolean {
        return apapterPosition.toRowIndex() == 0
    }

    override fun isEndPosition(apapterPosition: Int, horizotion: Boolean): Boolean {
        return apapterPosition.toRowIndex() == rawItemCount - 1
    }

    override fun forwardScrollConsume(firstView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val maxDy = firstView.centerY() - height / 2
            return dy.coerceAtMost(maxDy)//取小值
        }
    }


    override fun reverseScrollConsume(lastView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val maxDy = lastView.centerY() - height / 2 //到中心可移动的最大距离
            return dy.coerceAtLeast(maxDy)//负值 绝对值越小反而越大
        }
    }

    override fun smoothScrollToMakeVisibleBox(
        layoutManager: RecyclerView.LayoutManager,
        view: View,
        horizotion: Boolean
    ): Pair<Int, Int> {
        val center = layoutManager.height / 2
        val halfHeight = view.height / 2
        return center - halfHeight to center + halfHeight
    }

    override fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int {
        //找到中间位置的item
        with(layoutManager) {
            val center = height / 2
            for (i in 0 until rawItemCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                if (view.decoratedTop() <= center && view.decoratedBottom() > center) {
                    return getPosition(view)
                }
            }
        }
        return layoutManager.width
    }

    override fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager) {
        with(layoutManager) {
            val center = height / 2
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                //基于滑动后的位置计算缩放值
                val scale = ((1 - (view.centerY() - center - dx).absoluteValue / (center / (1 - .7F))))
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    }
}

open class Horizotion(
    /**
     * 几行  横向滚动的时候用
     */
    val row: Int = 1
) : LayoutStrategy {
    var rawItemCount = 0


    lateinit var orientationHelper: OrientationHelper

    override fun orientation() = RecyclerView.HORIZONTAL

    override fun attach(layoutManager: RecyclerView.LayoutManager, recycler: Recycler, state: RecyclerView.State) {
        if (rawItemCount > 0) {
            return
        }
        rawItemCount = state.itemCount / row
        orientationHelper = OrientationHelper.createOrientationHelper(layoutManager, RecyclerView.VERTICAL)
    }

    override fun isStartPosition(apapterPosition: Int, horizotion: Boolean) = apapterPosition % rawItemCount == 0

    override fun isEndPosition(apapterPosition: Int, horizotion: Boolean) = apapterPosition % rawItemCount == rawItemCount - 1

    override fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler, state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    ) {
        //1234
        //5678
        if (state.itemCount % row > 0) {
            throw RuntimeException("每行必须排满")
        }
        //每行多少个item
        val rowStartPosition = startPosition % rawItemCount
        var top = 0
        var height = 0
        var start = 0
        for (row in 0 until row) {
            start = 0
            val rowIndex = row * rawItemCount
            for (column in rowStartPosition until rawItemCount) {
                val adapterPonsition = rowIndex + column
                val view = action(row, column, adapterPonsition, start.normal(), top.normal())
                start += view.width
                height = view.height
                //下一个右边不可见就停止
                if (start > layoutManager.width) {
                    break
                }
            }
            top += height
        }
        if (start < layoutManager.width) {
            //纠正 最后一个item必须在最后
            layoutManager.detachAndScrapAttachedViews(recycler)
            layoutForEach(layoutManager, recycler, state, startPosition - 1, action)
        }
    }

    override fun addBlockViewsAtPosition(
        layoutManager: CenterLayoutManager,
        recycler: Recycler,
        position: Int,
        left: Axis?, right: Axis?,
        top: Axis?, bottom: Axis?,
    ) {
        val rowPosition = position % rawItemCount
        var itemTop = 0
        with(layoutManager) {
            for (row in 0 until row) {
                val adapterPosition = (row * rawItemCount) + rowPosition
                val toAddView = createItemViewWithPosition(
                    recycler, adapterPosition,
                    left = left, right = right,
                    top = itemTop.normal()
                )
                if (left != null) {
                    addView(toAddView)//添加在后面
                } else {
                    addView(toAddView, 0)//添加在前面
                }
                toAddView.layout()
                itemTop += toAddView.height
            }
        }
    }

    /**
     * 正向滚动
     * -------->
     * 视图从左向右滚动
     * 左边补充 右边回收
     * @param dx <0
     *
     */
    override fun forwardScrollConsume(firstView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val maxDx = firstView.decoratedLeft() - paddingStart
            return dx.coerceAtLeast(maxDx)//负值 绝对值越小反而越大
        }
    }


    /**
     * 反向滚动
     * <----------
     * 视图右向左移动 最后一个item可移动距离纠正实际想要移动距离dx
     * 右边补充 左边回收
     * @param dx >0
     */
    override fun reverseScrollConsume(lastView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        with(layoutManager) {
            val end = width - paddingEnd
            val maxDx = lastView.decoratedRight() - end //到中心可移动的最大距离
            return dx.coerceAtMost(maxDx)//取小值
        }
    }

    /**
     * 计算view滑动到的目标区域 横向滑动 start to end
     * 点击 移动到第一个
     */
    override fun smoothScrollToMakeVisibleBox(
        layoutManager: RecyclerView.LayoutManager,
        view: View,
        horizotion: Boolean
    ): Pair<Int, Int> {
        return 0 to view.width
    }


    /**
     * 横轴上 目标位置当前显示的是第几个item
     * 然后根据要滑动到的item和当前显示的item判断左滑动还是右滑动
     * 目标位置上 显示的是4 想要第9个item显示到目标位置
     * 那么就需要向左滑动
     */
    override fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int {
        //找到中间位置的item
        with(layoutManager) {
            val center = width / 2
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                if (view.decoratedLeft() <= paddingStart && view.decoratedRight() > paddingStart) {
                    return getPosition(view)
                }
            }
        }
        return layoutManager.width
    }

    override fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager) {
//        with(layoutManager) {
//            val center = width / 2
//            for (i in 0 until childCount) {
//                val view = getChildAt(i) ?: continue
//                if (!view.isAttachedToWindow) {
//                    continue
//                }
//                //基于滑动后的位置计算缩放值
//                val scale = ((1 - (view.centerX() - center - dx).absoluteValue / (center / (1 - .7F))))
//                view.scaleX = scale
//                view.scaleY = scale
//            }
//        }
    }
}

class HorizotionCenter(row: Int = 2) : Horizotion(row) {
    override fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler, state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    ) {
        //1234
        //5678
        if (state.itemCount % row > 0) {
            throw RuntimeException("每行必须排满")
        }
        var itemWidth = 0//每个item大小一致
        //每行多少个item
        val rowStartPosition = startPosition % rawItemCount
        var top = 0
        var height = 0
        //右边布局  __456
        for (row in 0 until row) {
            var center = layoutManager.width / 2
            val rowIndex = row * rawItemCount
            for (column in rowStartPosition until rawItemCount) {
                val adapterPonsition = rowIndex + column
                val view = action(row, column, adapterPonsition, center.center(), top.normal())
                center += view.width
                height = view.height
                itemWidth = view.width
                //下一个右边不可见就停止
                if (view.right > layoutManager.width) {
                    break
                }
            }
            top += height
        }
        if (rowStartPosition == 0) {
            return
        }
        //左边布局 23___
        top = 0
        for (row in 0 until row) {
            var center = layoutManager.width / 2 - itemWidth
            val rowIndex = row * rawItemCount
            for (column in rowStartPosition - 1 downTo 0) {
                val adapterPonsition = rowIndex + column
                val view = action(row, column, adapterPonsition, center.center(), top.normal())
                center += view.width
                height = view.height
                //下一个右边不可见就停止
                if (view.left < 0) {
                    break
                }
            }
            top += height
        }
    }

    /**
     * 正向滚动
     * -------->
     * 视图从左向右滚动
     * 左边补充 右边回收
     * @param dx <0
     *
     */
    override fun forwardScrollConsume(firstView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        val center = layoutManager.width / 2
        with(layoutManager) {
            val maxDx = firstView.centerX() - center
            return dx.coerceAtLeast(maxDx)//负值 绝对值越小反而越大
        }
    }


    /**
     * 反向滚动
     * <----------
     * 视图右向左移动 最后一个item可移动距离纠正实际想要移动距离dx
     * 右边补充 左边回收
     * @param dx >0
     */
    override fun reverseScrollConsume(lastView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        val center = layoutManager.width / 2
        with(layoutManager) {
            val maxDx = lastView.centerX() - center //到中心可移动的最大距离
            return dx.coerceAtMost(maxDx)//取小值
        }
    }

    /**
     * 计算view滑动到的目标区域 横向滑动 start to end
     */
    override fun smoothScrollToMakeVisibleBox(
        layoutManager: RecyclerView.LayoutManager,
        view: View,
        horizotion: Boolean
    ): Pair<Int, Int> {
        val center = layoutManager.width / 2
        val halfWidth = view.width / 2
        return center - halfWidth to center + halfWidth
    }


    /**
     * 横轴上 目标位置当前显示的是第几个item
     * 然后根据要滑动到的item和当前显示的item判断左滑动还是右滑动
     * 目标位置上 显示的是4 想要第9个item显示到目标位置
     * 那么就需要向左滑动
     */
    override fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int {
        //找到中间位置的item
        with(layoutManager) {
            val center = width / 2
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                if (view.decoratedLeft() < center && view.decoratedRight() > center) {
                    return getPosition(view)
                }
            }
        }
        return layoutManager.width
    }

    override fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager) {
        with(layoutManager) {
            val center = width / 2
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                //基于滑动后的位置计算缩放值
                val scale = ((1 - (view.centerX() - center - dy).absoluteValue / (center / (1 - .7F))))
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    }
}

class AllCenter(val row: Int = 4) : LayoutStrategy {
    lateinit var verticalCenter: VerticalCenter
    lateinit var horizontalCenter: HorizotionCenter
    override fun orientation() = null

    override fun attach(layoutManager: RecyclerView.LayoutManager, recycler: Recycler, state: RecyclerView.State) {
        if (state.itemCount <= 0 || ::verticalCenter.isInitialized) {
            return
        }
        horizontalCenter = HorizotionCenter(row)
        horizontalCenter.attach(layoutManager, recycler, state)
        val column = state.itemCount / row
        verticalCenter = VerticalCenter(column)
        verticalCenter.attach(layoutManager, recycler, state)
    }

    override fun isStartPosition(apapterPosition: Int, horizotion: Boolean): Boolean {
        return if (horizotion)
            horizontalCenter.isStartPosition(apapterPosition, true)
        else
            verticalCenter.isStartPosition(apapterPosition, false)
    }

    override fun isEndPosition(apapterPosition: Int, horizotion: Boolean): Boolean {
        return if (horizotion)
            horizontalCenter.isEndPosition(apapterPosition, true)
        else
            verticalCenter.isEndPosition(apapterPosition, false)
    }

    override fun layoutForEach(
        layoutManager: RecyclerView.LayoutManager,
        recycler: Recycler,
        state: RecyclerView.State,
        startPosition: Int,
        action: (Int, Int, Int, Axis, Axis) -> View
    ) {
        if (state.itemCount % row > 0) {
            throw RuntimeException("每行必须排满")
        }
        var itemWidth = 0//每个item大小一致
        //每行多少个item
        val horizotionPosition = startPosition % horizontalCenter.rawItemCount
        var top = layoutManager.height / 2
        var height = 0
        //右边布局  __456
        for (row in 0 until row) {
            var center = layoutManager.width / 2
            val rowIndex = row * horizontalCenter.rawItemCount
            for (column in horizotionPosition until horizontalCenter.rawItemCount) {
                val adapterPonsition = rowIndex + column
                val view = action(row, column, adapterPonsition, center.center(), top.normal())
                center += view.width
                height = view.height
                itemWidth = view.width
                //下一个右边不可见就停止
                if (view.right > layoutManager.width) {
                    break
                }
            }
            top += height
        }
        if (horizotionPosition > 0) {
            //左边布局 23___
            top = 0
            for (row in 0 until row) {
                var center = layoutManager.width / 2 - itemWidth
                val rowIndex = row * horizontalCenter.rawItemCount
                for (column in horizontalCenter.rawItemCount - 1 downTo 0) {
                    val adapterPonsition = rowIndex + column
                    val view = action(row, column, adapterPonsition, center.center(), top.normal())
                    center += view.width
                    height = view.height
                    //下一个右边不可见就停止
                    if (view.left < 0) {
                        break
                    }
                }
                top += height
            }
        }

//        val verticalPosition = startPosition % horizontalCenter.rawItemCount
//        var start = layoutManager.width / 2
//        var width = 0
//        if (verticalPosition == 0) {
//            return
//        }
//        //中间开始 上半部 得横着👏
//        for (column in 0 until verticalCenter.column) {
//            top = layoutManager.height / 2 - height
//            val columnIndex = column * verticalCenter.rawItemCount
//            for (row in verticalPosition - 1 downTo 0) {
//                val adapterPosition = columnIndex + row
//                val view = action(row, column, adapterPosition, start.normal(), top.center())
//                top += view.height
//                width = view.width
//                if (top > layoutManager.height) {
//                    break
//                }
//            }
//            start += width
//        }
    }

    override fun addBlockViewsAtPosition(
        layoutManager: CenterLayoutManager,
        recycler: Recycler,
        position: Int,
        left: Axis?,
        right: Axis?,
        top: Axis?,
        bottom: Axis?
    ) {
        if (left != null || right != null) {
            horizontalCenter.addBlockViewsAtPosition(layoutManager, recycler, position, left, right)
        } else {
            verticalCenter.addBlockViewsAtPosition(layoutManager, recycler, position, top, bottom)
        }
    }

    override fun forwardScrollConsume(firstView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        if (dx != 0) {
            return horizontalCenter.forwardScrollConsume(firstView, dx, dy, layoutManager)
        }
        if (dy != 0) {
            return verticalCenter.forwardScrollConsume(firstView, dx, dy, layoutManager)
        }
        return 0
    }

    override fun reverseScrollConsume(lastView: View, dx: Int, dy: Int, layoutManager: CenterLayoutManager): Int {
        if (dx != 0) {
            return horizontalCenter.reverseScrollConsume(lastView, dx, dy, layoutManager)
        }
        if (dy != 0) {
            return verticalCenter.reverseScrollConsume(lastView, dx, dy, layoutManager)
        }
        return 0
    }

    override fun smoothScrollToMakeVisibleBox(
        layoutManager: RecyclerView.LayoutManager,
        view: View,
        horizotion: Boolean
    ): Pair<Int, Int> {
        return if (horizotion) {
            horizontalCenter.smoothScrollToMakeVisibleBox(layoutManager, view, horizotion)
        } else {
            verticalCenter.smoothScrollToMakeVisibleBox(layoutManager, view, horizotion)
        }
    }

    override fun currentAdapterPositionAtTargetLocation(layoutManager: CenterLayoutManager): Int {
        return horizontalCenter.currentAdapterPositionAtTargetLocation(layoutManager = layoutManager)
    }

    override fun transformViews(dx: Int, dy: Int, layoutManager: CenterLayoutManager) {
        with(layoutManager) {
            val center = PointF(width / 2F, height / 2F)
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (!view.isAttachedToWindow) {
                    continue
                }
                val xDiff = view.centerX() - dx - center.x
                val yDiff = view.centerY() - dy - center.y
                val distance = sqrt(xDiff.pow(2) + yDiff.pow(2))
                //基于滑动后的位置计算缩放值
//                val scale = 1.0F
                val scale = ((1 - distance.absoluteValue / (center.y / (1 - .7F))))
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    }
}