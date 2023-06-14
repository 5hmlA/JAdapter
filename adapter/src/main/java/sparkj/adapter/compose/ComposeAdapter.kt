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

class ComposeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
class ComposeAdapter : RecyclerView.Adapter<ComposeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeHolder {
        return ComposeHolder(ComposeView(parent.context).apply {
            setContent {

            }
        })
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

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