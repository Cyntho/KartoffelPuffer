package org.cyntho.fh.kotlin.kartoffelpuffer.data

import org.cyntho.fh.kotlin.kartoffelpuffer.net.AllergyWrapper

data class Dish(val dishId: Int,
                val active: Boolean,
                val iconLink: String,
                val name: String,
                val allergies: List<AllergyWrapper>,
                val description: String)
