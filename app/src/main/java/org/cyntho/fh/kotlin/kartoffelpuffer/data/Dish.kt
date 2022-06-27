package org.cyntho.fh.kotlin.kartoffelpuffer.data

data class Dish(val dishId: Int,
                val active: Boolean,
                val iconLink: String,
                val name: String,
                val allergies: List<Allergy>,
                val ingredients: String)
