package com.fantasmaplasma.beta.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import com.fantasmaplasma.beta.R

class ImageAdapter(private val mContext: Context, private val startIntentChooseImage: () -> Unit) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private var mImage = listOf<Image>()
    private val mImageClickListener = View.OnClickListener {
        startIntentChooseImage()
    }

    fun setImageList(images: List<Image>) {
        mImage = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder =
        ImageHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.list_route_image_item, parent, false)
        )

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount() = // Increase by 1 for add item
        mImage.size + 1

    inner class ImageHolder(view: View): RecyclerView.ViewHolder(view) {
        private val imgBackground = view.findViewById<View>(R.id.cv_list_route_image)
        private val imgRoute = view.findViewById<ImageView>(R.id.iv_list_route_image_source)

        init {
            imgBackground.setOnClickListener(mImageClickListener)
        }

        fun bindViewHolder(idx: Int) {
            val isSelectedImage = idx < mImage.size
            Glide.with(mContext)
                .load(
                    if(isSelectedImage)
                        mImage[idx].path
                    else
                        R.drawable.img_add_route_add_image
                    )
                .into(imgRoute)
        }

    }

}