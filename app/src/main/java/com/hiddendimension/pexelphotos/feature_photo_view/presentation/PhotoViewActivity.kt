package com.hiddendimension.pexelphotos.feature_photo_view.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hiddendimension.pexelphotos.R
import com.hiddendimension.pexelphotos.databinding.ActivityPhotoViewBinding

class PhotoViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fullScreenImageViewActivityBinding =
            ActivityPhotoViewBinding.inflate(layoutInflater)
        val fullScreenImageViewActivityView = fullScreenImageViewActivityBinding.root
        setContentView(fullScreenImageViewActivityView)


        ViewModelProvider(this)
            .get(FullScreenImageViewActivityViewModel::class.java)

        val url = intent.getStringExtra("url")

        if (url != null) {
            loadImage(url, fullScreenImageViewActivityBinding.imageView)
        }


    }

    private fun loadImage(url: String, view: ImageView) {
        Glide.with(view.context)
            .load(url)
            .transform(CenterInside(), RoundedCorners(24))
            .into(view)
    }
}