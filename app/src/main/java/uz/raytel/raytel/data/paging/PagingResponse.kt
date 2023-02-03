package uz.raytel.raytel.data.paging

data class PagingResponse<T>(
    val data: List<T>,
    val pagination: Pagination
)
