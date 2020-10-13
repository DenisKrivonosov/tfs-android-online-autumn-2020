package ru.krivonosovdenis.fintechapp.rvcomponents

interface DecorationTypeProvider {
    fun getDecorationType(position: Int): PostsListDecorationType
}
