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
        "Belajar abjad dari A-Z",
        "Mudah",
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657813872181258/abc-block.png"
    ),
    LessonItemModel(
        "Angka",
        1,
        "Belajar Angka 1-10",
        "Mudah",
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657814283227166/number-blocks.png"
    ),
    LessonItemModel(
        "Kata",
        2,
        "Belajar kata sehari-hari",
        "Mudah",
        "https://cdn.discordapp.com/attachments/946779825325084682/1116657814618767360/talking.png"
    ),
)

