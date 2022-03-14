package ru.netology

object ChatService {
    private var chats = mutableListOf<Chat>()

    //Создает новый чат
    fun addChat (name: String,
                 autorId: Int,
                 companionId: Int,
                 textFirstMessage: String): Chat {

        val chat = Chat(chats.getMaxGuidInList(), name, autorId, companionId)
        //id устанавливаем 1 т.к. это первое сообщение в чате в принципе
        val message = Message(1, autorId, textFirstMessage, (java.lang.System.currentTimeMillis() / 1000L).toInt())

        chat.messages += message
        chats += chat

        return chats.last()
    }

    //Получает чат по идентификатору
    fun getChatById (id: Int) : Chat? {
        val listChat = chats.toList().filter { it.id == id}
        return if (listChat.size == 0) null else listChat[0]
    }

    //Удаляет чат по идентификатору
    fun deleteChat (id: Int) {
        val chat = getChatById(id)
        if (chat != null) chats.remove(chat)
    }

    //Получает список чатов
    fun getChats () : MutableList<Chat> {
        return chats
    }

    //Создает новое сообщение
    fun addMessage (chat: Chat, autorId: Int, textMessage: String): Message {
        val message = Message(chat.messages.getMaxGuidInList(), autorId, textMessage, (java.lang.System.currentTimeMillis() / 1000L).toInt())
        chat.messages += message
        return chat.messages.last()
    }

    //Получает сообщение по идентификатору
    fun getMessageById (chat: Chat, id: Int) : Message? {
        val listMessage = chat.messages.toList().filter { it.id == id}
        return if (listMessage.size == 0) null else listMessage[0]
    }

    //Удаляет сообщение по идентификатору
    fun deleteMessage (chat: Chat, id: Int) {
        val message = getMessageById(chat, id)
        if (message != null) {
            chat.messages.remove(message)
            if (chat.messages.size == 0) chats.remove(chat)
        }
    }

    //Удаляет сообщение по идентификатору
    fun editMessage (chat: Chat, message: Message) {
        val messageInList = getMessageById(chat, message.id)
        val autorId: Int = (messageInList?.autorId ?: -1)
        if (autorId == message.autorId) chat.messages[chat.messages.indexOf(messageInList)] = message.copy()
    }

    //Получает список сообщений
    fun getMessages (chat: Chat, userId: Int) : MutableList<Message> {
        val unreadedMessages = chat.messages.toList().filter { it.autorId != userId }
        for (message in unreadedMessages) chat.messages[chat.messages.indexOf(message)] = message.copy(readed = true)
        return chat.messages
    }

    //Получает список непрочитанных чатов
    fun getUnreadChats (userId: Int) : MutableList<Chat> {
        val result = chats.toList().filter { chat: Chat -> chat.companionId == userId && chat.messages.toList().filter{message: Message -> message.autorId != userId && message.readed == false}.size > 0 }.toMutableList()
        result.addAll(chats.toList().filter { chat: Chat -> chat.autorId == userId && chat.messages.toList().filter{message: Message -> message.autorId != userId && message.readed == false}.size > 0 })

        return result
    }

    //Получает количество непрочитанных чатов
    fun getUnreadChatsCount (userId: Int) : Int {
        val unreadChats = getUnreadChats(userId)
        return unreadChats.size
    }

    //Получает список сообщений по расширенным параметрам
    fun getMessagesOnFilters (chat: Chat, userId: Int, messageId: Int, count: Int = 5) : MutableList<Message> {

        val messageInList = getMessageById(chat, messageId)
        val dateFilter: Int = (messageInList?.date ?: 0)
        val unreadedMessages = chat.messages.toList().filter{ it.autorId != userId && it.date >= dateFilter}
        val result = unreadedMessages.take(count)
        for (message in result) chat.messages[chat.messages.indexOf(message)] = message.copy(readed = true)
        return result.toMutableList()

    }

    //Получает максимальный идентификатор в коллекции, ограниченное использование
    fun <T> MutableList<T>.getMaxGuidInList(): Int {

        var currentMaxId = 0

        for (value in this) {
            val id: Int = when{
                (value is Chat) -> (value as Chat).id
                (value is Message) -> (value as Message).id
                (value is Int) -> value
                else -> 0}
            if (id >= currentMaxId)  currentMaxId = id
        }

        return  currentMaxId + 1

    }

}