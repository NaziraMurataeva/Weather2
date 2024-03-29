package kg.tutorialapp.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kg.tutorialapp.weather.databinding.ActivityMainBinding
import kg.tutorialapp.weather.models.ForeCast
import kg.tutorialapp.weather.network.WeatherApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
     lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root

        val call = weatherApi.getWeather()
        call.enqueue(object: Callback<ForeCast> {
            override fun onResponse(call: Call<ForeCast>, response: Response<ForeCast>) {
                if (response.isSuccessful) {
                    val foreCast = response.body()
                    foreCast?.let {
                        binding.textView.text = it.current?.weather!![0].description
                        binding.textView.text = it.timezone

                        Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

                override fun onFailure(call: Call<ForeCast>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }


       }  )
    }
    private val okhttp by lazy {
        val interceptor =HttpLoggingInterceptor{}.apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttp)
            .build()
    }
    private val weatherApi by lazy {
        retrofit.create(WeatherApi::class.java)

    }
}