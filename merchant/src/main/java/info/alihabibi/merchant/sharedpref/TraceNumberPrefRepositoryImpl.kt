package info.alihabibi.merchant.sharedpref

import jakarta.inject.Inject

class TraceNumberPrefRepositoryImpl @Inject constructor(
    private val pref: TraceNumberPref
) : TraceNumberPrefRepository {

    override fun saveTraceNumber(value: Int) {
        pref.saveTraceNumber(value)
    }

    override fun getLatestTraceNumber(): Int = pref.getLatestTraceNumber()

}