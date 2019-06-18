// IBinderPool.aidl
package com.android.zone;

// Declare any non-default types here with import statements

interface IBinderPool {
       IBinder queryBinder(int binderCode);
       void jian();

}
