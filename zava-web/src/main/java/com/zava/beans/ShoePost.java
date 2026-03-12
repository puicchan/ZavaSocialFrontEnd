package com.zava.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * JavaBean representing a shoe post from the Zava Social community.
 * 
 * Maps to the ShoePost complex type defined in ZavaService.wsdl.
 * Field names match the SOAP response element names from the .NET backend.
 * 
 * NOTE: PostDate is stored as java.util.Date here, but the SOAP service
 * returns xsd:dateTime as a string. Parsing happens in the SOAP client
 * layer -- see SoapDateHelper for the format string. If dates show up
 * wrong, check the format mismatch between .NET and Java first. - JK 2008
 * 
 * @author shuri
 * @version 1.0
 * @since 2007-04-15
 */
public class ShoePost implements Serializable {

    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    // ============================================================
    // Fields -- match WSDL ShoePost complex type element names
    // ============================================================

    /** The unique identifier for this shoe post */
    private int PostId;

    /** The username of the person who posted this shoe */
    private String UserName;

    /** The brand name of the shoe (e.g. Nike, Adidas) */
    private String Brand;

    /** The model name of the shoe */
    private String Model;

    /** A description of the shoe written by the poster */
    private String Description;

    /** URL to the shoe image -- can be relative or absolute */
    private String ImageUrl;

    /**
     * The date this shoe was posted.
     * Uses java.util.Date because that's what SimpleDateFormat gives us
     * when we parse the SOAP response. Yes, we know about Calendar.
     * No, we're not changing it now.
     */
    private Date PostDate;

    /** Number of likes this post has received */
    private int LikeCount;

    /**
     * Whether this post is active (1) or deleted/hidden (0).
     * Stored as int because the .NET side uses int instead of boolean.
     * Don't ask why -- the original C# dev liked ints for everything.
     */
    private int IsActive;

    // ============================================================
    // Constructors
    // ============================================================

    /**
     * Default no-argument constructor.
     * Required by the JavaBeans specification.
     */
    public ShoePost() {
        // Default constructor -- fields will be set via setters
        // after SOAP response parsing
    }

    /**
     * Full constructor with all fields.
     * Used primarily in unit... err, manual testing.
     *
     * @param PostId      the post identifier
     * @param UserName    the poster's username
     * @param Brand       the shoe brand
     * @param Model       the shoe model name
     * @param Description the shoe description text
     * @param ImageUrl    the URL to the shoe image
     * @param PostDate    when the shoe was posted
     * @param LikeCount   number of likes
     * @param IsActive    1 if active, 0 if not
     */
    public ShoePost(int PostId, String UserName, String Brand, String Model,
                    String Description, String ImageUrl, Date PostDate,
                    int LikeCount, int IsActive) {
        this.PostId = PostId;
        this.UserName = UserName;
        this.Brand = Brand;
        this.Model = Model;
        this.Description = Description;
        this.ImageUrl = ImageUrl;
        this.PostDate = PostDate;
        this.LikeCount = LikeCount;
        this.IsActive = IsActive;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================

    /**
     * Gets the post identifier.
     * @return the PostId
     */
    public int getPostId() {
        return PostId;
    }

    /**
     * Sets the post identifier.
     * @param PostId the PostId to set
     */
    public void setPostId(int PostId) {
        this.PostId = PostId;
    }

    /**
     * Gets the username of the poster.
     * @return the UserName
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * Sets the username of the poster.
     * @param UserName the UserName to set
     */
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    /**
     * Gets the shoe brand name.
     * @return the Brand
     */
    public String getBrand() {
        return Brand;
    }

    /**
     * Sets the shoe brand name.
     * @param Brand the Brand to set
     */
    public void setBrand(String Brand) {
        this.Brand = Brand;
    }

    /**
     * Gets the shoe model name.
     * @return the Model
     */
    public String getModel() {
        return Model;
    }

    /**
     * Sets the shoe model name.
     * @param Model the Model to set
     */
    public void setModel(String Model) {
        this.Model = Model;
    }

    /**
     * Gets the shoe description text.
     * @return the Description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * Sets the shoe description text.
     * @param Description the Description to set
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * Gets the URL to the shoe image.
     * @return the ImageUrl
     */
    public String getImageUrl() {
        return ImageUrl;
    }

    /**
     * Sets the URL to the shoe image.
     * @param ImageUrl the ImageUrl to set
     */
    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    /**
     * Gets the date this shoe was posted.
     * @return the PostDate as java.util.Date
     */
    public Date getPostDate() {
        return PostDate;
    }

    /**
     * Sets the date this shoe was posted.
     * @param PostDate the PostDate to set
     */
    public void setPostDate(Date PostDate) {
        this.PostDate = PostDate;
    }

    /**
     * Gets the number of likes.
     * @return the LikeCount
     */
    public int getLikeCount() {
        return LikeCount;
    }

    /**
     * Sets the number of likes.
     * @param LikeCount the LikeCount to set
     */
    public void setLikeCount(int LikeCount) {
        this.LikeCount = LikeCount;
    }

    /**
     * Gets whether this post is active.
     * @return 1 if active, 0 if inactive
     */
    public int getIsActive() {
        return IsActive;
    }

    /**
     * Sets whether this post is active.
     * @param IsActive 1 for active, 0 for inactive
     */
    public void setIsActive(int IsActive) {
        this.IsActive = IsActive;
    }

    // ============================================================
    // toString
    // ============================================================

    /**
     * Returns a string representation of this ShoePost.
     * Useful for debug logging in the servlet layer.
     * 
     * NOTE: This format is different from Review.toString() and
     * UserProfile.toString() because different developers wrote them.
     * Don't "fix" this inconsistency -- it's realistic. - JK 2009
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ShoePost{");
        sb.append("PostId=").append(PostId);
        sb.append(", UserName='").append(UserName).append("'");
        sb.append(", Brand='").append(Brand).append("'");
        sb.append(", Model='").append(Model).append("'");
        sb.append(", LikeCount=").append(LikeCount);
        sb.append(", IsActive=").append(IsActive);
        if (PostDate != null) {
            sb.append(", PostDate=").append(PostDate.toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
