package com.fantasmaplasma.beta.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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

    override fun getItemCount() =
        mImage.size + 1

    inner class ImageHolder(view: View): RecyclerView.ViewHolder(view) {
        private val imgBackground = view.findViewById<ImageView>(R.id.iv_list_route_image_background)
        private val imgRoute = view.findViewById<ImageView>(R.id.iv_list_route_image_source)

        init {
            imgBackground.setOnClickListener(mImageClickListener)
        }

        fun bindViewHolder(idx: Int) {
            if(idx < mImage.size) {
                val image = mImage[idx].uri
                val inputStream = mContext.contentResolver.openInputStream(image)
                BitmapFactory.decodeStream(inputStream)
                imgRoute.setImageURI(image)
            } else {
                imgRoute.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.img_add_route_add_image))
            }
        }

    }

}