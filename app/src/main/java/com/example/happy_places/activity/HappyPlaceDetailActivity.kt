package com.example.happy_places.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.happy_places.databinding.ActivityHappyPlaceDetailBinding
import com.example.happy_places.model.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var happyPlaceDetailModel: HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            happyPlaceDetailModel =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (happyPlaceDetailModel != null) {

            setSupportActionBar(binding?.toolbarHappyPlaceDetail)
            supportActionBar!!.apply {
                setDisplayHomeAsUpEnabled(true)
                title = happyPlaceDetailModel.title
            }

            binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }

            binding?.apply {
                ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
                tvDescription.text = happyPlaceDetailModel.description
                tvLocation.text = happyPlaceDetailModel.location
            }
        }

        binding?.btnViewOnMap?.setOnClickListener {
            val intent = Intent(this@HappyPlaceDetailActivity, MapActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceDetailModel)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}