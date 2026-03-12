package com.zava.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * JavaBean representing a user profile in the Zava Social community.
 * 
 * Maps to the UserProfile complex type defined in ZavaService.wsdl.
 * Field names match the SOAP response element names from the .NET backend.
 * 
 * NOTE: JoinDate uses java.util.Date (like ShoePost) even though Review
 * uses String for dates. This is because this class was written by the
 * same developer who wrote ShoePost (JK), not the one who wrote Review (TM).
 * The inconsistency is a natural result of two developers with different
 * approaches working on the same codebase without a style guide.
 * 
 * This class also has a transient List of ShoePosts for the user's posts,
 * which is NOT part of the WSDL type -- it gets populated separately by
 * calling GetUserPosts. This is the kind of thing that happens when you
 * need to combine data from two SOAP calls into one page.
 * 
 * @author jk
 * @version 1.1
 * @since 2007-04-20
 */
public class UserProfile implements Serializable {

    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 2L;

    // ============================================================
    // Fields -- match WSDL UserProfile complex type element names
    // ============================================================

    /** The unique user identifier */
    private int UserId;

    /** The user's login name */
    private String UserName;

    /** The user's display name (can be different from UserName) */
    private String DisplayName;

    /** The user's email address */
    private String Email;

    /** The user's bio / about me text */
    private String Bio;

    /**
     * The date the user joined Zava Social.
     * Uses java.util.Date -- same as ShoePost.PostDate.
     * See ShoePost javadoc for why we use Date instead of Calendar.
     */
    private Date JoinDate;

    /**
     * Whether this user account is active (1) or disabled (0).
     * Int instead of boolean -- inherited from .NET backend convention.
     */
    private int IsActive;

    // ============================================================
    // Non-WSDL fields -- populated by separate SOAP calls
    // ============================================================

    /**
     * The user's shoe posts -- populated by calling GetUserPosts separately.
     * NOT part of the WSDL UserProfile type. This field exists because
     * the profile page needs to show the user's posts, and it was easier
     * to stuff them into the bean than pass them separately through
     * request attributes. Is this good design? No. Does it work? Yes.
     */
    private transient List userPosts;

    /**
     * The user's reviews -- populated by iterating through shoe posts.
     * Also NOT part of the WSDL type. Added in version 1.1 when
     * the profile page got a "My Reviews" section.
     */
    private transient List userReviews;

    // ============================================================
    // Constructors
    // ============================================================

    /**
     * Default no-argument constructor.
     * Required by the JavaBeans specification.
     */
    public UserProfile() {
        this.userPosts = new ArrayList();
        this.userReviews = new ArrayList();
    }

    /**
     * Constructor with WSDL fields only.
     * Does not populate userPosts or userReviews -- those need
     * separate SOAP calls.
     *
     * @param UserId      the user identifier
     * @param UserName    the login username
     * @param DisplayName the display name
     * @param Email       the email address
     * @param Bio         the user's bio text
     * @param JoinDate    when the user joined
     * @param IsActive    1 if active, 0 if not
     */
    public UserProfile(int UserId, String UserName, String DisplayName,
                       String Email, String Bio, Date JoinDate,
                       int IsActive) {
        this.UserId = UserId;
        this.UserName = UserName;
        this.DisplayName = DisplayName;
        this.Email = Email;
        this.Bio = Bio;
        this.JoinDate = JoinDate;
        this.IsActive = IsActive;
        this.userPosts = new ArrayList();
        this.userReviews = new ArrayList();
    }

    // ============================================================
    // Getters and Setters -- WSDL fields
    // ============================================================

    /**
     * Gets the user identifier.
     * @return the UserId
     */
    public int getUserId() {
        return UserId;
    }

    /**
     * Sets the user identifier.
     * @param UserId the UserId to set
     */
    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    /**
     * Gets the login username.
     * @return the UserName
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * Sets the login username.
     * @param UserName the UserName to set
     */
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    /**
     * Gets the display name.
     * @return the DisplayName
     */
    public String getDisplayName() {
        return DisplayName;
    }

    /**
     * Sets the display name.
     * @param DisplayName the DisplayName to set
     */
    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    /**
     * Gets the email address.
     * @return the Email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * Sets the email address.
     * @param Email the Email to set
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }

    /**
     * Gets the user's bio text.
     * @return the Bio
     */
    public String getBio() {
        return Bio;
    }

    /**
     * Sets the user's bio text.
     * @param Bio the Bio to set
     */
    public void setBio(String Bio) {
        this.Bio = Bio;
    }

    /**
     * Gets the date the user joined.
     * @return the JoinDate as java.util.Date
     */
    public Date getJoinDate() {
        return JoinDate;
    }

    /**
     * Sets the date the user joined.
     * @param JoinDate the JoinDate to set
     */
    public void setJoinDate(Date JoinDate) {
        this.JoinDate = JoinDate;
    }

    /**
     * Gets whether the user account is active.
     * @return 1 if active, 0 if inactive
     */
    public int getIsActive() {
        return IsActive;
    }

    /**
     * Sets whether the user account is active.
     * @param IsActive 1 for active, 0 for inactive
     */
    public void setIsActive(int IsActive) {
        this.IsActive = IsActive;
    }

    // ============================================================
    // Getters and Setters -- Non-WSDL fields
    // ============================================================

    /**
     * Gets the user's shoe posts.
     * This list is populated separately from the WSDL response
     * by calling GetUserPosts and stuffing the results here.
     * 
     * @return list of ShoePost objects, or empty list if not populated
     */
    public List getUserPosts() {
        return userPosts;
    }

    /**
     * Sets the user's shoe posts.
     * @param userPosts the list of ShoePost objects
     */
    public void setUserPosts(List userPosts) {
        this.userPosts = userPosts;
    }

    /**
     * Gets the user's reviews.
     * @return list of Review objects, or empty list if not populated
     */
    public List getUserReviews() {
        return userReviews;
    }

    /**
     * Sets the user's reviews.
     * @param userReviews the list of Review objects
     */
    public void setUserReviews(List userReviews) {
        this.userReviews = userReviews;
    }

    // ============================================================
    // Utility Methods
    // ============================================================

    /**
     * Returns the number of shoe posts by this user.
     * Convenience method for the profile page JSP.
     * 
     * @return the count of user's shoe posts
     */
    public int getPostCount() {
        if (userPosts == null) {
            return 0;
        }
        return userPosts.size();
    }

    /**
     * Returns the number of reviews by this user.
     * Convenience method for the profile page JSP.
     * 
     * @return the count of user's reviews
     */
    public int getReviewCount() {
        if (userReviews == null) {
            return 0;
        }
        return userReviews.size();
    }

    // ============================================================
    // toString
    // ============================================================

    /**
     * Returns a string representation of this UserProfile.
     * 
     * Yet another toString format -- this one uses the "ClassName@hashCode"
     * pattern plus fields. JK saw this in a Java textbook once and
     * thought it was "the right way to do it." It is, technically,
     * but it doesn't match the other two beans in this package.
     */
    public String toString() {
        return "UserProfile@" + Integer.toHexString(hashCode())
            + "[UserId=" + UserId
            + ", UserName=" + UserName
            + ", DisplayName=" + DisplayName
            + ", Email=" + Email
            + ", IsActive=" + IsActive
            + ", JoinDate=" + (JoinDate != null ? JoinDate.toString() : "null")
            + ", posts=" + (userPosts != null ? userPosts.size() : 0)
            + ", reviews=" + (userReviews != null ? userReviews.size() : 0)
            + "]";
    }
}
