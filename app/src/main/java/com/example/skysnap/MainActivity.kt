package com.example.skysnap

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.ApiInterface
import com.WeatherApp
import com.example.skysnap.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//e9cee4bc8ca6cec4af332c7016d68fe5


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fetchWeatherData("Kolkata")
        SearchCity()

    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
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
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val responce=retrofit.getWeatherData(cityName,"e9cee4bc8ca6cec4af332c7016d68fe5","metric")
        responce.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, responce: Response<WeatherApp>) {
                val responceBody=responce.body()
                if(responce.isSuccessful && responceBody !=null){
                    val temperature=responceBody.main.temp.toString()
                    val humidity=responceBody.main.humidity
                    val windSpeed=responceBody.wind.speed
                    val sunRise=responceBody.sys.sunrise.toLong()
                    val sunSet=responceBody.sys.sunset.toLong()
                    val seeLevel=responceBody.main.pressure
                    val condition=responceBody.weather.firstOrNull()?.main?:"unkmown"
                    val maxTemp=responceBody.main.temp_max
                    val minTemp=responceBody.main.temp_min
                    //Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max Temp:$maxTemp °C"
                    binding.minTemp.text="Min Temp:$minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seeLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityname.text="$cityName"

                    ChangeImagesAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun ChangeImagesAccordingToWeatherCondition(conditions:String) {
        when(conditions){
            "Partly Clouds","Clouds","OverCast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
        }
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Snow","Moserate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return  sdf.format((Date()))
    }
    private fun time(timestamp:Long): String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format((Date(timestamp*1000)))
    }

    private fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
    }
}