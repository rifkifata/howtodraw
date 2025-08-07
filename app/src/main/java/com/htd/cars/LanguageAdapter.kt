package com.htd.cars

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LanguageAdapter(
    private val languages: Array<String>,
    private val languageCodes: Array<String>,
    private val currentLanguageCode: String,
    private val onLanguageSelected: (String) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition = languageCodes.indexOf(currentLanguageCode)

    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagEmoji: TextView = itemView.findViewById(R.id.flagIcon)
        val languageName: TextView = itemView.findViewById(R.id.languageName)
        val selectionIndicator: TextView = itemView.findViewById(R.id.selectionIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.languageName.text = language

        // Set flag emoji based on language
        val flagEmoji = getFlagEmoji(language)
        holder.flagEmoji.text = flagEmoji
        // Set warna dan shadow pada flag agar kontras
        holder.flagEmoji.setTextColor(android.graphics.Color.parseColor("#000000"))
        holder.flagEmoji.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))

        // Show selection indicator for current language
        if (position == selectedPosition) {
            holder.selectionIndicator.visibility = View.VISIBLE
            holder.itemView.isSelected = true
        } else {
            holder.selectionIndicator.visibility = View.GONE
            holder.itemView.isSelected = false
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onLanguageSelected(languageCodes[position])
        }
    }

    override fun getItemCount(): Int = languages.size

    private fun getFlagEmoji(language: String): String {
        return when (language) {
            "English" -> "\uD83C\uDDFA\uD83C\uDDF8"
            "Indonesia" -> "\uD83C\uDDEE\uD83C\uDDE9"
            "\u4e2d\u6587" -> "\uD83C\uDDE8\uD83C\uDDF3"
            "\u0939\u093f\u0928\u094d\u0926\u0940" -> "\uD83C\uDDEE\uD83C\uDDF3"
            "Espa\u00f1ol" -> "\uD83C\uDDEA\uD83C\uDDF8"
            "\u0627\u0644\u0639\u0631\u0628\u064a\u0629" -> "\uD83C\uDDF8\uD83C\uDDE6"
            "Fran\u00e7ais" -> "\uD83C\uDDEB\uD83C\uDDF7"
            "Portugu\u00eas" -> "\uD83C\uDDF5\uD83C\uDDF9"
            "\u09ac\u09be\u0982\u09b2\u09be" -> "\uD83C\uDDE7\uD83C\uDDE9"
            "\u0420\u0443\u0441\u0441\u043a\u0438\u0439" -> "\uD83C\uDDF7\uD83C\uDDFA"
            "\u65e5\u672c\u8a9e" -> "\uD83C\uDDEF\uD83C\uDDF5"
            "Deutsch" -> "\uD83C\uDDE9\uD83C\uDDEA"
            "\u0627\u0631\u062f\u0648" -> "\uD83C\uDDF5\uD83C\uDDF0"
            "\u0641\u0627\u0631\u0633\u06cc" -> "\uD83C\uDDEE\uD83C\uDDF7"
            "\u0e44\u0e17\u0e22" -> "\uD83C\uDDF9\uD83C\uDDED"
            "Italiano" -> "\uD83C\uDDEE\uD83C\uDDF9"
            "\uD55C\uAD6D\uC5B4" -> "\uD83C\uDDF0\uD83C\uDDF7"
            "Bahasa Melayu" -> "\uD83C\uDDF2\uD83C\uDDFE"
            "Ti\u1ebfng Vi\u1ec7t" -> "\uD83C\uDDFB\uD83C\uDDF3"
            else -> "\uD83C\uDDFA\uD83C\uDDF8"
        }
    }
}
