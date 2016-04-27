package kyeah.gitterbomb.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.row_progress.view.*

/**
 * Created by kyeh on 4/27/16.
 */
class ProgressViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val progressBar = view.progress_bar
    fun setIndeterminate(b: Boolean) {
        progressBar.isIndeterminate = b
    }
}