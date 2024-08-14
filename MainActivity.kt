package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private  val binding:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("allahabad");
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName,"ae2ed8afd0fb2c102690586b5d70ff0e","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if (response.isSuccessful && responseBody !=null){
                    val temperature=responseBody.main.temp.toString();
                    val humidity=responseBody.main.humidity.toString();
                    val windSpeed=responseBody.wind.speed.toString();
                    val Sunrise=responseBody.sys.sunrise.toLong();
                    val Sunset=responseBody.sys.sunset.toLong();
                    val Sealevel=responseBody.main.pressure.toString();
                    val conditon=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val Maxtemp=responseBody.main.temp_max
                    val MinTemp=responseBody.main.temp_min

                    binding.textView3.text="$temperature °C"
                    binding.textView4.text=conditon
                    binding.textView5.text="Max Temp: $Maxtemp °C"
                    binding.min.text="Min Temp: $MinTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$windSpeed m/s"
                    binding.sunrise.text="${time(Sunrise)}"
                    binding.sunset.text="${time(Sunset)}"
                    binding.sea.text="$Sealevel"
                    binding.condition.text=conditon
                    binding.textView.text="$cityName"
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()

                    changeImageToCondition(conditon);
                }

            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {

            }

        })

    }
    private fun changeImageToCondition(conditions:String){
        when(conditions){
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
            }
            "Haze","Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background);
                binding.lottieAnimationView.setAnimation(R.raw.cloud);
            }
            "Thunderstorm","Light Rain","Moderate Rain","Heavy Rain","Drizzle","Showers" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background);
                binding.lottieAnimationView.setAnimation(R.raw.rain);
            }
            "Light snow","Moderate snow","Heavy snow","Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background);
                binding.lottieAnimationView.setAnimation(R.raw.snow);
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
            }
        }
        binding.lottieAnimationView.playAnimation();

    }

    private fun date(): String {
        val  sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}