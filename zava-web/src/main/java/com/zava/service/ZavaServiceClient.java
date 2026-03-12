package com.zava.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zava.beans.Review;
import com.zava.beans.ShoePost;
import com.zava.beans.UserProfile;
import com.zava.util.DateFormatter;
import com.zava.util.XmlHelper;

/**
 * Hand-rolled SOAP client for the ZavaService .NET ASMX backend.
 *
 * Calls the backend via string-concatenated XML envelopes over HttpURLConnection.
 * No generated stubs, no JAX-WS -- just raw SOAP 1.1 the way it was done in 2007.
 *
 * NOTE: Do NOT use the WSDL-generated stub. It broke date parsing on the
 * feed page. - JK 2009
 *
 * @author jk
 * @version 1.3
 * @since 2007-05-01
 * @see com.zava.util.XmlHelper
 * @see com.zava.util.DateFormatter
 */
public class ZavaServiceClient {

    // Service URL - reads from ZAVA_SERVICE_URL env var if set, falls back to localhost
    private static final String SERVICE_URL = System.getenv("ZAVA_SERVICE_URL") != null
        ? System.getenv("ZAVA_SERVICE_URL")
        : "http://localhost:8081/ZavaService.asmx";

    private static final String SOAP_NS = "http://zava.example.com/services/2007";

    // date formatter instance -- NOT thread-safe, see DateFormatter javadoc
    private DateFormatter dateFormatter = new DateFormatter();

