package com.example.composevsxml.data
/**
 * Representa o modelo de dados para um único filme.
 *
 * Sendo uma 'data class', o Kotlin gera automaticamente para nós os métodos
 * úteis como .equals(), .hashCode(), .toString() e .copy().
 */
data class Movie(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val posterUrl: String,
    val rating: Double,
    val genre: String
)