package biz.eastservices.suara.Common;

import android.location.Location;

/**
 * Created by reale on 3/17/2018.
 */

public class Common {
    //Database define
    public static final String USER_TABLE_CANDIDATE = "Candidates";
    public static final String USER_TABLE_EMPLOYER = "Employers";
    public static final String USER_RATING = "Rating";



    public static final String USER_TABLE_CANDIDATE_LOCATION = "CandidateLocations";
    public static final int PICK_IMAGE_REQUEST = 8881;
    public static final int SIGN_IN_REQUEST_CODE = 8888;

    public static Location currentLocation;
    public static  String selected_uid_people="";


    public static String convertTypeToCategory(int type)
    {
        if (type ==0)
            return "Any";
        else if (type ==1)
            return "Services";
        else if (type ==2)
            return "Transport";
        else if (type ==3)
            return "Sell";
        else if (type ==4)
            return "Buy";
        else
            return "null";
    }

    public static int convertCategoryToType(String type)
    {
        if (type.equals("Any"))
            return 0;
        else if (type.equals("Services"))
            return 1;
        else if (type.equals("Transport"))
            return 2;
        else if (type.equals("Sell"))
            return 3;
        else if (type.equals("Buy"))
            return 4;
        else
            return -1;
    }

}
