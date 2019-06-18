package com.android.zone;

import android.os.IBinder;
import android.os.RemoteException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.zone.manager.ComputeImpl;
import com.android.zone.manager.SecurityCenterImpl;

import java.util.concurrent.CountDownLatch;


/**
 * Created by cuiran
 * Time  16/9/18 14:36
 * Email cuiran2001@163.com
 * Description
 */
public class BinderPool {

    private static final String TAG="BinderPool";
    public static final int BINDER_NONE=-1;
    public static final int BINDER_COMPUTE=0;
    public static final int BINDER_SECRITY_CENTER=1;

    private Context mContext;

    private IBinderPool mBinderPool;
    private static volatile BinderPool sInstance;
    private CountDownLatch mCountDownLatch;

    private BinderPool(Context context){
        mContext=context;
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context){
        if(sInstance==null){
            synchronized (BinderPool.class){
                if(sInstance==null){
                    sInstance=new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService(){
        mCountDownLatch=new CountDownLatch(1);
        Intent service=new Intent(mContext,BinderPoolService.class);
        mContext.bindService(service,mBinderPoolConnection,Context.BIND_AUTO_CREATE);
        try{
            mCountDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode){
        IBinder binder=null;
        try{
            if(mBinderPool!=null){
                binder=mBinderPool.queryBinder(binderCode);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return binder;
    }

    private ServiceConnection mBinderPoolConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinderPool=IBinderPool.Stub.asInterface(iBinder);

            try{
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            }catch (RemoteException e){
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };





    private IBinder.DeathRecipient mBinderPoolDeathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG,"binder died");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool=null;
            connectBinderPoolService();

        }
    };

    public static class  BinderPoolImpl extends IBinderPool.Stub{
        public BinderPoolImpl(){
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder=null;
            switch (binderCode){
                case BINDER_SECRITY_CENTER:{
                    binder=new SecurityCenterImpl();

                    break;
                }
                case BINDER_COMPUTE:{
                    binder=new ComputeImpl();
                    break;
                }
                default:

                    break;
            }
            return binder;
        }

        @Override
        public void jian() throws RemoteException {
            int a = 101;
            int b = 223;
            int c =a-b;
        }
    }

}
