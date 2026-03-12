package com.zava.beans;

import java.io.Serializable;

/**
 * JavaBean representing a review of a shoe post in the Zava Social community.
 * 
 * Maps to the Review complex type defined in ZavaService.wsdl.
 * Field names match the SOAP response element names from the .NET service.
 * 
 * IMPORTANT: ReviewDate is stored as a String here (not java.util.Date),
 * because we got tired of fighting with SimpleDateFormat across the
 * SOAP boundary. The .NET service returns dates in "M/d/yyyy h:mm:ss tt"
 * format sometimes and "yyyy-MM-ddTHH:mm:ss" other times, depending on
 * which method you call. Storing as String and letting the JSP format it
 * was easier than fixing the real problem. TODO: fix the real problem.
 * 
 * NOTE: The WSDL says "ShoePostId" but the web method parameter is
 * called "shoeId" -- this naming inconsistency is inherited from the
 * .NET backend. We use ShoePostId here to match the SOAP response.
 * 
 * @author tm
 * @version 1.0
 * @since 2007-06-22
 */
public class Review implements Serializable {

    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 200706221L;

    // ============================================================
    // Fields -- match WSDL Review complex type element names
    // ============================================================

    /** Unique identifier for this review */
    private int ReviewId;

    /**
     * The ID of the shoe post being reviewed.
     * Column name in DB: ShoePostId
     * Web method param name: shoeId
     * Property name in C# class: ShoePostId
     * We use ShoePostId to match the SOAP response element name.
     */
    private int ShoePostId;

    /** Username of the person who wrote this review */
    private String ReviewerName;

    /** Rating from 1 to 5 -- stored as int, displayed as stars */
    private int Rating;

    /** The full text of the review */
    private String ReviewText;

    /**
     * The date this review was submitted.
     * 
     * Stored as String because of the cross-platform date format
     * mismatch between .NET and Java. See class javadoc for details.
     * 
     * When .NET sends "1/15/2007 3:45:00 PM" and Java expects
     * "2007-01-15T15:45:00", someone has to lose. It's us.
     */
    private String ReviewDate;

    // ============================================================
    // Constructors
    // ============================================================

    /**
     * Default no-argument constructor.
     * Required by the JavaBeans specification.
     */
    public Review() {
        // Default constructor -- populate via setters after SOAP parsing
    }

    /**
     * Convenience constructor with all fields.
     *
     * @param ReviewId     the unique review identifier
     * @param ShoePostId   the ID of the shoe post being reviewed
     * @param ReviewerName the reviewer's username
     * @param Rating       the rating (1-5)
     * @param ReviewText   the review text
     * @param ReviewDate   the review date as a String
     */
    public Review(int ReviewId, int ShoePostId, String ReviewerName,
                  int Rating, String ReviewText, String ReviewDate) {
        this.ReviewId = ReviewId;
        this.ShoePostId = ShoePostId;
        this.ReviewerName = ReviewerName;
        this.Rating = Rating;
        this.ReviewText = ReviewText;
        this.ReviewDate = ReviewDate;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================

    /**
     * Gets the review identifier.
     * @return the ReviewId
     */
    public int getReviewId() {
        return ReviewId;
    }

    /**
     * Sets the review identifier.
     * @param ReviewId the ReviewId to set
     */
    public void setReviewId(int ReviewId) {
        this.ReviewId = ReviewId;
    }

    /**
     * Gets the shoe post ID that this review is for.
     * @return the ShoePostId
     */
    public int getShoePostId() {
        return ShoePostId;
    }

    /**
     * Sets the shoe post ID that this review is for.
     * @param ShoePostId the ShoePostId to set
     */
    public void setShoePostId(int ShoePostId) {
        this.ShoePostId = ShoePostId;
    }

    /**
     * Gets the reviewer's username.
     * @return the ReviewerName
     */
    public String getReviewerName() {
        return ReviewerName;
    }

    /**
     * Sets the reviewer's username.
     * @param ReviewerName the ReviewerName to set
     */
    public void setReviewerName(String ReviewerName) {
        this.ReviewerName = ReviewerName;
    }

    /**
     * Gets the rating (1-5).
     * @return the Rating
     */
    public int getRating() {
        return Rating;
    }

    /**
     * Sets the rating.
     * Should be 1-5 but we don't validate here. The .NET service
     * is supposed to validate, but... it doesn't always.
     * @param Rating the Rating to set
     */
    public void setRating(int Rating) {
        this.Rating = Rating;
    }

    /**
     * Gets the review text.
     * @return the ReviewText
     */
    public String getReviewText() {
        return ReviewText;
    }

    /**
     * Sets the review text.
     * @param ReviewText the ReviewText to set
     */
    public void setReviewText(String ReviewText) {
        this.ReviewText = ReviewText;
    }

    /**
     * Gets the review date as a String.
     * Format depends on which .NET method returned it --
     * could be "M/d/yyyy" or "yyyy-MM-ddTHH:mm:ss".
     * @return the ReviewDate string
     */
    public String getReviewDate() {
        return ReviewDate;
    }

    /**
     * Sets the review date.
     * @param ReviewDate the ReviewDate string to set
     */
    public void setReviewDate(String ReviewDate) {
        this.ReviewDate = ReviewDate;
    }

    // ============================================================
    // Utility Methods
    // ============================================================

    /**
     * Returns the rating as a string of star characters for display.
     * Filled stars for the rating value, empty stars for the rest.
     * 
     * Example: Rating 3 returns three filled and two empty stars
     * 
     * @return star rating string
     */
    public String getStarRating() {
        StringBuffer stars = new StringBuffer();
        for (int i = 0; i < 5; i++) {
            if (i < Rating) {
                stars.append("\u2605"); // filled star
            } else {
                stars.append("\u2606"); // empty star
            }
        }
        return stars.toString();
    }

    // ============================================================
    // toString
    // ============================================================

    /**
     * Returns a debug-friendly string representation.
     * 
     * Uses a different format than ShoePost.toString() -- this one
     * uses square brackets and pipes. Why? Because TM wrote this class
     * and JK wrote ShoePost. Nobody enforced a standard.
     */
    public String toString() {
        return "[Review | id=" + ReviewId
            + " | shoePostId=" + ShoePostId
            + " | reviewer=" + ReviewerName
            + " | rating=" + Rating
            + " | date=" + ReviewDate + "]";
    }
}
