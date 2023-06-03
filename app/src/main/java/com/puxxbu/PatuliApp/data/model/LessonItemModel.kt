package com.puxxbu.PatuliApp.data.model

data class LessonItemModel(
    val title : String,
    val video_count : Int,
    val description : String,
    val image_url : String
)

val itemLesson : List<LessonItemModel> = listOf(
    LessonItemModel(
        "Abjad A-Z",
        3,
        "This is a description",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    LessonItemModel(
        "Angka",
        3,
        "This is a description",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    LessonItemModel(
        "Kata",
        3,
        "This is a description",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    )

