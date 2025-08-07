package com.htd.cars

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.io.ByteArrayInputStream
import com.bumptech.glide.Glide

import android.util.Log

class CarMenuAdapter(
    private val context: Context,
    private val carTypes: List<String>,
    private val onCarClick: (String) -> Unit
) : RecyclerView.Adapter<CarMenuAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.carImage)
        val titleView: TextView = itemView.findViewById(R.id.carTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_car_menu, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val carType = carTypes[position]

        // Set title
        holder.titleView.text = getCarDisplayName(carType)

        // Apply theme colors
        val themeColors = ThemeHelper.getCurrentThemeColors(context)
        (holder.itemView as androidx.cardview.widget.CardView).setCardBackgroundColor(android.graphics.Color.parseColor(themeColors.cardBackgroundColor))
        holder.titleView.setTextColor(android.graphics.Color.parseColor(themeColors.cardTextColor))

        // Load last image as thumbnail using Glide (from assets)
        try {
            val lastImagePath = getLastImageFromFolder(carType)
            if (lastImagePath != null) {
                val assetUri = "file:///android_asset/$lastImagePath"
                Log.d("CarMenuAdapter", "Loading image: $assetUri for position: $position, car type: $carType")

                // Configure Glide with simple image loading
                Glide.with(context)
                    .load(assetUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .fitCenter()
                    .into(holder.imageView)

            } else {
                Log.w("CarMenuAdapter", "No image found for car type: $carType")
                holder.imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        } catch (e: IOException) {
            Log.e("CarMenuAdapter", "Error loading image for car type: $carType", e)
            holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        }

        // Set click listener with animation
        holder.itemView.setOnClickListener {
            // Create scale animation for press feedback
            val scaleDown = android.animation.ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1f, 0.95f)
            val scaleDownY = android.animation.ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1f, 0.95f)
            val scaleUp = android.animation.ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.95f, 1f)
            val scaleUpY = android.animation.ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.95f, 1f)
            val scaleDownAnimator = android.animation.AnimatorSet()
            scaleDownAnimator.playTogether(scaleDown, scaleDownY)
            scaleDownAnimator.duration = 100
            val scaleUpAnimator = android.animation.AnimatorSet()
            scaleUpAnimator.playTogether(scaleUp, scaleUpY)
            scaleUpAnimator.duration = 100
            scaleUpAnimator.startDelay = 100
            val fullAnimator = android.animation.AnimatorSet()
            fullAnimator.playSequentially(scaleDownAnimator, scaleUpAnimator)
            fullAnimator.start()

            // Start tutorial activity after a short delay
            holder.itemView.postDelayed({
                onCarClick(carType)
            }, 150)
        }
    }
    
    private fun loadImageWithoutBackgroundRemoval(inputStream: java.io.InputStream): android.graphics.drawable.Drawable? {
        return try {
            val options = android.graphics.BitmapFactory.Options().apply {
                inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
            }
            
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
            if (bitmap != null) {
                android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun getItemCount(): Int = carTypes.size

    private fun getCarDisplayName(carType: String): String {
        return carType
    }
    
    private fun getLastImageFromFolder(folderName: String): String? {
        return try {
            val assetManager = context.assets
            val files = assetManager.list(folderName)
            
            if (files != null && files.isNotEmpty()) {
                // Sort files to find the one with highest number
                val sortedFiles = files.sortedBy { fileName ->
                    // Extract number from filename (e.g., "10.webp" -> 10, "1.webp" -> 1)
                    val numberStr = fileName.replace(Regex("[^0-9]"), "")
                    if (numberStr.isNotEmpty()) numberStr.toInt() else 0
                }
                
                val lastImageFile = sortedFiles.last()
                "$folderName/$lastImageFile"
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
