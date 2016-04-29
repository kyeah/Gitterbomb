package kyeah.gitterbomb

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

/**
 * Created by kyeh on 2/15/16.
 */
object Utils {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val timeFormat = SimpleDateFormat("hh:mm aa")

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC");
    }
}

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun DrawerLayout.consume(f: () -> Unit): Boolean {
    f()
    closeDrawers()
    return true
}

inline fun DrawerLayout.toggle() {
    if (isDrawerOpen(GravityCompat.START)) {
        closeDrawer(GravityCompat.START)
    } else {
        openDrawer(GravityCompat.START)
    }
}

inline fun stringToDate(s: String): Date? {
    return Utils.dateFormat.parse(s)
}

inline fun dateToTime(date: Date?): String {
    return if (date != null) {
        Utils.timeFormat.format(date)
    } else {
        ""
    }
}

inline fun ImageView.loadUrl(url: String) {
    Glide.with(context).load(url).into(this)
}

inline fun ImageView.loadUrlRounded(url: String, corners: Int) {
    Glide.with(context)
            .load(url)
            .bitmapTransform(RoundedCornersTransformation(context, corners, corners))
            .into(this)
}

inline fun <reified T:Any> logger() = Logger.getLogger(T::class.java.toString())