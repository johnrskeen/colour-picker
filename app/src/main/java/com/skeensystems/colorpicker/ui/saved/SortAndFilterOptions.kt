package com.skeensystems.colorpicker.ui.saved

interface SortAndFilterOptions {
    val label: String
}

enum class SortOptions(
    override val label: String,
) : SortAndFilterOptions {
    NEWEST_FIRST("Newest first"),
    OLDEST_FIRST("Oldest first"),
    BY_COLOUR("By Colour"),
}

enum class FilterOptions(
    override val label: String,
) : SortAndFilterOptions {
    NO_FILTER("No filter"),
    FAVOURITES("Favourites"),
}
