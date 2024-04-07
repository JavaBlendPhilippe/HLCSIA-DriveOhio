package com.example.driveohioia.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driveohioia.location.GetLocationUseCase
import com.example.driveohioia.location.PermissionEvent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.example.driveohioia.location.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class DriveViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase
): ViewModel() {
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    // Updates the ViewState based on the event coming from the view
    // Activity calls handle to determine if state will update values or not.
    fun handle(event: PermissionEvent){
        when (event) {
            PermissionEvent.Granted -> {
                viewModelScope.launch {
                    getLocationUseCase.invoke().collect {location ->
                        _viewState.value = ViewState.Success(location)
                        Timber.d("XOXO - ViewState set to Success")
                    }
                }
            }

            PermissionEvent.Revoked -> {
                _viewState.value = ViewState.RevokedPermissions
            }
        }
    }

}