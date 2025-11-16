package com.slyene.appslist.data.checksums

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private val Context.checksumsDataStore: DataStore<ChecksumCache> by dataStore(
    fileName = "checksums.pb",
    serializer = ChecksumCacheSerializer
)

class ChecksumCacheDataSource(private val context: Context) {

    val checksums: Flow<ChecksumCache> = context.checksumsDataStore.data

    suspend fun updateChecksum(packageName: String, checksum: String, versionName: String) {
        context.checksumsDataStore.updateData {
            it.toBuilder()
                .putChecksums(packageName, ChecksumInfo.newBuilder().setChecksum(checksum).setVersionName(versionName).build())
                .build()
        }
    }

    suspend fun getChecksum(packageName: String): ChecksumInfo? {
        return checksums.first().checksumsMap[packageName]
    }
}
