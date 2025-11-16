package com.slyene.appslist.data.checksums

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object ChecksumCacheSerializer : Serializer<ChecksumCache> {
    override val defaultValue: ChecksumCache = ChecksumCache.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ChecksumCache {
        return runCatching {
            ChecksumCache.parseFrom(input)
        }.getOrElse { e ->
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: ChecksumCache, output: OutputStream) {
        t.writeTo(output)
    }
}
