package com.compareprices.ui.components

fun displayBrand(brand: String?): String = if (brand.isNullOrBlank()) "Sin marca" else brand
