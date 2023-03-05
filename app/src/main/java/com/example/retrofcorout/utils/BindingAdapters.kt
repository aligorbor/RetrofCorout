package com.example.retrofcorout.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.retrofcorout.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:src_glide")
fun setSrc(view: ImageView, src: String?) {
    src?.let {
        Glide.with(view.context)
            .load(src)
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .into(view)
    }
}

@BindingAdapter("app:date_to_text")
fun setText(view: TextView, date: Date?) {
    val strFormat = view.context.getString(R.string.date_format)
    val myFormat = SimpleDateFormat(strFormat, Locale.getDefault(Locale.Category.FORMAT))
    date?.let {
        view.text = myFormat.format(date)
    }
}