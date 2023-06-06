package com.puxxbu.PatuliApp.data.model

data class QuizModel(
    val quiz_title: String,
    val quiz_type : String,
    val img_url : String,
    val desc : String,
)


val quizList : List<QuizModel> = listOf(
    QuizModel(
        "Quiz Beginner",
        "Beginner",
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Quiz ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat mulai dari Abjad hingga Angka",
    ),
    QuizModel(
        "Quiz Intermediate",
        "Intermediate",
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Quiz ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat yang berhubungan dengan Kata sehari-hari",
    ),
    QuizModel(
        "Quiz Expert",
        "Expert",
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Quiz ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat yang cukup rumit dan jarang digunakan sehari-hari",
    ),
)