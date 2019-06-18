package com.android.zone.manager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.zone.IBookManager;
import com.android.zone.bean.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {

    private static final String TAG = "BMS";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private Binder mBinder =new IBookManager.Stub() {
        @Override
        public void addBook(Book book) throws RemoteException {
                mBookList.add(book);
        }


        @Override
        public List<Book> getBooks() throws RemoteException {
            return mBookList;
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(11,"ios"));
        mBookList.add(new Book(11,"android"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
