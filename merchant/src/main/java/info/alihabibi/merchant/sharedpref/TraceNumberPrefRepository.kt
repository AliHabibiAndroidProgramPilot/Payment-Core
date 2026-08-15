package info.alihabibi.merchant.sharedpref

interface TraceNumberPrefRepository {

    fun saveTraceNumber(value: Int)

    fun getLatestTraceNumber(): Int

}