package it.jaschke.alexandria.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhishek on 19-Mar-16.
 */
public class Book implements Parcelable{

    private String title;
    private String subTitle;
    private String authors;
    private String image;
    private String category;
    private int init;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getInit() {
        return init;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public Book(){

    }

    private Book(Parcel parcel){
        title = parcel.readString();
        subTitle = parcel.readString();
        authors = parcel.readString();
        image = parcel.readString();
        category = parcel.readString();
        init = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeString(authors);
        parcel.writeString(image);
        parcel.writeString(category);
        parcel.writeInt(init);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>(){

        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int i) {
            return new Book[i];
        }
    };
}
