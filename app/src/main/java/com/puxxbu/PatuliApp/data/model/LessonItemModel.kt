package com.puxxbu.PatuliApp.data.model

data class LessonItemModel(
    val title: String,
    val hour_needed: Int,
    val description: String,
    val difficulty: String,
    val image_url: String
)

val itemLesson: List<LessonItemModel> = listOf(
    LessonItemModel(
        "Abjad",
        1,
        "Dalam sesi belajar ini Anda akan diajarkan bagaimana cara memperagakan bahasa isyarat dari huruf A-Z dalam standar Bisindo",
        "Mudah",
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657813872181258/abc-block.png"
    ),
    LessonItemModel(
        "Angka",
        1,
        "Dalam sesi belajar ini Anda akan diajarkan bagaimana cara memperagakan bahasa isyarat dari angka 0-10 dalam standar Bisindo",
        "Mudah",
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657814283227166/number-blocks.png"
    ),
    LessonItemModel(
        "Kata",
        2,
        "Dalam sesi belajar ini Anda akan diajarkan bagaimana cara memperagakan bahasa isyarat dari kata-kata umum sehingga dapat mempermudah Anda dalam berkomunikasi dengan para tuna rungu",
        "Mudah",
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657814618767360/talking.png"
    ),
)

