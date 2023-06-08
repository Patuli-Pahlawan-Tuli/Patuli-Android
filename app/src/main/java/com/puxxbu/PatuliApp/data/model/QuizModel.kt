package com.puxxbu.PatuliApp.data.model

data class QuizModel(
    val quiz_title: String,
    val quiz_difficulty : String,
    val start_number : Int = 1,
    val img_url : String,
    val desc : String,
    val completed_quiz_req : Int = 0,
    var is_enabled : Boolean = true,
)


var quizList : List<QuizModel> = listOf(
    QuizModel(
        "Quiz Beginner",
        "Beginner",
        start_number = 1,
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Quiz ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat mulai dari Abjad hingga Angka",
        completed_quiz_req = 0,
    ),
    QuizModel(
        "Quiz Intermediate",
        "Intermediate",
        start_number = 1,
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Quiz ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat yang berhubungan dengan Kata sehari-hari",
        completed_quiz_req = 1,
    ),
    QuizModel(
        "Quiz Expert",
        "Expert",
        start_number = 1,
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Quiz ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat yang cukup rumit dan jarang digunakan sehari-hari",
        completed_quiz_req = 2,
    ),
)