package com.puxxbu.PatuliApp.ui.fragments.lesson.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puxxbu.PatuliApp.PatuliApp.Companion.context
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.LessonItemModel
import com.puxxbu.PatuliApp.databinding.ItemLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.LessonListActivity

class LessonAdapter (private val items: List<LessonItemModel>) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding : ItemLessonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LessonItemModel) {

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
           binding.apply {
               Glide.with(itemView.context)
                   .load(item.image_url)
                   .placeholder(circularProgressDrawable)
                   .fitCenter()
                   .diskCacheStrategy(DiskCacheStrategy.DATA)
                   .into(ivLessonPhoto)
               tvLessonTitle.text = item.title
               tvDifficulty.text = item.difficulty
               var color = when(item.difficulty){
                   "Pemula" -> R.color.pemula
                   "Menengah" -> R.color.menengah
                   "Mahir" -> R.color.mahir
                   else -> R.color.pemula
               }
               ViewCompat.setBackgroundTintList(tvDifficulty, context.getColorStateList(color))
               tvLessonDesc.text = item.description
               tvTimeNeeded.text = context.getString(R.string.time_needed, item.hour_needed.toString())
           }

            binding.cardLesson.setOnClickListener{
                val intent = Intent(itemView.context, LessonListActivity::class.java)
                intent.putExtra(LessonListActivity.EXTRA_LESSON_NAME, item.title)
                itemView.context.startActivity(intent)
            }

        }
    }
}