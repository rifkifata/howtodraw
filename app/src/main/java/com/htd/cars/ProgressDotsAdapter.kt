package com.htd.cars

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ProgressDotsAdapter(
    private val context: Context,
    private val totalSteps: Int
) : RecyclerView.Adapter<ProgressDotsAdapter.ProgressDotViewHolder>() {

    private var currentPosition = 0

    class ProgressDotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressDot: View = itemView.findViewById(R.id.progressDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressDotViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_progress_dot, parent, false)
        return ProgressDotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgressDotViewHolder, position: Int) {
        val themeColors = ThemeHelper.getCurrentThemeColors(context)
        
        // Remove margin for last item
        val layoutParams = holder.progressDot.layoutParams as? ViewGroup.MarginLayoutParams
        if (position == totalSteps - 1) {
            layoutParams?.marginEnd = 0
        } else {
            layoutParams?.marginEnd = context.resources.getDimensionPixelSize(R.dimen.progress_dot_margin)
        }
        holder.progressDot.layoutParams = layoutParams
        
        if (position == currentPosition) {
            // Active dot
            holder.progressDot.setBackgroundResource(R.drawable.progress_dot_active)
            val background = holder.progressDot.background
            if (background is android.graphics.drawable.GradientDrawable) {
                background.setColor(android.graphics.Color.parseColor(themeColors.primaryColor))
            }
        } else {
            // Inactive dot
            holder.progressDot.setBackgroundResource(R.drawable.progress_dot_inactive)
            val background = holder.progressDot.background
            if (background is android.graphics.drawable.GradientDrawable) {
                background.setColor(android.graphics.Color.parseColor("#E0E0E0"))
            }
        }
    }

    override fun getItemCount(): Int = totalSteps

    fun updateCurrentPosition(newPosition: Int) {
        val oldPosition = currentPosition
        currentPosition = newPosition
        
        // Notify specific items changed for better performance
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }
}
