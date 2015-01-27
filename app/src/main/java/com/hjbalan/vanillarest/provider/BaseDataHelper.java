package com.hjbalan.vanillarest.provider;

import com.hjbalan.vanillarest.ApplicationController;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;

/**
 * Created by alan on 14/12/28.
 */
public abstract class BaseDataHelper {

    private Context mContext;

    public BaseDataHelper() {
        mContext = ApplicationController.getInstance().getApplicationContext();
    }

    public Context getContext() {
        return mContext;
    }

    protected abstract Uri getContentUri();

    public void notifyChange() {
        mContext.getContentResolver().notifyChange(getContentUri(), null);
    }

    protected final Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return mContext.getContentResolver().query(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    protected final Cursor query(String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        return mContext.getContentResolver().query(getContentUri(), projection, selection,
                selectionArgs, sortOrder);
    }

    protected final Uri insert(ContentValues values) {
        return mContext.getContentResolver().insert(getContentUri(), values);
    }

    protected final int bulkInsert(ContentValues[] values) {
        return mContext.getContentResolver().bulkInsert(getContentUri(), values);
    }

    protected final int update(ContentValues values, String where, String[] whereArgs) {
        return mContext.getContentResolver().update(getContentUri(), values, where, whereArgs);
    }

    protected final int delete(String selection, String[] selectionArgs) {
        return mContext.getContentResolver().delete(getContentUri(), selection, selectionArgs);
    }

    protected final Cursor getList(String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        return mContext.getContentResolver().query(getContentUri(), projection, selection,
                selectionArgs, sortOrder);
    }

    protected final void applyBatch(ArrayList<ContentProviderOperation> operations) {
        try {
            mContext.getContentResolver().applyBatch(Data.CONTENT_AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

}