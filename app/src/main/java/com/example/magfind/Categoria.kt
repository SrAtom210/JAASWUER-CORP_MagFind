import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

data class Categoria(
    val id: Int? = null,
    val nombre: String
)

interface ApiService {
    @POST("categorias")
    suspend fun addCategoria(@Body categoria: Categoria): Response<String>

    @GET("categorias")
    suspend fun getCategorias(): List<Categoria>
}

object RetrofitClient {
    private const val BASE_URL = "http://TU_IP:PUERTO/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
