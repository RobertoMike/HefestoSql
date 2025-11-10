package io.github.robertomike.hefesto.utils

/**
 * Represents a paginated result set from a database query.
 * Contains the data for a specific page along with pagination metadata.
 * 
 * Example usage:
 * ```java
 * Page<User> page = Hefesto.make(User.class)
 *     .orderBy("name")
 *     .paginate(1, 20);  // page 1, 20 items per page
 * 
 * List<User> users = page.getData();
 * long currentPage = page.getPage();
 * long totalRecords = page.getTotal();
 * int totalPages = page.getTotalPages();
 * ```
 * 
 * @param T the type of entities in the page
 * @property data the list of entities for this page
 * @property page the current page number (1-indexed)
 * @property total the total number of records across all pages
 */
data class Page<T>(
    val data: List<T>,
    val page: Long,
    val total: Long
) {
    /**
     * Calculates the total number of pages based on page size.
     * 
     * @return the total number of pages
     */
    fun getTotalPages(): Int {
        if (data.isEmpty()) return 0
        return ((total + data.size - 1) / data.size).toInt()
    }
}
