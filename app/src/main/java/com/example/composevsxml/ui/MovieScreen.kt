package com.example.composevsxml.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.composevsxml.data.Movie

@Composable
fun MovieScreen(
    state: MovieScreenState,
    onFavoriteClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onGenreSelected: (String) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is MovieScreenState.Loading -> LoadingScreen()
        is MovieScreenState.Error -> ErrorScreen(message = state.message, onRetry = onRetry)
        is MovieScreenState.Success -> {
            SuccessScreen(
                state = state,
                onFavoriteClick = onFavoriteClick,
                onSearchQueryChanged = onSearchQueryChanged,
                onGenreSelected = onGenreSelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(
    state: MovieScreenState.Success,
    onFavoriteClick: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onGenreSelected: (String) -> Unit
) {
    // Column organiza os elementos verticalmente.
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Barra de Busca
        TextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChanged, // A UI informa a mudança para o ViewModel
            label = { Text("Buscar filmes...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        // 2. Filtros de Gênero
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.availableGenres) { genre ->
                FilterChip(
                    selected = (genre == state.selectedGenre),
                    onClick = { onGenreSelected(genre) },
                    label = { Text(genre) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Lista de Filmes
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.displayedMovies, key = { movie -> movie.id }) { movie ->
                MovieListItem(
                    movie = movie,
                    isFavorite = state.favoriteMovieIds.contains(movie.id),
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    isFavorite: Boolean,
    onFavoriteClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Pôster do Filme
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Pôster do filme ${movie.title}",
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            // Informações do Filme
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(text = movie.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${movie.releaseYear} • ★ ${movie.rating}", style = MaterialTheme.typography.bodyMedium)
            }

            // Botão de Favorito
            IconButton(onClick = { onFavoriteClick(movie.id) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritar",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

// As funções LoadingScreen e ErrorScreen continuam as mesmas de antes...
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) { /* ...código anterior... */ }

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) { /* ...código anterior... */ }