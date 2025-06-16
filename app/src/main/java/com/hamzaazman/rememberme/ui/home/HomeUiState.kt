package com.hamzaazman.rememberme.ui.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val list: List<String> = emptyList(),
)