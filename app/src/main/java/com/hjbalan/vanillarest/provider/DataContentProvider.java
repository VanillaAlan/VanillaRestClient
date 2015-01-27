package com.hjbalan.vanillarest.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by alan on 14/12/28.
 */
public class DataContentProvider extends ContentProvider {

    //@formatter:off
    private static final String TAG = DataContentProvider.class.getSimpleName();

    private static final HashMap<String, String> sColmunsProjectionMap;

    private static final UriMatcher sUriMatcher;
    private static final int COLUMNS = 1;
    private static final int COLUMNS_ID = 2;

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "vanilla.db";

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Data.CONTENT_AUTHORITY, "nuts", COLUMNS);
        sUriMatcher.addURI(Data.CONTENT_AUTHORITY, "nuts/#", COLUMNS_ID);

        sColmunsProjectionMap = new HashMap<String, String>();
//

    }

    private DataOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DataOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Log.d(TAG, "query >>> " + selection + ", " + Arrays.toString(selectionArgs));

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;

        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                qb.setTables(Data.Column.TABLE_NAME);
                qb.setProjectionMap(sColmunsProjectionMap);
                orderBy = Data.Column.DEFAULT_SORT_ORDER;
                break;
            case COLUMNS_ID:
                qb.setTables(Data.Column.TABLE_NAME);
                qb.setProjectionMap(sColmunsProjectionMap);
                qb.appendWhere(Data.Column.ID + "=" + uri.getPathSegments().get(1));
                orderBy = Data.Column.DEFAULT_SORT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (!TextUtils.isEmpty(sortOrder)) {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                return Data.Column.CONTENT_TYPE;

            case COLUMNS_ID:
                return Data.Column.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert >>> " + values);

        // Validate ContentValues
        if (values == null) {
            throw new IllegalArgumentException("Content values can not be empty");
        }

        // Validate the requested uri and NOT NULL value
        String exceptionMsg = null;
        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                if (!values.containsKey(Data.Column.ID)) {
                    exceptionMsg = "tag_id in nut table can not be empty";
                }

                if (!values.containsKey(Data.Column.ID)) {
                    exceptionMsg = "device_id in nut table can not be empty";
                }
                break;

            default:
                exceptionMsg = "Unknown URI " + uri;
        }

        if (exceptionMsg != null) {
            throw new IllegalArgumentException(exceptionMsg);
        }

        String table = "";
        Uri contentUri = null;

        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                table = Data.Column.TABLE_NAME;
                contentUri = Data.Column.CONTENT_URI;
                break;

        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(table, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.d(TAG, "when bulkInsert, db is " + mOpenHelper.toString());
        String table = "";
        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                table = Data.Column.TABLE_NAME;
                break;

        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues contentValues : values) {
                db.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            return values.length;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        Log.d(TAG, "applyBatch >>> " + operations.size());

        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int i = 0;
        try {
            for (ContentProviderOperation operation : operations) {
                result[i++] = operation.apply(this, result, i);
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(operations.get(0).getUri(), null);
        } catch (OperationApplicationException e) {
            Log.e(TAG, "batch failed: " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete >>> " + selection + ", " + Arrays.toString(selectionArgs));

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String id;

        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                count = db.delete(Data.Column.TABLE_NAME, selection, selectionArgs);
                break;
            case COLUMNS_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(Data.Column.TABLE_NAME, Data.Column.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update >>> " + selection + ", " + Arrays.toString(selectionArgs) + ", " + values);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String id;
        switch (sUriMatcher.match(uri)) {
            case COLUMNS:
                count = db.update(Data.Column.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COLUMNS_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(Data.Column.TABLE_NAME, values,
                        Data.Column.ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DataOpenHelper extends SQLiteOpenHelper {

        private DataOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + Data.Column.TABLE_NAME + " ("
                            + Data.Column._ID 		        + " INTEGER PRIMARY KEY,"
                            + Data.Column.ID + " TEXT"
                            + " );"
            );

//            db.execSQL("CREATE INDEX idx_tag_id ON " + Data. Nut.TABLE_NAME + "(" + Data.Nut.UUID + ");" );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Data.Column.TABLE_NAME);
            onCreate(db);
        }
    }
}
