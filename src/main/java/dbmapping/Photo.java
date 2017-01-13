package dbmapping;

import javax.persistence.*;

@Entity
@Table(name = "photo")
public class Photo {

    public Photo(long userid, byte[] photo_data){
        this.id = userid;
        this.data = photo_data;
    }

    public Photo(){

    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Id
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Lob
    @Column(name="photo")
    private byte[] data;

    @Transient
    @OneToOne(optional=false, mappedBy = "photo")
    private User user;

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Column(name="height")
    private long height;

    @Column(name="width")
    private long width;

    @Column(name="format")
    private String format;
}
