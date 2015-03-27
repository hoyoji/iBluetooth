package com.hoyoji.android.hyjframework.userdatabase;

public final class HyjUserDbContract {
	    // To prevent someone from accidentally instantiating the contract class,
	    // give it an empty constructor.
	    public HyjUserDbContract() {}

	    /* Inner class that defines the table contents */
	    public static abstract class UserDatabaseEntry {
	        public static final String TABLE_NAME = "UserDatabase";
	        public static final String COLUMN_NAME_ID = "id";
	        public static final String COLUMN_NAME_USERNAME = "userName";
	    }
}
