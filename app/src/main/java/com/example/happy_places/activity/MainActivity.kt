@file:Suppress("DEPRECATION")

package com.example.happy_places.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happy_places.adapter.HappyPlacesAdapter
import com.example.happy_places.database.DatabaseHandler
import com.example.happy_places.databinding.ActivityMainBinding
import com.example.happy_places.model.HappyPlaceModel
import com.example.happy_places.util.SwipeToDeleteCallback
import com.example.happy_places.util.SwipeToEditCallback

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        getHappyPlacesListFromLocalDB()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    private fun getHappyPlacesListFromLocalDB() {

        val dbHandler = DatabaseHandler(this)

        val getHappyPlacesList = dbHandler.getHappyPlacesList()

        if (getHappyPlacesList.size > 0) {
            binding?.apply {
                rvHappyPlacesList.visibility = View.VISIBLE
                tvNoRecordsAvailable.visibility = View.GONE
            }
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        } else {
            binding?.apply {
                rvHappyPlacesList.visibility = View.GONE
                tvNoRecordsAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {

        binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
        binding?.rvHappyPlacesList?.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlacesList)
        binding?.rvHappyPlacesList?.adapter = placesAdapter

        placesAdapter.setOnClickListener(object :
            HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvHappyPlacesList?.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlacesList)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvHappyPlacesList?.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPlacesListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlacesList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}