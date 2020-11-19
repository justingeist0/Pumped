package com.fantasmaplasma.beta.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fantasmaplasma.beta.R
import java.net.URI

class ImageAdapter(private val mContext: Context, startIntentChooseImage: () -> Unit) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private val mImage = mutableListOf<URI>()

    fun addItem(image: URI) {
        val idxStart = mImage.size
        mImage.add(image)
        notifyItemRangeInserted(idxStart, mImage.size)
    }

    private fun removeItem(idx: Int) {
        mImage.removeAt(idx)
        notifyItemRemoved(idx)
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
        mImage.size + 3

    inner class ImageHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val imgButton = view.findViewById<ImageView>(R.id.iv_list_route_image_btn)
        val imgRoute = view.findViewById<ImageView>(R.id.iv_list_route_image_source)

        fun bindViewHolder(idx: Int) {
            if(idx >= mImage.size) {

            } else {

            }
        }

    }

}