package ru.krivonosovdenis.fintechapp.rvcomponents

sealed class PostsListDecorationType {
    object Space : PostsListDecorationType()
    class WithText(
        val text: String
    ) : PostsListDecorationType()
}
