package edu.mit.mitmobile2.tour.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MITRepresentation;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapItem;

public class MITTourStop extends MapItem {

    public class InfoWindowSnippet {
        public String type;
        public String title;
        public int index;

        public InfoWindowSnippet(String title, String type, int index) {
            this.title = title;
            this.type = type;
            this.index = index;
        }
    }

    @Expose
    private String title;
    @SerializedName("body_html")
    @Expose
    private String bodyHtml;
    @Expose
    private String id;
    @Expose
    private String type;

    @Expose
    private double[] coordinates;

    @SerializedName("images")
    @Expose
    private List<MITRepresentation> representations;

    @SerializedName("directions_to_next_stop")
    @Expose
    MITTourStopDirection direction;

    private int index;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public MITTourStopImage getImage() {
        return representations.get(0).getImages().get(0);
    }

    public MITTourStopImage getThumbnailImage() {
        return representations.get(0).getImages().get(1);
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public MITTourStopDirection getDirection() {
        return direction;
    }

    public void setDirection(MITTourStopDirection direction) {
        this.direction = direction;
    }

    public List<MITRepresentation> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITRepresentation> representations) {
        this.representations = representations;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getMapItemType() {
        return MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(coordinates[1], coordinates[0]));
        Gson gson = new Gson();
        String snippet = gson.toJson(new InfoWindowSnippet(this.title, this.type, this.index), InfoWindowSnippet.class);
        options.snippet(snippet);
        return options;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public int getIconResource() {
        return type.toLowerCase().equals("main loop") ? R.drawable.ic_pin_red : R.drawable.ic_pin_blue;
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

    @Override
    public void setMarkerText(String markerText) {
        super.setMarkerText(markerText);
        index = Integer.parseInt(markerText.trim()) - 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(bodyHtml);
        dest.writeString(id);
        dest.writeString(type);
        dest.writeInt(index);
        dest.writeDoubleArray(coordinates);
        dest.writeTypedList(representations);
        dest.writeParcelable(direction, 0);
    }

    private MITTourStop(Parcel p) {
        this.title = p.readString();
        this.bodyHtml = p.readString();
        this.id = p.readString();
        this.type = p.readString();
        this.index = p.readInt();
        this.coordinates = p.createDoubleArray();
        this.representations = p.createTypedArrayList(MITRepresentation.CREATOR);
        this.direction = p.readParcelable(MITTourStopDirection.class.getClassLoader());
    }

    public static final Parcelable.Creator<MITTourStop> CREATOR = new Parcelable.Creator<MITTourStop>() {
        public MITTourStop createFromParcel(Parcel source) {
            return new MITTourStop(source);
        }

        public MITTourStop[] newArray(int size) {
            return new MITTourStop[size];
        }
    };
}