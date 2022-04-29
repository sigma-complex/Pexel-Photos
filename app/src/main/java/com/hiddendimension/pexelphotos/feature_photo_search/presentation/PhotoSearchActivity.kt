package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.hiddendimension.pexelphotos.R
import com.hiddendimension.pexelphotos.databinding.ActivityPhotoSearchBinding
import com.hiddendimension.pexelphotos.di.AppModule
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pexelPhotoSearchActivityBinding =
            ActivityPhotoSearchBinding.inflate(layoutInflater)
        val pexelPhotoSearchActivityView = pexelPhotoSearchActivityBinding.root
        setContentView(pexelPhotoSearchActivityView)

        val pexelPhotoSearchActivityViewModel =
            ViewModelProvider(this, AppModule.provideViewModelFactory(owner = this))
                .get(PhotoSearchViewModel::class.java)



    }
}