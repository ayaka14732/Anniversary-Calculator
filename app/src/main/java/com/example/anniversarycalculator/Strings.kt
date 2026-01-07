package com.example.anniversarycalculator

data class Strings(
    val title: String,
    val addNew: String,
    val anniversaryTitle: String,
    val selectDate: String,
    val cancel: String,
    val save: String,
    val empty: String,
    val emptyDesc: String,
    val years: String,
    val months: String,
    val days: String,
    val today: String,
    val ago: String,
    val later: String
) {
    companion object {
        val da = Strings(
            title = "Mindedage",
            addNew = "Tilføj mindedag",
            anniversaryTitle = "Navn på mindedag",
            selectDate = "Vælg dato",
            cancel = "Annuller",
            save = "Gem",
            empty = "Ingen mindedage endnu",
            emptyDesc = "Tryk på knappen nedenfor for at tilføje din første mindedag",
            years = " år",
            months = " måneder",
            days = " dage",
            today = "Det er i dag!",
            ago = " siden",
            later = " fra nu"
        )

        val zh = Strings(
            title = "紀念日",
            addNew = "添加紀念日",
            anniversaryTitle = "紀念日名稱",
            selectDate = "選擇日期",
            cancel = "取消",
            save = "保存",
            empty = "還沒有紀念日",
            emptyDesc = "點擊下方按鈕添加第一個紀念日",
            years = " 年",
            months = " 個月",
            days = " 天",
            today = "就是今天！",
            ago = "前",
            later = "後"
        )

        val en = Strings(
            title = "Anniversaries",
            addNew = "Add Anniversary",
            anniversaryTitle = "Anniversary Title",
            selectDate = "Select Date",
            cancel = "Cancel",
            save = "Save",
            empty = "No anniversaries yet",
            emptyDesc = "Tap the button below to add your first anniversary",
            years = " years",
            months = " months",
            days = " days",
            today = "Today!",
            ago = " ago",
            later = " from now"
        )
    }
}
