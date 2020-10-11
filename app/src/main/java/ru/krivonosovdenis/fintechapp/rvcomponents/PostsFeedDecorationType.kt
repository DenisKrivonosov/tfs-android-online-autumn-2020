package ru.krivonosovdenis.fintechapp.rvcomponents

sealed class PostsFeedDecorationType {
    object Space : PostsFeedDecorationType()
    class WithText(
        val text: String
    ) : PostsFeedDecorationType()
}
