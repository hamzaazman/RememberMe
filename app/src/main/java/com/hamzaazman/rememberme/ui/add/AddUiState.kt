package com.hamzaazman.rememberme.ui.add

data class AddUiState(
    val isLoading: Boolean = false,
    val list: List<String> = emptyList(),
)