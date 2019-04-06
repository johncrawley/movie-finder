package com.jacsstuff.joesfilmfinder.db;

import android.provider.BaseColumns;

public class DbContract {

    private DbContract(){}

    static class ProfilesEntry implements BaseColumns{

        static final String TABLE_NAME = "profiles";
        static final String COL_URL = "url";
        static final String COL_IMAGE = "image";
        static final String COL_NAME = "name";
        static final String COL_DATE_CREATED = "date_created";
    }

    static class ResultLinksEntry implements BaseColumns{

        static final String TABLE_NAME = "result_links";
        static final String COL_PROFILE_ID = "profile_id";
        static final String COL_URL = "url";
        static final String COL_NAME = "name";
        static final String COL_YEAR = "year";
        static final String COL_ROLES = "roles";
        static final String COL_CHARACTER_NAME = "character_name";
        static final String COL_CHARACTER_URL = "character_url";
    }



    static class SearchResultEntry implements BaseColumns{

        static final String TABLE_NAME = "search_results";
        static final String COL_SEARCH_TERM = "search_term";
        static final String COL_NAME = "name";
        static final String COL_URL = "url";
        static final String COL_IMAGE = "image";
        static final String COL_DATE_CREATED = "date_created";

    }
}
