package com.example.composevsxml.ui

import MovieAdapter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import com.example.composevsxml.databinding.ActivityXmlMoviesBinding

class XmlMoviesActivity : AppCompatActivity() {

    // 1. View Binding para o layout da activity
    private lateinit var binding: ActivityXmlMoviesBinding

    // 2. Obter a instância do ViewModel
    private val viewModel: MovieViewModel by viewModels()

    // 3. Instanciar o nosso MovieAdapter
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout e o torna acessível via 'binding'
        binding = ActivityXmlMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModelState()
    }

    private fun setupRecyclerView() {
        // Passamos a lambda que será executada quando o botão de favorito for clicado.
        // A lambda simplesmente delega a chamada para o ViewModel.
        movieAdapter = MovieAdapter(
            onFavoriteClick = { movieId ->
                viewModel.onToggleFavorite(movieId)
            }
        )
        binding.moviesRecyclerView.adapter = movieAdapter
    }

    private fun setupListeners() {
        // Listener para a barra de busca
        binding.searchEditText.addTextChangedListener { editable ->
            viewModel.onSearchQueryChanged(editable.toString())
        }

        // Listener para o grupo de filtros de gênero
        binding.genreChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            if (chip != null) {
                viewModel.onGenreSelected(chip.text.toString())
            }
        }

        // Listener para o botão de tentar novamente na tela de erro
        binding.retryButton.setOnClickListener {
            viewModel.fetchMovies()
        }
    }

    private fun observeViewModelState() {
        // Inicia uma corrotina que observa o estado do ViewModel de forma segura
        // em relação ao ciclo de vida da Activity.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect { state ->
                    // Este bloco 'when' é o coração da lógica imperativa.
                    // Para cada estado, manipulamos a visibilidade e os dados das Views.
                    when (state) {
                        is MovieScreenState.Loading -> {
                            binding.loadingProgressBar.visibility = View.VISIBLE
                            binding.errorViewContainer.visibility = View.GONE
                            binding.moviesRecyclerView.visibility = View.GONE
                        }
                        is MovieScreenState.Error -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            binding.errorViewContainer.visibility = View.VISIBLE
                            binding.errorTextView.text = state.message
                            binding.moviesRecyclerView.visibility = View.GONE
                        }
                        is MovieScreenState.Success -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            binding.errorViewContainer.visibility = View.GONE
                            binding.moviesRecyclerView.visibility = View.VISIBLE

                            // Atualiza a lista de filmes no adapter
                            movieAdapter.submitList(state.displayedMovies)
                            // Atualiza a lista de favoritos no adapter
                            movieAdapter.updateFavorites(state.favoriteMovieIds)

                            // Popula os chips de gênero (apenas uma vez para evitar recriação)
                            if (binding.genreChipGroup.childCount == 0) {
                                populateGenreChips(state.availableGenres)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun populateGenreChips(genres: List<String>) {
        binding.genreChipGroup.removeAllViews() // Limpa antes de adicionar
        genres.forEach { genre ->
            val chip = Chip(this).apply {
                text = genre
                isCheckable = true
                // O primeiro chip (ex: "Todos") será marcado por padrão
                isChecked = (genre == genres.first())
            }
            binding.genreChipGroup.addView(chip)
        }
    }
}