// IBookManager.aidl
package com.android.zone;

// Declare any non-default types here with import statements
import com.android.zone.bean.Book;
interface IBookManager {
    void addBook(in Book book);
    List<Book> getBooks();
    }
