package com.slyene.appslist.domain.model

sealed class ChecksumState {
    object Idle : ChecksumState()
    data class Loading(val progress: Float) : ChecksumState()
    data class Success(val checksum: String) : ChecksumState()
    data class Error(val message: String) : ChecksumState()
}