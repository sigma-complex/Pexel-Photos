package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hiddendimension.pexelphotos.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_search)
    }
}