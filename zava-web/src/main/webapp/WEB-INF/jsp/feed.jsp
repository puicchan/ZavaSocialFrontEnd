<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.List, com.zava.beans.ShoePost, com.zava.util.DateFormatter" %>
<%@ taglib uri="http://jakarta.ee/tags/core" prefix="c" %>
<%--
  feed.jsp -- Trending Kicks feed page for Zava Social.

  Displays a table of trending shoe posts from the backend.
  Uses a mix of scriptlets and JSTL tags because two different
  developers touched this file at different times. - JK 2008

  CSS classes: .pageTitle, .dataTable, .dataHeader, .dataRowAlt,
               .newBadge, .metaText
--%>

<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/includes/nav.jsp" %>

            <h1 class="pageTitle">TRENDING KICKS</h1>

<%
    List<ShoePost> posts = (List<ShoePost>) request.getAttribute("posts");
    DateFormatter dateFormatter = new DateFormatter();
    if (posts != null && posts.size() > 0) {
%>
            <table class="dataTable" width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr class="dataHeader">
                    <th>&nbsp;</th>
                    <th>Shoe</th>
                    <th>Brand</th>
                    <th>Posted By</th>
                    <th>Likes</th>
                    <th>Date</th>
                </tr>
<%
        for (int i = 0; i < posts.size(); i++) {
            ShoePost p = (ShoePost) posts.get(i);
            String rowClass = (i % 2 == 1) ? " class=\"dataRowAlt\"" : "";
%>
                <tr<%= rowClass %>>
                    <td>&nbsp;</td>
                    <td><b><a href="<%= request.getContextPath() %>/feed?action=view&id=<%= p.getPostId() %>"><%= p.getModel() %></a></b></td>
                    <td><%= p.getBrand() %></td>
                    <td><a href="<%= request.getContextPath() %>/profile?user=<%= p.getUserName() %>"><%= p.getUserName() %></a></td>
                    <td align="center"><%= p.getLikeCount() %></td>
                    <td><span class="metaText"><%= p.getPostDate() != null ? dateFormatter.formatDate(p.getPostDate()) : "" %></span></td>
                </tr>
<%
        }
%>
            </table>

            <%-- This section was added by a different developer who preferred JSTL - TM 2008 --%>
            <c:if test="${not empty posts}">
                <div class="pagination">
                    &laquo; Prev
                    <span class="navSeparator"> | </span>
                    <b>Page 1 of 1</b>
                    <span class="navSeparator"> | </span>
                    Next &raquo;
                </div>
            </c:if>

<%
    } else {
%>
            <p>No posts yet. Be the first to <a href="<%= request.getContextPath() %>/post">post your kicks</a>!</p>
<%
    }
%>

<%@ include file="/WEB-INF/jsp/includes/footer.jsp" %>
