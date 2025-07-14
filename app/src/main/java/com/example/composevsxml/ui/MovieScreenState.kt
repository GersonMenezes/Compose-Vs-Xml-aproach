package com.example.composevsxml.ui

import com.example.composevsxml.data.Movie

/**
 * Representa os diferentes estados que a tela de filmes pode ter.
 * Usar uma sealed class garante que temos que tratar todos os estados possíveis
 * no bloco 'when', prevenindo bugs.
 */
sealed class MovieScreenState {
    // Estado de carregamento inicial
    object Loading : MovieScreenState()

    // Estado de erro, com uma mensagem
    data class Error(val message: String) : MovieScreenState()

    // Estado de sucesso, contendo todos os dados necessários para a UI
    data class Success(
        val allMovies: List<Movie>,         // A lista original vinda da "API"
        val displayedMovies: List<Movie>,   // A lista filtrada que será exibida na tela
        val availableGenres: List<String>,  // Os gêneros para os chips de filtro
        val searchQuery: String,            // O texto atual na barra de busca
        val selectedGenre: String,          // O gênero atualmente selecionado
        val favoriteMovieIds: Set<Int>      // Um conjunto com os IDs dos filmes favoritados
    ) : MovieScreenState()
}