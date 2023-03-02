package com.example.retrofcorout.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey

@BindingAdapter("app:src_glide")
fun setSrc(view: ImageView, src: String?){
    src?.let {
        Glide.with(view.context)
            .load(src)
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .into(view)
    }
}