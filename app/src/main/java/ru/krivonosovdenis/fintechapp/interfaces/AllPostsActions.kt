package ru.krivonosovdenis.fintechapp.interfaces

import ru.krivonosovdenis.fintechapp.dataclasses.PostRenderData

//Вообще я сначала в качестве параметра для всех методов передавал position элемента. Но более честно
//передавать уникальный postId (в нем хранится связка postId+groupId). Так мы не привязываемся к
// конкретной реализации списка, избегаем возможных ошибок при добавлении и удалении элементов.
// Также с помощью такой реализации проще реализовать список с понравившимися постами. Мы, по факту,
// используем один источник данных для реализации двух списков. При подходе, используещем postId
// вместо position, для обоих списков можно использовать один общий обработчик
// fun onPostClicked(postId: String). С position такое провернуть не получилось бы.
// Потом, когда мы пройдем запросы и бд, источники данных, скорее всего, разделятся
//текущий фид из сети, лайкнутые посты из бд. Пока для простоты решил сделать так
interface AllPostsActions {
    fun onPostDismiss(post: PostRenderData)
    fun onPostLiked(post:PostRenderData)
    fun onPostClicked(post: PostRenderData)
}
