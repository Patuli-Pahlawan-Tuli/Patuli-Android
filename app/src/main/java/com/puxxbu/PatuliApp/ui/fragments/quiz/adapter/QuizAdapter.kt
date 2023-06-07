package com.puxxbu.PatuliApp.ui.fragments.quiz.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.data.model.QuizModel
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puxxbu.PatuliApp.databinding.DialogQuizConfirmationBinding
import com.puxxbu.PatuliApp.databinding.ItemQuizBinding
import com.puxxbu.PatuliApp.ui.fragments.quiz.QuizActivity

class QuizAdapter (private val items: List<QuizModel>) : RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding : ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuizModel) {

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            binding.apply {
                tvLessonTitle.text = item.quiz_title
                Glide.with(itemView.context)
                    .load(item.img_url)
                    .fitCenter()
                    .into(ivQuizImage)
                tvLessonDesc.text = item.desc

                btnStartLesson.setOnClickListener {

                    val dialogView = DialogQuizConfirmationBinding.inflate(LayoutInflater.from(itemView.context))
                    val okButton = dialogView.okButton
                    val cancelButton = dialogView.btnDismiss

                    val builder = MaterialAlertDialogBuilder(itemView.context)
                    builder.setView(dialogView.root)

                    val dialog = builder.create()
                    okButton.setOnClickListener {
                        val intent = Intent(itemView.context, QuizActivity::class.java)
                        intent.putExtra(QuizActivity.EXTRA_QUIZ_DIFFICULTY, item.quiz_difficulty)
                        intent.putExtra(QuizActivity.EXTRA_NUMBER, item.start_number)
                        itemView.context.startActivity(intent)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }


                    dialog.show()


                }
            }

        }
    }

    private fun showDialogInformation(){

    }
}