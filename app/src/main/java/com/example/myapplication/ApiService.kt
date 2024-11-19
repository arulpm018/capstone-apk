import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("chat/")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>

    @POST("related_documents/")
    suspend fun getRelatedDocuments(@Body request: DocumentRequest): Response<DocumentResponse>
}

data class ChatRequest(
    val query: String,
    val context: String,
    val chat_history: List<Int> = emptyList()
)

data class DocumentRequest(
    val title: String,
    val number: Int = 3
)

data class ChatResponse(
    val response: String
)

data class DocumentResponse(
    val related_documents: List<RelatedDocument>
)
