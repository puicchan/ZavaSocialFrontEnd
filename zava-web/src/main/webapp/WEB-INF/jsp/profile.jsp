<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.List, com.zava.beans.ShoePost, com.zava.beans.UserProfile, com.zava.util.DateFormatter" %>
<%--
  profile.jsp -- User profile page for Zava Social.

  Shows user info (display name, bio, join date) and their shoe posts.
  TODO: add edit profile form (phase 2 that never happened)

  CSS classes: .pageTitle, .sectionTitle, .metaText, .profileInfo,
               .profileBio, .shoeCard, .dataTable, .dataHeader, .dataRowAlt
--%>

<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

<%
    UserProfile profile = (UserProfile) request.getAttribute("profile");
    List<ShoePost> userPosts = (List<ShoePost>) request.getAttribute("userPosts");
    String profileUser = (String) request.getAttribute("profileUser");
    DateFormatter dateFormatter = new DateFormatter();
%>

<% if (profile != null) { %>

            <h1 class="pageTitle"><%= profile.getUserName() != null ? profile.getUserName().toUpperCase() : profileUser.toUpperCase() %>'S PROFILE</h1>

            <div class="profileInfo">
                <b>Display Name:</b> <%= profile.getDisplayName() != null ? profile.getDisplayName() : profile.getUserName() %><br>
                <b>Member Since:</b> <span class="metaText"><%= profile.getJoinDate() != null ? dateFormatter.formatDate(profile.getJoinDate()) : "Unknown" %></span><br>
<% if (profile.getBio() != null && profile.getBio().length() > 0) { %>
                <br>
                <b>Bio:</b><br>
                <span class="profileBio"><%= profile.getBio() %></span>
<% } %>
            </div>

<% } else { %>

            <h1 class="pageTitle"><%= profileUser != null ? profileUser.toUpperCase() : "UNKNOWN" %>'S PROFILE</h1>
            <p>Profile not found.</p>

<% } %>

            <h2 class="sectionTitle">MY KICKS</h2>

<%
    if (userPosts != null && userPosts.size() > 0) {
%>
            <table class="dataTable" width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr class="dataHeader">
                    <th>Shoe</th>
                    <th>Brand</th>
                    <th>Likes</th>
                    <th>Date</th>
                </tr>
<%
        for (int i = 0; i < userPosts.size(); i++) {
            ShoePost p = (ShoePost) userPosts.get(i);
            String rowClass = (i % 2 == 1) ? " class=\"dataRowAlt\"" : "";
%>
                <tr<%= rowClass %>>
                    <td><b><a href="<%= request.getContextPath() %>/feed?action=view&id=<%= p.getPostId() %>"><%= p.getModel() %></a></b></td>
                    <td><%= p.getBrand() %></td>
                    <td align="center"><%= p.getLikeCount() %></td>
                    <td><span class="metaText"><%= p.getPostDate() != null ? dateFormatter.formatDate(p.getPostDate()) : "" %></span></td>
                </tr>
<%
        }
%>
            </table>
<%
    } else {
%>
            <p>No kicks posted yet.</p>
<%
    }
%>

            <%-- TODO: add edit profile form (phase 2 that never happened) --%>

<%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
