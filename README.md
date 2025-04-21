# ScheduleAPP 📅🔔

Android-приложение для создания событий в календаре с интеллектуальными уведомлениями.
Управление напоминаниями и моментальный доступ к предстоящим активностям.

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![WorkManager](https://img.shields.io/badge/WorkManager-5C6BC0?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/workmanager)
[![Clean Architecture](https://img.shields.io/badge/Clean_Architecture-6DB33F?style=for-the-badge)](https://developer.android.com/topic/architecture)

## 🌟 Ключевые возможности
- Создание/редактирование событий с датой и временем

- Гибкие настройки уведомлений 

- Просмотр календаря с визуализацией ивентов

- Оффлайн-доступ к данным и синхронизация

- Реализована темная/светлая темы

## 🛠 Технологии
- **Язык**: Kotlin

- **UI**: Jetpack Compose, Material 3

- **DI**: Hilt

- **Локальная БД**: Room

- **Фоновая обработка**: WorkManager

- **Асинхронность**: Kotlin Coroutines, Flow, StateFlow

- **Архитектура**: MVVM, Clean Architecture

## 🔄 Архитектура жизненного цикла
```mermaid
sequenceDiagram
    Пользователь->>UI: Создает событие
    UI->>ViewModel: Передает данные
    ViewModel->>Repository: Сохраняет в БД
    Repository->>WorkManager: Ставит в очередь уведомление
    WorkManager->>NotificationService: Триггерит по времени
    NotificationService->>Пользователь: Показывает уведомление
    Repository->>UI: Обновляет список через Flow
```

MIT License © Дмитрий Тимонин.
