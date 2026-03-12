<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="com.zava.beans.ShoePost, com.zava.util.DateFormatter" %>
<%--
  viewPost.jsp -- Single shoe post detail page for Zava Social.

  Shows a full shoe post with image, details, description,
  and a link to reviews.

  CSS classes: .pageTitle, .shoeCard, .shoeImage, .metaText
--%>

<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

<%
    ShoePost post = (ShoePost) request.getAttribute("post");
    DateFormatter dateFormatter = new DateFormatter();
%>

<% if (post != null) { %>

            <h1 class="pageTitle">SHOE DETAIL</h1>

            <div class="shoeCard">
                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <td width="310" valign="top">
<% if (post.getImageUrl() != null && post.getImageUrl().length() > 0) { %>
                            <img src="<%= post.getImageUrl() %>" alt="<%= post.getBrand() %> <%= post.getModel() %>" class="shoeImage" width="300">
<% } else { %>
                            <div style="width: 300px; height: 220px; background-color: #E0E0E0; border: 2px solid #000000; margin: 5px; text-align: center; line-height: 220px; font-family: Verdana, Geneva, sans-serif; font-size: 10px; color: #999999;">
                                [ No Image ]
                            </div>
<% } %>
                        </td>
                        <td valign="top" style="padding-left: 15px;">
                            <b style="font-size: 16px; font-family: Arial, Helvetica, sans-serif;"><%= post.getBrand() %> <%= post.getModel() %></b><br><br>

                            <b>Brand:</b> <%= post.getBrand() %><br>
                            <b>Model:</b> <%= post.getModel() %><br>
                            <b>Posted By:</b> <a href="<%= request.getContextPath() %>/profile?user=<%= post.getUserName() %>"><%= post.getUserName() %></a><br>
                            <b>Date:</b> <span class="metaText"><%= post.getPostDate() != null ? dateFormatter.formatDate(post.getPostDate()) : "" %></span><br>
                            <b>Likes:</b> <%= post.getLikeCount() %><br><br>

                            <b>Description:</b><br>
                            <%= post.getDescription() != null ? post.getDescription() : "" %>
                        </td>
                    </tr>
                </table>
            </div>

            <hr style="border: 0; border-top: 1px solid #CCCCCC; margin: 20px 0;">

            <p><b><a href="<%= request.getContextPath() %>/reviews?shoeId=<%= post.getPostId() %>" style="color: #CC0000;">View Reviews &raquo;</a></b></p>

<% } else { %>

            <h1 class="pageTitle">POST NOT FOUND</h1>
            <p>The shoe post you are looking for could not be found. <a href="<%= request.getContextPath() %>/feed">Return to feed</a>.</p>

<% } %>

<%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
