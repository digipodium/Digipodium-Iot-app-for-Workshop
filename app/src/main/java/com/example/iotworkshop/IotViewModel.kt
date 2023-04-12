package com.example.iotworkshop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class IotViewModel(
    private val db: FirebaseDatabase
) : ViewModel() {

    private val _items: MutableLiveData<List<DataItem>> = MutableLiveData()
    val items: LiveData<List<DataItem>> = _items

    private fun loadDateFromFirebase() {
        db.getReference("dht11").get().addOnSuccessListener {
            val items = it.children.mapNotNull { child ->
                val value = child.getValue(DataItem::class.java)
                value
            }
            Log.d("IotViewModel", "items: $items, size: ${items.size}")
            _items.postValue(items)
        }
    }

    fun refresh() {
        loadDateFromFirebase()
    }
}