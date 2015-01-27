package com.hjbalan.vanillarest.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by alan on 14/12/26.
 */
public class Data {
    //@formatter:off
    private Data() {
    }

    /**
     * Content provider authority
     */
    public static final String CONTENT_AUTHORITY = "com.hjbalan.app.vanillarest.provider";

    /**
     * Base URI.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * nut info table
     */
    public static final class Column implements BaseColumns {

        private Column() {}

        /**
         * table name
         */
        public static final String TABLE_NAME = "column";

        /**
         * Fully qualified URI for "column" resources.
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME)
                .build();

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of nut.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.com.hjbalan.app.vanillarest.provider.column";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single nut.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.com.hjbalan.app.vanillarest.provider.column";

        /**
         * Column name for id
         * <p>Type: TEXT<p/>
         */
        public static final String ID = "id";


        public static final String DEFAULT_SORT_ORDER = " DESC";

    }

}
