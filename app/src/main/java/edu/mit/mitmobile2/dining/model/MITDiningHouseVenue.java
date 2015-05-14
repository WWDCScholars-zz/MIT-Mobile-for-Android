package edu.mit.mitmobile2.dining.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.maps.MapItem;


public class MITDiningHouseVenue extends MapItem implements Parcelable {
    @SerializedName("icon_url")
    protected String iconURL;
    @SerializedName("id")
    protected String identifier;
    protected String name;
    protected Object payment; /* The ObjC Folks dont know what this is it seems */
    @SerializedName("short_name")
    protected String shortName;
    protected MITDiningLocation location;
    protected HashSet<MITDiningHouseDay> mealsByDay;
    protected MITDiningVenues venues;

    public String getIconURL() {
        return iconURL;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Object getPayment() {
        return payment;
    }

    public String getShortName() {
        return shortName;
    }

    public MITDiningLocation getLocation() {
        return location;
    }

    public HashSet<MITDiningHouseDay> getMealsByDay() {
        return mealsByDay;
    }

    public MITDiningVenues getVenues() {
        return venues;
    }

    @Override
    public String toString() {
        return "MITDiningHouseVenue{" +
                "iconURL='" + iconURL + '\'' +
                ", identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", payment=" + payment +
                ", shortName='" + shortName + '\'' +
                ", location=" + location +
                ", mealsByDay=" + mealsByDay +
                ", venues=" + venues +
                '}';
    }

    protected MITDiningHouseVenue(Parcel in) {
        iconURL = in.readString();
        identifier = in.readString();
        name = in.readString();
        payment = (Object) in.readValue(Object.class.getClassLoader());
        shortName = in.readString();
        location = (MITDiningLocation) in.readValue(MITDiningLocation.class.getClassLoader());
        mealsByDay = (HashSet) in.readValue(HashSet.class.getClassLoader());
        venues = (MITDiningVenues) in.readValue(MITDiningVenues.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iconURL);
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeValue(payment);
        dest.writeString(shortName);
        dest.writeValue(location);
        dest.writeValue(mealsByDay);
        dest.writeValue(venues);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningHouseVenue> CREATOR = new Parcelable.Creator<MITDiningHouseVenue>() {
        @Override
        public MITDiningHouseVenue createFromParcel(Parcel in) {
            return new MITDiningHouseVenue(in);
        }

        @Override
        public MITDiningHouseVenue[] newArray(int size) {
            return new MITDiningHouseVenue[size];
        }
    };

    @Override
    public int getMapItemType() {
        return MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        MarkerOptions options = new MarkerOptions();
        LatLng position = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()));
        options.position(position);
        options.snippet(this.toString());
        return options;
    }

    @Override
    public String getMarkerText() {
        return "";
    }

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {

    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }
}