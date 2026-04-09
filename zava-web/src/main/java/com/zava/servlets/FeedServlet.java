package com.zava.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.zava.beans.ShoePost;
import com.zava.service.ZavaServiceClient;

/**
 * FeedServlet -- The main landing page for Zava Social.
 *
 * Displays trending shoe posts from the community, pulled from
 * the .NET backend via SOAP. Also handles viewing individual
 * shoe post details via the action=view parameter.
 *
 * URL: /feed
 * URL: /feed?action=view&id=5
 *
 * TODO: Add pagination. Right now we just dump all trending posts
 * on the page. The postsPerPage context-param is there but nobody
 * ever wired it up. - JK 2008
 *
 * @author jk
 * @version 1.1
 * @since 2007-04-15
 */
@WebServlet(name = "FeedServlet", urlPatterns = {"/feed"}, loadOnStartup = 1)
public class FeedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // TODO: move this to a filter (2008)
    private ZavaServiceClient serviceClient = new ZavaServiceClient();

    /**
     * Handles feed display and individual post viewing via action dispatch.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authentication check - copied from LoginServlet
        // TODO: move this to a filter (2008)
        String currentUser = (String) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("view".equals(action)) {
            // View a single shoe post detail
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int postId = Integer.parseInt(idParam);
                    ShoePost post = serviceClient.getShoePost(postId);
                    request.setAttribute("post", post);
                    request.getRequestDispatcher("/WEB-INF/jsp/viewPost.jsp").forward(request, response);
                    return;
                } catch (NumberFormatException e) {
                    // fall through to feed
                }
            }
        }

        // Default: show trending feed
        List<ShoePost> posts = serviceClient.getTrendingPosts();
        request.setAttribute("posts", posts);

        request.getRequestDispatcher("/WEB-INF/jsp/feed.jsp").forward(request, response);
    }
}
