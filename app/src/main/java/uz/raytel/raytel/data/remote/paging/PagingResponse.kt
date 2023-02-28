package uz.raytel.raytel.data.remote.paging

data class PagingResponse<T>(
    val data: List<T>,
    val pagination: Pagination
)
