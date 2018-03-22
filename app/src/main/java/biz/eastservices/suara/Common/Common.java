package biz.eastservices.suara.Common;

import android.location.Location;

import biz.eastservices.suara.Model.Vendor;

/**
 * Created by reale on 3/17/2018.
 */

public class Common {
    public static final String USER_TABLE_VENDOR="Vendors";
    public static final String VENDOR_ONLINE="vendorsOnline";
    public static final String USER_RATING = "Rating";
    public static final int RESULT_CODE = 7777 ;
    public static final String VENDOR_LOCATION ="vendorLocation" ;


    public static boolean isDebug = true;

    public static Vendor currentVendor;

    public static final String USER_TABLE_CANDIDATE_LOCATION = "CandidateLocations";
    public static final int PICK_IMAGE_REQUEST = 8881;
    public static final int SIGN_IN_REQUEST_CODE = 8888;

    public static Location currentLocation;
    public static  String selected_uid_people="";


    public static String convertTypeToCategory(int type)
    {
        if (type ==0)
            return "Deliveries";
        else if (type ==1)
            return "Services";
        else if (type ==2)
            return "Transports";
        else if (type ==3)
            return "Sell";
        else if (type ==4)
            return "Rent";
        else
            return "null";
    }

    public static int convertCategoryToType(String type)
    {
        if (type.equals("Deliveries"))
            return 0;
        else if (type.equals("Services"))
            return 1;
        else if (type.equals("Transports"))
            return 2;
        else if (type.equals("Sell"))
            return 3;
        else if (type.equals("Rent"))
            return 4;
        else
            return -1;
    }

}
