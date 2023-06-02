package com.puxxbu.PatuliApp.data.model

data class LessonItemNavModel(
    val title : String,
    val lessonNumber : Int
)

val alphabethItemList = createLessonItems()


fun createLessonItems(): List<LessonItemNavModel> {
    val items = mutableListOf<LessonItemNavModel>()

    for (i in 1..26) {
        val title = ('A'.toInt() + i - 1).toChar().toString()
        val item = LessonItemNavModel(title, i)
        items.add(item)
    }

    return items
}