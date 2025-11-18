import com.example.magfind.models.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

// Modelo
data class Categoria(
    val id: Int? = null,
    val nombre: String
)

// Wrapper para el GET
data class CategoriaResponse(
    val items: List<Categoria>
)


// Retrofit Client
object RetrofitClient {
    private const val BASE_URL = "https://gf8cee49287ea17-magfindgps.adb.us-ashburn-1.oraclecloudapps.com/ords/admin/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
