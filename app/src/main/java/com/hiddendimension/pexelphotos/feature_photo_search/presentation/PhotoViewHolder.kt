package com.hiddendimension.pexelphotos.feature_photo_search.presentation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hiddendimension.pexelphotos.R
import com.hiddendimension.pexelphotos.feature_photo_search.domain.model.Photo
import com.hiddendimension.pexelphotos.feature_photo_view.presentation.PhotoViewActivity

class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.credit_name)
    private val photoImage: ImageView = view.findViewById(R.id.photoImage)


    private var photo: Photo? = null

    init {
        view.setOnClickListener {
            photo?.url?.let {
                Intent(view.context, PhotoViewActivity::class.java).apply {
                    this.putExtra("url", photo?.src?.medium)
                    view.context.startActivity(this)

                }
            }
        }
    }

    fun bind(repo: Photo?) {
        if (repo == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
            photoImage.visibility = View.GONE

        } else {
            showRepoData(repo)
        }
    }

    private fun showRepoData(photo: Photo) {
        this.photo = photo
        name.text = photo.photographer

        Glide.with(photoImage.context)
            .load(photo.src?.tiny)
            .transform(CenterInside(), RoundedCorners(24))
            .into(photoImage)

    }

    companion object {
        fun create(parent: ViewGroup): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_view_item, parent, false)
            return PhotoViewHolder(view)
        }
    }
}