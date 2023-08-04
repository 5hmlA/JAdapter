package sparkj.adapter.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView

interface ComposeBean {

    @Composable
    fun content()
}

//(proxy.javaClass.genericSuperclass as ParameterizedType)// 获取类型参数列表
//.actualTypeArguments[0].toString()// 获取类型参数表列中的第一个类型


class ComposeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}
class ComposeAdapter : RecyclerView.Adapter<ComposeHolder>() {

    val datas = mutableListOf<ComposeBean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeHolder {
        return ComposeHolder(ComposeView(parent.context).apply {
            setContent {

            }
        })
    }

    override fun getItemCount() = datas.size

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ComposeHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ComposeHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }
}