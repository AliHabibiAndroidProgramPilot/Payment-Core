package info.alihabibi.merchant.sharedpref

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TraceNumberPref @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        const val TRACE_NUMBER_REFERENCES = "TRACE_NUMBER_REFERENCES"
        const val TRACE_NUMBER_PREF = "TRACE_NUMBER_PREF"
    }

    private val sharedPref = context.getSharedPreferences(TRACE_NUMBER_REFERENCES, Context.MODE_PRIVATE)

    fun saveTraceNumber(value: Int) {
        sharedPref.edit {
            putInt(TRACE_NUMBER_PREF, value)
        }
    }

    fun getLatestTraceNumber(): Int = sharedPref.getInt(TRACE_NUMBER_PREF, 1)

}