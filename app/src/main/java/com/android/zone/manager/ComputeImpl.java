package com.android.zone.manager;

import android.os.RemoteException;

import com.android.zone.ICompute;

public class ComputeImpl extends ICompute.Stub {

    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
