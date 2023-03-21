package uz.raytel.raytel.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

val bottomShopListCloseFlow = MutableSharedFlow<Int>()
val shopSelectedFlow = MutableStateFlow(false)
