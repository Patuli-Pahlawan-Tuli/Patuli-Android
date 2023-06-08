package com.puxxbu.PatuliApp.data.model

data class LessonItemModel(
    val title : String,
    val hour_needed : Int,
    val description : String,
    val image_url : String
)

val itemLesson : List<LessonItemModel> = listOf(
    LessonItemModel(
        "Abjad",
        1,
        "Belajar abjad dari A-Z",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    LessonItemModel(
        "Angka",
        1,
        "Belajar Angka 1-10",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    LessonItemModel(
        "Kata",
        2,
        "Belajar kata sehari-hari",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    )

