package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.CommentData

interface CommentsActions {
    fun likeComment(comment: CommentData)
    fun dislikeComment(comment: CommentData)
}