    /**
     * Returns the trending shoe posts from the backend service.
     * These are sorted by LikeCount descending on the .NET side.
     *
     * @return list of trending ShoePost objects, empty list on error
     */
    public List<ShoePost> getTrendingPosts() {
        List<ShoePost> posts = new ArrayList<ShoePost>();
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:zava=\"http://zava.example.com/services/2007\">"
                + "<soap:Body>"
                + "<zava:GetTrendingPosts />"
                + "</soap:Body></soap:Envelope>";

            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/GetTrendingPosts");
            Document doc = XmlHelper.parseDocument(response);

            List<Element> postElements = XmlHelper.getChildElements(doc.getDocumentElement(), "ShoePost");
            for (int i = 0; i < postElements.size(); i++) {
                Element el = (Element) postElements.get(i);
                ShoePost post = parseShoePost(el);
                posts.add(post);
            }
        } catch (Exception e) {
            // return empty list on failure
            System.err.println("SOAP fault: " + e.getMessage());
        }
        return posts;
    }

    /**
     * Returns the most recent shoe posts, limited by count.
     *
     * @param count how many recent posts to return
     * @return list of recent ShoePost objects, empty list on error
     */
    public List<ShoePost> getRecentPosts(int count) {
        List<ShoePost> posts = new ArrayList<ShoePost>();
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:zava=\"http://zava.example.com/services/2007\">"
                + "<soap:Body>"
                + "<zava:GetRecentPosts>"
                + "<zava:count>" + count + "</zava:count>"
                + "</zava:GetRecentPosts>"
                + "</soap:Body></soap:Envelope>";

            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/GetRecentPosts");
            Document doc = XmlHelper.parseDocument(response);

            List<Element> postElements = XmlHelper.getChildElements(doc.getDocumentElement(), "ShoePost");
            for (int i = 0; i < postElements.size(); i++) {
                Element el = (Element) postElements.get(i);
                posts.add(parseShoePost(el));
            }
        } catch (Exception e) {
            // return empty list
        }
        return posts;
    }

    /**
     * Gets a single shoe post by its ID.
     * BUG: doesn't handle null responses from the service
     *
     * @param postId the post identifier
     * @return the ShoePost, or null if not found
     */
    public ShoePost getShoePost(int postId) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:zava=\"http://zava.example.com/services/2007\">"
                + "<soap:Body>"
                + "<zava:GetShoePost>"
                + "<zava:postId>" + postId + "</zava:postId>"
                + "</zava:GetShoePost>"
                + "</soap:Body></soap:Envelope>";

            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/GetShoePost");
            Document doc = XmlHelper.parseDocument(response);

            // Added 2008 - had to add this after the .NET service changed response format
            Element resultEl = XmlHelper.getFirstChildElement(doc.getDocumentElement(), "GetShoePostResult");
            if (resultEl != null) {
                return parseShoePost(resultEl);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Adds a new shoe post. Returns the new PostId from the service,
     * or -1 if the post could not be created.
     *
     * @param userName    the poster's username
     * @param brand       the shoe brand
     * @param model       the shoe model name
     * @param description a description of the shoe
     * @param imageUrl    URL to the shoe image
     * @return the new PostId, or -1 on failure
     */
    public int addShoePost(String userName, String brand, String model, String description, String imageUrl) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:zava=\"http://zava.example.com/services/2007\">"
                + "<soap:Body>"
                + "<zava:AddShoePost>"
                + "<zava:userName>" + escapeXml(userName) + "</zava:userName>"
                + "<zava:brand>" + escapeXml(brand) + "</zava:brand>"
                + "<zava:model>" + escapeXml(model) + "</zava:model>"
                + "<zava:description>" + escapeXml(description) + "</zava:description>"
                + "<zava:imageUrl>" + escapeXml(imageUrl) + "</zava:imageUrl>"
                + "</zava:AddShoePost>"
                + "</soap:Body></soap:Envelope>";

            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/AddShoePost");
            Document doc = XmlHelper.parseDocument(response);

            String resultText = XmlHelper.getElementText(doc.getDocumentElement(), "AddShoePostResult");
            if (resultText.length() > 0) {
                return Integer.parseInt(resultText);
            }
        } catch (Exception e) {
            System.err.println("SOAP fault: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Gets reviews for a shoe post.
     *
     * Note: parameter is called shoeId (not postId) per the WSDL contract.
     * The .NET side maps this to ShoePostId in the database.
     *
     * @param shoeId the shoe post ID to get reviews for
     * @return list of Review objects, empty list on error
     */
    public List<Review> getShoeReviews(int shoeId) {
        List<Review> reviews = new ArrayList<Review>();
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:zava=\"http://zava.example.com/services/2007\">"
                + "<soap:Body>"
                + "<zava:GetShoeReviews>"
                + "<zava:shoeId>" + shoeId + "</zava:shoeId>"
                + "</zava:GetShoeReviews>"
                + "</soap:Body></soap:Envelope>";

            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/GetShoeReviews");
            Document doc = XmlHelper.parseDocument(response);

            List<Element> reviewElements = XmlHelper.getChildElements(doc.getDocumentElement(), "Review");
            for (int i = 0; i < reviewElements.size(); i++) {
                Element el = (Element) reviewElements.get(i);
                reviews.add(parseReview(el));
            }
        } catch (Exception e) {
            // return empty list on failure
        }
        return reviews;
    }

    /**
     * Submits a review for a shoe post.
     *
     * NOTE: userId parameter is actually a username string, not an integer ID.
     * Legacy naming from the original developer who copy-pasted from another method.
     *
     * @param shoeId     the shoe post to review
     * @param userId     the reviewer's username (yes, it's called userId but it's a name)
     * @param rating     rating from 1 to 5
     * @param reviewText the review text
     * @return "OK" on success, "ERROR: ..." on failure
     */
    public String submitShoeReview(int shoeId, String userId, int rating, String reviewText) {
        String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " xmlns:zava=\"http://zava.example.com/services/2007\">"
            + "<soap:Body>"
            + "<zava:SubmitShoeReview>"
            + "<zava:shoeId>" + shoeId + "</zava:shoeId>"
            + "<zava:userId>" + escapeXml(userId) + "</zava:userId>"
            + "<zava:rating>" + rating + "</zava:rating>"
            + "<zava:reviewText>" + escapeXml(reviewText) + "</zava:reviewText>"
            + "</zava:SubmitShoeReview>"
            + "</soap:Body></soap:Envelope>";

        try {
            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/SubmitShoeReview");
            Document doc = XmlHelper.parseDocument(response);

            return XmlHelper.getElementText(doc.getDocumentElement(), "SubmitShoeReviewResult");
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit review: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a user profile by username.
     *
     * @param userName the username to look up
     * @return the UserProfile, or null if not found or on error
     */
    public UserProfile getUserProfile(String userName) {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:zava=\"http://zava.example.com/services/2007\">"
                + "<soap:Body>"
                + "<zava:GetUserProfile>"
                + "<zava:userName>" + escapeXml(userName) + "</zava:userName>"
                + "</zava:GetUserProfile>"
                + "</soap:Body></soap:Envelope>";

            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/GetUserProfile");
            Document doc = XmlHelper.parseDocument(response);

            Element resultEl = XmlHelper.getFirstChildElement(doc.getDocumentElement(), "GetUserProfileResult");
            if (resultEl == null) {
                return null;
            }

            // inline parsing for UserProfile -- didn't bother making a helper method
            // since this is the only place we parse user profiles
            UserProfile profile = new UserProfile();
            profile.setUserId(XmlHelper.getIntValue(resultEl, "UserId", 0));
            profile.setUserName(XmlHelper.getElementText(resultEl, "UserName"));
            profile.setDisplayName(XmlHelper.getElementText(resultEl, "DisplayName"));
            profile.setEmail(XmlHelper.getElementText(resultEl, "Email"));
            profile.setBio(XmlHelper.getElementText(resultEl, "Bio"));

            // The .NET service returns dates in at least 3 different formats. Don't ask.
            String joinDateStr = XmlHelper.getElementText(resultEl, "JoinDate");
            if (joinDateStr.length() > 0) {
                Date joinDate = dateFormatter.parseServiceDate(joinDateStr);
                profile.setJoinDate(joinDate);
            }

            profile.setIsActive(XmlHelper.getIntValue(resultEl, "IsActive", 1));

            return profile;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets all shoe posts by a specific user.
     *
     * @param userName the username to get posts for
     * @return list of ShoePost objects, empty list on error
     */
    public List<ShoePost> getUserPosts(String userName) {
        List<ShoePost> posts = new ArrayList<ShoePost>();

        String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " xmlns:zava=\"http://zava.example.com/services/2007\">"
            + "<soap:Body>"
            + "<zava:GetUserPosts>"
            + "<zava:userName>" + escapeXml(userName) + "</zava:userName>"
            + "</zava:GetUserPosts>"
            + "</soap:Body></soap:Envelope>";

        try {
            InputStream response = sendSoapRequest(soapBody, "http://zava.example.com/services/2007/GetUserPosts");
            Document doc = XmlHelper.parseDocument(response);

            // inline parsing here -- different dev wrote this method, didn't know
            // about the parseShoePost helper
            List<Element> postElements = XmlHelper.getChildElements(doc.getDocumentElement(), "ShoePost");
            for (int i = 0; i < postElements.size(); i++) {
                Element el = (Element) postElements.get(i);
                ShoePost post = new ShoePost();
                post.setPostId(XmlHelper.getIntValue(el, "PostId", 0));
                post.setUserName(XmlHelper.getElementText(el, "UserName"));
                post.setBrand(XmlHelper.getElementText(el, "Brand"));
                post.setModel(XmlHelper.getElementText(el, "Model"));
                post.setDescription(XmlHelper.getElementText(el, "Description"));
                post.setImageUrl(XmlHelper.getElementText(el, "ImageUrl"));

                String dateStr = XmlHelper.getElementText(el, "PostDate");
                if (dateStr.length() > 0) {
                    post.setPostDate(dateFormatter.parseServiceDate(dateStr));
                }

                post.setLikeCount(XmlHelper.getIntValue(el, "LikeCount", 0));
                post.setIsActive(XmlHelper.getIntValue(el, "IsActive", 1));
                posts.add(post);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting user posts: " + e.getMessage(), e);
        }
        return posts;
    }

    // public List<ShoePost> searchPosts(String query) {
    //     // Feature was cut in sprint 4 - 2008
    //     // Was going to search by brand and model name
    //     // String soapBody = "<?xml version=\"1.0\" ...
    //     // Never finished implementing this on the .NET side either
    //     return new ArrayList<ShoePost>();
    // }

    // ================================================================
    // Private helpers
    // ================================================================

    /**
     * Parses a ShoePost element from the SOAP XML response.
     */
    private ShoePost parseShoePost(Element el) {
        ShoePost post = new ShoePost();
        post.setPostId(XmlHelper.getIntValue(el, "PostId", 0));
        post.setUserName(XmlHelper.getElementText(el, "UserName"));
        post.setBrand(XmlHelper.getElementText(el, "Brand"));
        post.setModel(XmlHelper.getElementText(el, "Model"));
        post.setDescription(XmlHelper.getElementText(el, "Description"));
        post.setImageUrl(XmlHelper.getElementText(el, "ImageUrl"));

        // The .NET service returns dates in at least 3 different formats. Don't ask.
        String dateStr = XmlHelper.getElementText(el, "PostDate");
        if (dateStr.length() > 0) {
            Date postDate = dateFormatter.parseServiceDate(dateStr);
            post.setPostDate(postDate);
        }

        post.setLikeCount(XmlHelper.getIntValue(el, "LikeCount", 0));
        post.setIsActive(XmlHelper.getIntValue(el, "IsActive", 1));
        return post;
    }

    /**
     * Parses a Review element from the SOAP XML response.
     * Added 2008 when we added the reviews feature.
     */
    private Review parseReview(Element el) {
        Review review = new Review();
        review.setReviewId(XmlHelper.getIntValue(el, "ReviewId", 0));
        review.setShoePostId(XmlHelper.getIntValue(el, "ShoePostId", 0));
        review.setReviewerName(XmlHelper.getElementText(el, "ReviewerName"));
        review.setRating(XmlHelper.getIntValue(el, "Rating", 0));
        review.setReviewText(XmlHelper.getElementText(el, "ReviewText"));
        // ReviewDate stored as String on the bean -- see Review.java javadoc
        review.setReviewDate(XmlHelper.getElementText(el, "ReviewDate"));
        return review;
    }

    /**
     * Sends a SOAP request to the backend service and returns the response stream.
     *
     * @param soapXml the complete SOAP envelope XML
     * @param soapAction the SOAPAction header value
     * @return the response input stream
     * @throws Exception if the HTTP request fails or returns non-200 status
     */
    private InputStream sendSoapRequest(String soapXml, String soapAction) throws Exception {
        URL url = new URL(SERVICE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"" + soapAction + "\"");
        conn.setDoOutput(true);

        // write the SOAP envelope
        OutputStream out = conn.getOutputStream();
        out.write(soapXml.getBytes("UTF-8"));
        out.close();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("SOAP request failed with HTTP status " + responseCode);
        }

        return conn.getInputStream();
    }

    /**
     * Basic XML escaping for string values going into SOAP envelopes.
     * Only handles the bare minimum -- &, <, >
     * Added after someone posted a shoe with "&" in the name and
     * it broke the entire feed page for 3 hours. - TM 2008
     */
    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
