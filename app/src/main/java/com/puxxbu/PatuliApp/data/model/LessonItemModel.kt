package com.puxxbu.PatuliApp.data.model

data class LessonItemModel(
    val title : String,
    val video_count : Int,
    val description : String,
    val image_url : String
)

val itemLesson : List<LessonItemModel> = listOf(
    LessonItemModel(
        "Lesson 1",
        3,
        "This is a description",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    LessonItemModel(
        "Lesson 2",
        3,
        "This is a description",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    LessonItemModel(
        "Lesson 3",
        3,
        "This is a description",
        "https://i.ytimg.com/vi/1Ne1hqOXKKI/maxresdefault.jpg"
    ),
    )

