package id.namikaze.moviescatalog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.namikaze.moviescatalog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}