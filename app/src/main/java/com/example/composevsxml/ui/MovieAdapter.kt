import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.R as MaterialR // Import para o ícone de erro do Coil
import com.example.composevsxml.R // Importe o R do seu projeto
import com.example.composevsxml.data.Movie
import com.example.composevsxml.databinding.ItemMovieBinding

// O Adapter recebe uma função lambda no seu construtor.
// Este é o padrão para comunicar eventos de clique para fora do adapter.
class MovieAdapter(
    private val onFavoriteClick: (movieId: Int) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback) {

    // Mantém uma referência ao conjunto atual de IDs de filmes favoritos.
    private var favoriteMovieIds: Set<Int> = emptySet()

    /**
     * Atualiza o conjunto de favoritos e notifica o adapter para redesenhar
     * os itens visíveis, atualizando o estado dos corações.
     */
    fun updateFavorites(newFavoriteMovieIds: Set<Int>) {
        favoriteMovieIds = newFavoriteMovieIds
        notifyDataSetChanged() // Embora ListAdapter lide com a lista, para
        // mudanças de estado internas como esta, notificar
        // a mudança é uma abordagem simples.
    }

    // O ViewHolder contém a lógica para conectar os dados a um item da UI.
    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitleTextView.text = movie.title
            binding.movieDetailsTextView.text = "${movie.releaseYear} • ★ ${movie.rating}"

            // Usa a biblioteca Coil para carregar a imagem do pôster a partir de uma URL.
            binding.moviePosterImageView.load(movie.posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background) // Imagem temporária
                error(MaterialR.drawable.ic_mtrl_chip_close_circle) // Imagem de erro
            }

            // Define o ícone de coração com base no estado atual de favoritos.
            val isFavorite = favoriteMovieIds.contains(movie.id)
            val heartIcon = if (isFavorite) {
                R.drawable.ic_favorite_filled
            } else {
                R.drawable.ic_favorite_border
            }
            binding.favoriteButton.setImageResource(heartIcon)

            // Configura o listener de clique para o botão de favorito.
            binding.favoriteButton.setOnClickListener {
                onFavoriteClick(movie.id)
            }
        }
    }

    // Chamado pelo RecyclerView quando precisa criar um novo ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Usa View Binding para "inflar" o layout XML do item.
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    // Chamado pelo RecyclerView para exibir os dados na posição especificada.
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    // O DiffUtil ajuda o ListAdapter a descobrir o que mudou na lista.
    private object MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        // Verifica se os itens são os mesmos (ex: mesmo ID).
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        // Verifica se o conteúdo dos itens mudou (ex: título, nota, etc.).
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem // Data class faz a comparação de todas as propriedades.
        }
    }
}