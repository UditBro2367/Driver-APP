package com.example.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isSimulatedOffline = MutableStateFlow(false)
    val isSimulatedOffline: StateFlow<Boolean> = _isSimulatedOffline.asStateFlow()

    private val _isSystemOnline = MutableStateFlow(checkInitialConnectivity())
    
    private val _isEffectiveOnline = MutableStateFlow(checkInitialConnectivity())
    val isOnline: StateFlow<Boolean> = _isEffectiveOnline.asStateFlow()

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isSystemOnline.value = true
                updateEffectiveOnline()
            }

            override fun onLost(network: Network) {
                _isSystemOnline.value = false
                updateEffectiveOnline()
            }
        })
    }

    private fun checkInitialConnectivity(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return true // Default true in sandbox
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return true
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun toggleSimulatedOffline() {
        _isSimulatedOffline.value = !_isSimulatedOffline.value
        updateEffectiveOnline()
    }

    fun setSimulatedOffline(offline: Boolean) {
        _isSimulatedOffline.value = offline
        updateEffectiveOnline()
    }

    private fun updateEffectiveOnline() {
        _isEffectiveOnline.value = _isSystemOnline.value && !_isSimulatedOffline.value
    }
}
