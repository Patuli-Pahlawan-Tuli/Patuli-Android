package com.puxxbu.PatuliApp.data.model

data class QuizModel(
    val quiz_title: String,
    val quiz_difficulty : String,
    val start_number : Int = 1,
    val img_url : String,
    val desc : String,
    val completed_quiz_req : Int = 0,
    var is_enabled : Boolean = true,
    val subQuiz: List<SubQuiz>
)

data class SubQuiz(
    var is_enabled: Boolean = true,
    val unlock_requirement : Int
)


var quizList : List<QuizModel> = listOf(
    QuizModel(
        "Kuis Pemula",
        "Beginner",
        start_number = 1,
        "https://cdn.discordapp.com/attachments/946779825325084682/1115506988361724024/abc.png",
        "Kuis ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat mulai dari Angka",
        completed_quiz_req = 0,
        subQuiz = listOf(SubQuiz(unlock_requirement = 0), SubQuiz(unlock_requirement = 1))
    ),
    QuizModel(
        "Kuis Menengah",
        "Intermediate",
        start_number = 1,
        "https://cdn.discordapp.com/attachments/946779825325084682/1116263244672270398/word-of-mouth.png",
        "Kuis ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat yang berhubungan dengan Alphabet A-Z",
        completed_quiz_req = 1,
        subQuiz = listOf(SubQuiz(unlock_requirement = 2), SubQuiz(unlock_requirement = 3))
    ),
    QuizModel(
        "Kuis Mahir",
        "Expert",
        start_number = 1,
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657389698039858/signs.png",
        "Kuis ini akan menguji kemampuan kalian dalam memperagakan gerakan bahasa isyarat yang digunakan sehari-hari",
        completed_quiz_req = 2,
        subQuiz = listOf(SubQuiz(unlock_requirement = 4), SubQuiz(unlock_requirement = 5))
    ),

)