package ru.krivonosovdenis.fintechapp.dataclasses

import androidx.room.Entity
import androidx.room.PrimaryKey

//Не самая надежная структура для расширения, но для моего приложения подходит больше всего
//Содержит название настройки, флаг, отвечающий за то, стоит ли использовать системную настройку в качестве
//дефолтной и настройку в стринговом формате, которую мы будем забирать, если флаг установлен в false
@Entity(tableName = "app_settings")
data class AppSettings(
    val settingsKey: String,
    val settingsShouldGetDefault: Boolean,
    val settingsValueString: String,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
