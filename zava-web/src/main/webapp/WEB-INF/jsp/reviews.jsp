<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.List, com.zava.beans.Review, com.zava.beans.ShoePost" %>
<%--
  reviews.jsp -- Reviews page for Zava Social.

  Shows shoe info at top, existing reviews, and a form to submit new ones.
  Rating is displayed as a number like "4/5" because we couldn't get
  the star characters to render consistently in IE6.

  Wait, actually TM got stars working later. But this page still uses
  the number format because nobody updated it. Classic.

  CSS classes: .pageTitle, .sectionTitle, .shoeCard, .reviewBox,
               .reviewStars, .reviewMeta, .formTable, .formLabel,
               .formInput, .formButton, .required, .errorMsg
--%>

<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

<%
    ShoePost post = (ShoePost) request.getAttribute("post");
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    Integer shoeId = (Integer) request.getAttribute("shoeId");
%>

            <h1 class="pageTitle">REVIEWS</h1>

<% if (post != null) { %>
            <h2 class="sectionTitle"><%= post.getBrand() %> <%= post.getModel() %></h2>

            <div class="shoeCard">
                <b>Brand:</b> <%= post.getBrand() %><br>
                <b>Model:</b> <%= post.getModel() %><br>
                <b>Posted By:</b> <a href="<%= request.getContextPath() %>/profile?user=<%= post.getUserName() %>"><%= post.getUserName() %></a><br>
                <a href="<%= request.getContextPath() %>/feed?action=view&id=<%= post.getPostId() %>">&laquo; Back to shoe detail</a>
            </div>
<% } %>

<% if (request.getAttribute("errorMsg") != null) { %>
            <div class="errorMsg"><%= request.getAttribute("errorMsg") %></div>
<% } %>

            <hr style="border: 0; border-top: 1px solid #CCCCCC; margin: 20px 0;">

<%
    if (reviews != null && reviews.size() > 0) {
        for (int i = 0; i < reviews.size(); i++) {
            Review r = (Review) reviews.get(i);
            // Build star string
            StringBuffer stars = new StringBuffer();
            for (int s = 0; s < r.getRating(); s++) {
                stars.append("\u2605");
            }
            for (int s = r.getRating(); s < 5; s++) {
                stars.append("\u2606");
            }
%>
            <div class="reviewBox">
                <span class="reviewStars"><%= stars.toString() %></span>
                <b> <%= r.getRating() %>/5</b><br>
                <b>by <a href="<%= request.getContextPath() %>/profile?user=<%= r.getReviewerName() %>"><%= r.getReviewerName() %></a></b>
                <span class="reviewMeta" style="float: right;"><%= r.getReviewDate() %></span><br>
                <span class="reviewText"><%= r.getReviewText() %></span>
            </div>
<%
        }
    } else {
%>
            <p>No reviews yet. Be the first to review this shoe!</p>
<%
    }
%>

            <hr style="border: 0; border-top: 1px solid #CCCCCC; margin: 20px 0;">

            <h2 class="sectionTitle">WRITE A REVIEW</h2>

            <form method="post" action="<%= request.getContextPath() %>/reviews">
            <input type="hidden" name="shoeId" value="<%= shoeId != null ? shoeId.intValue() : 0 %>">
            <table class="formTable" width="500" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td class="formLabel" width="100">Rating: <span class="required">*</span></td>
                    <td class="formInput">
                        <select name="rating">
                            <option value="">-- Select --</option>
                            <option value="5">&#9733;&#9733;&#9733;&#9733;&#9733; (5)</option>
                            <option value="4">&#9733;&#9733;&#9733;&#9733;&#9734; (4)</option>
                            <option value="3">&#9733;&#9733;&#9733;&#9734;&#9734; (3)</option>
                            <option value="2">&#9733;&#9733;&#9734;&#9734;&#9734; (2)</option>
                            <option value="1">&#9733;&#9734;&#9734;&#9734;&#9734; (1)</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="formLabel">Review: <span class="required">*</span></td>
                    <td class="formInput"><textarea name="reviewText" rows="6" cols="40" style="width: 350px; height: 100px;"></textarea></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td class="formInput" style="padding-top: 10px;">
                        <input type="submit" value="SUBMIT REVIEW" class="formButton">
                    </td>
                </tr>
            </table>
            </form>

<%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
