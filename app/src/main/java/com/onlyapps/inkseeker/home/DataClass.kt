package com.onlyapps.inkseeker.home

data class HomeDataClass(var id: Int, var dataPicLink: String,var dataName: String, var dataAddress: String, var dataStars: String, var lat: Double, var lng: Double) {
}

data class StyleDataClass(var id: String, var isSelected: Boolean) {
}