package com.my.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class BookCodec implements MessageCodec<Book, Book> {

    @Override
    public void encodeToWire(Buffer buffer, Book book) {
        JsonObject json = new JsonObject();
        json.put("id", book.getId());
        json.put("name", book.getName());
        json.put("author", book.getAuthor());

        String jsonToStr = json.encode();
        int length = jsonToStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonToStr);
    }

    @Override
    public Book decodeFromWire(int pos, Buffer buffer) {
        int _pos = pos;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos += 4, _pos += length);
        JsonObject contentJson = new JsonObject(jsonStr);

        Book book = new Book(contentJson.getString("name"), contentJson.getString("author"));
        book.setId(contentJson.getLong("id"));
        return book;
    }

    @Override
    public Book transform(Book book) {
        return book;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
