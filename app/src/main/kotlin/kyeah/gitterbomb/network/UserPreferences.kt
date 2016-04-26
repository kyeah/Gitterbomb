package kyeah.gitterbomb.network

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by kyeh on 4/25/16.
 */

object UserPreferences {

    private val PREFS_GITTERBOMB = "prefs_gitterbomb"
    private val PREFS_ACCESS_TOKEN = "prefs_access_token"

    fun getPrefs(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFS_GITTERBOMB, Context.MODE_PRIVATE)

    fun getAccessToken(context: Context): String? =
            getPrefs(context).getString(PREFS_ACCESS_TOKEN, null)

    fun setAccessToken(context: Context, token: String): Boolean {
        val editor = getPrefs(context).edit()
        editor.putString(PREFS_ACCESS_TOKEN, token)
        return editor.commit()
    }
}