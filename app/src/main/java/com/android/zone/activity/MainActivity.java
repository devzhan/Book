package com.android.zone.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.zone.BinderPool;
import com.android.zone.IBookManager;
import com.android.zone.ICompute;
import com.android.zone.ISecurityCenter;
import com.android.zone.R;
import com.android.zone.bean.Book;
import com.android.zone.manager.BookManagerService;
import com.android.zone.manager.ComputeImpl;
import com.android.zone.manager.SecurityCenterImpl;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity";

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        Intent intent = new Intent(this,BookManagerService.class);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
        doWork();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                service.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {

                    }
                },1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try {
                List<Book> books = bookManager.getBooks();
                Log.i("TAG",books.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }


    ISecurityCenter securityCenter;
    ICompute mCompute;
    private void doWork(){
        Log.d(TAG,"doWork.......");
        BinderPool binderPool=BinderPool.getInstance(MainActivity.this);
        IBinder securityBinder=binderPool.queryBinder(BinderPool.BINDER_SECRITY_CENTER);
        securityCenter=(ISecurityCenter) SecurityCenterImpl.asInterface(securityBinder);

        Log.d(TAG,"访问ISecurityCenter");
        String msg="HelloWorld ,I'm cayden";
        Log.d(TAG,"content="+msg);
        try {
            String password=securityCenter.encrypt(msg);
            Log.d(TAG,"password="+password);
            Log.d(TAG,"decrypt="+securityCenter.decrypt(password));
        }catch (Exception e){
            e.printStackTrace();
        }


        Log.d(TAG,"访问ICompute");
        IBinder computeBinder=binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        mCompute=(ICompute) ComputeImpl.asInterface(computeBinder);
        try {
            Log.d(TAG,"3+5="+mCompute.add(3,5));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
