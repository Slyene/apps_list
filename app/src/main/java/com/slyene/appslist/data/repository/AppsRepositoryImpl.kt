package com.slyene.appslist.data.repository

import android.content.pm.PackageManager
import android.os.Build
import com.slyene.appslist.data.checksums.ChecksumCacheDataSource
import com.slyene.appslist.domain.model.AppInfo
import com.slyene.appslist.domain.repository.AppsRepository
import com.slyene.appslist.domain.model.ChecksumState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

class AppsRepositoryImpl(
    private val packageManager: PackageManager,
    private val checksumCacheDataSource: ChecksumCacheDataSource
) : AppsRepository {

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }

        packages.mapNotNull { packageInfo ->
            val appInfo = packageInfo.applicationInfo
            if (appInfo != null) {
                AppInfo(
                    name = appInfo.loadLabel(packageManager).toString(),
                    packageName = packageInfo.packageName,
                    versionName = packageInfo.versionName.orEmpty(),
                    sourceDir = appInfo.sourceDir,
                    icon = appInfo.loadIcon(packageManager)
                )
            } else {
                null // Skip packages without applicationInfo
            }
        }
    }

    override fun getAppChecksumWithProgress(
        packageName: String,
        apkPath: String
    ): Flow<ChecksumState> = flow {
        val currentVersionName = try {
            packageManager.getPackageInfo(packageName, 0)?.versionName.orEmpty()
        } catch (_: PackageManager.NameNotFoundException) {
            // Package not found, this might happen if the app was uninstalled between list generation and checksum calculation
            emit(ChecksumState.Error(packageName))
            return@flow
        }

        // Try to get from cache first
        val cachedChecksumInfo = checksumCacheDataSource.getChecksum(packageName)

        if (
            cachedChecksumInfo != null
            && cachedChecksumInfo.versionName == currentVersionName
            && cachedChecksumInfo.checksum.isNotEmpty()
        ) {
            // Cache is valid
            emit(ChecksumState.Success(cachedChecksumInfo.checksum))
        } else {
            // If not in cache or version mismatch, start calculation
            emit(ChecksumState.Loading(0.0f)) // Initial loading state
            val md = MessageDigest.getInstance("MD5")
            val file = File(apkPath)
            val totalBytes = file.length()
            var bytesRead: Long = 0

            FileInputStream(file).use { fis ->
                val dataBytes = ByteArray(4096)
                var nread: Int
                while (fis.read(dataBytes).also { nread = it } != -1) {
                    md.update(dataBytes, 0, nread)
                    bytesRead += nread
                    val progress = bytesRead.toFloat() / totalBytes.toFloat()
                    emit(ChecksumState.Loading(progress))
                }
            }
            val mdbytes = md.digest()
            val checksum = mdbytes.joinToString("") { "%02x".format(it) }

            // Update cache with new checksum and current version name
            checksumCacheDataSource.updateChecksum(packageName, checksum, currentVersionName)
            emit(ChecksumState.Success(checksum)) // Emit final checksum and 100% progress
        }
    }
        .flowOn(Dispatchers.IO)
        .catch { e ->
            e.printStackTrace()
            emit(ChecksumState.Error(e.localizedMessage.orEmpty()))
        }
}
