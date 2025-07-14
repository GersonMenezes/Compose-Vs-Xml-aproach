package com.example.composevsxml.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composevsxml.ui.theme.ComposeVsXMLTheme // Importe o tema do seu projeto

class ComposeMoviesActivity : ComponentActivity() {

    // 1. A mesma forma de obter o ViewModel! Nenhuma mudança aqui.
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 2. O tema do seu app. Ele envolve toda a UI em Compose.
            ComposeVsXMLTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Coleta o estado do ViewModel de forma segura
                    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

                    // 4. A raiz da nossa UI em Compose.
                    // Passamos o estado e os callbacks para o ViewModel.
                    MovieScreen(
                        state = screenState,
                        onFavoriteClick = { movieId -> viewModel.onToggleFavorite(movieId) },
                        onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
                        onGenreSelected = { genre -> viewModel.onGenreSelected(genre) },
                        onRetry = { viewModel.fetchMovies() }
                    )
                }
            }
        }
    }
}