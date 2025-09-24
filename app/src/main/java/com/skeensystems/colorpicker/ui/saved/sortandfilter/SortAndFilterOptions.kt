package com.skeensystems.colorpicker.ui.saved.sortandfilter

interface SortAndFilterOptions {
    val label: String
}

enum class SortOptions(
    override val label: String,
) : SortAndFilterOptions {
    NEWEST_FIRST("Newest first"),
    OLDEST_FIRST("Oldest first"),
    BY_COLOUR("By colour"),
    BY_R_VALUE("By red value"),
    BY_G_VALUE("By green value"),
    BY_B_VALUE("By blue value"),
}

enum class FilterOptions(
    override val label: String,
) : SortAndFilterOptions {
    NO_FILTER("No filter"),
    FAVOURITES("Favourites"),
}
