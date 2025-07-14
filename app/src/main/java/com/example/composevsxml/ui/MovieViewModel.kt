package com.example.composevsxml.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.composevsxml.data.Movie

class MovieViewModel : ViewModel() {

    private val _screenState = MutableStateFlow<MovieScreenState>(MovieScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        fetchMovies()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _screenState.value = MovieScreenState.Loading
            kotlinx.coroutines.delay(1500)

            val mockMovies = getMockMovies()
            val mockGenres = listOf("Todos", "Ação", "Drama", "Ficção Científica", "Comédia")

            _screenState.value = MovieScreenState.Success(
                allMovies = mockMovies,
                displayedMovies = mockMovies,
                availableGenres = mockGenres,
                searchQuery = "",
                selectedGenre = "Todos",
                favoriteMovieIds = emptySet()
            )
        }
    }

    fun onToggleFavorite(movieId: Int) {
        _screenState.update { currentState ->
            if (currentState is MovieScreenState.Success) {
                val currentFavorites = currentState.favoriteMovieIds
                val newFavorites = if (currentFavorites.contains(movieId)) {
                    currentFavorites - movieId
                } else {
                    currentFavorites + movieId
                }
                currentState.copy(favoriteMovieIds = newFavorites)
            } else {
                currentState
            }
        }
    }

    /**
     * Chamado quando o texto na barra de busca muda.
     */
    fun onSearchQueryChanged(query: String) {
        _screenState.update { currentState ->
            if (currentState is MovieScreenState.Success) {
                // Atualiza o estado com a nova query e a nova lista de filmes filtrada
                currentState.copy(
                    searchQuery = query,
                    displayedMovies = applyFiltersAndSearch(
                        movies = currentState.allMovies,
                        query = query,
                        genre = currentState.selectedGenre
                    )
                )
            } else {
                currentState
            }
        }
    }

    /**
     * Chamado quando um chip de gênero é selecionado.
     */
    fun onGenreSelected(genre: String) {
        _screenState.update { currentState ->
            if (currentState is MovieScreenState.Success) {
                // Atualiza o estado com o novo gênero e a nova lista de filmes filtrada
                currentState.copy(
                    selectedGenre = genre,
                    displayedMovies = applyFiltersAndSearch(
                        movies = currentState.allMovies,
                        query = currentState.searchQuery,
                        genre = genre
                    )
                )
            } else {
                currentState
            }
        }
    }

    /**
     * A LÓGICA CENTRAL: aplica os filtros de gênero e busca na lista completa de filmes.
     */
    private fun applyFiltersAndSearch(
        movies: List<Movie>,
        query: String,
        genre: String
    ): List<Movie> {
        return movies
            .filter { movie -> // 1º: Filtra por gênero
                if (genre == "Todos") {
                    true // Se for "Todos", não filtra por gênero
                } else {
                    movie.genre == genre
                }
            }
            .filter { movie -> // 2º: Filtra por busca na lista já filtrada por gênero
                movie.title.contains(query, ignoreCase = true)
            }
    }

    // --- Dados Mockados (Atualizados com gênero) ---
    private fun getMockMovies(): List<Movie> {
        return listOf(
            Movie(1, "Interestelar", 2014, "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", 8.4, "Ficção Científica"),
            Movie(2, "O Poderoso Chefão", 1972, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/uP46DujkD3nwcisOjz9a0Xw0Knj.jpg", 8.7, "Drama"),
            Movie(3, "O Senhor dos Anéis: O Retorno do Rei", 2003, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/egSO4klmc9pnI1CNnHoYKV70XKI.jpg", 8.5, "Aventura"),
            Movie(4, "Pulp Fiction: Tempo de Violência", 1994, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/tptjnB2LDbuUWya9Cx5sQtv5hqb.jpg", 8.5, "Ação"),
            Movie(5, "Batman: O Cavaleiro das Trevas", 2008, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/4lj1ikfsSmMZNyfdi8R8Tv5tsgb.jpg", 8.5, "Ação"),
            Movie(6, "A Lista de Schindler", 1993, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/fvPYwfXH513e8Nqe0kzWFm2jjg.jpg", 8.6, "Drama"),
            Movie(7, "Forrest Gump: O Contador de Histórias", 1994, "https://image.tmdb.org/t/p/w600_and_h900_bestv2/d74WpIsH8379TIL4wUxDneRCYv2.jpg", 8.5, "Comédia")
        )
    }
}