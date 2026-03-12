package com.zava.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zava.beans.ShoePost;
import com.zava.beans.UserProfile;
import com.zava.service.ZavaServiceClient;

/**
 * ProfileServlet -- Displays user profile pages.
 *
 * GET only -- profiles are read-only. The edit profile feature was
 * planned for "Phase 2" which never happened. There's a commented-out
 * doPost method at the bottom if you're curious about what could have been.
 *
 * URL: /profile           (shows logged-in user's profile)
 * URL: /profile?user=kickflipkid  (shows that user's profile)
 *
 * Makes TWO SOAP calls per page load:
 *   1. getUserProfile(userName) -- for profile info
 *   2. getUserPosts(userName) -- for their shoe posts
 *
 * We know this is inefficient. The .NET service doesn't have a
 * combined endpoint and nobody wants to add one. - JK 2007
 *
 * @author jk
 * @version 1.0
 * @since 2007-06-15
 */
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ZavaServiceClient serviceClient = new ZavaServiceClient();

    /**
     * Displays a user's profile page with their info and shoe posts.
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

        // Get the target user -- default to logged-in user
        String userName = request.getParameter("user");
        if (userName == null || userName.trim().length() == 0) {
            userName = currentUser;
        }

        // SOAP call #1: get profile info
        UserProfile profile = serviceClient.getUserProfile(userName);
        request.setAttribute("profile", profile);
        request.setAttribute("profileUser", userName);

        // SOAP call #2: get user's shoe posts
        List<ShoePost> userPosts = serviceClient.getUserPosts(userName);
        request.setAttribute("userPosts", userPosts);

        request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
    }

    // TODO: add edit profile form (phase 2 that never happened)
    // protected void doPost(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {
    //     // Was going to handle profile updates here
    //     // Had the form mockup and everything
    //     // Then management decided reviews were higher priority
    //     // and we never came back to this. - JK 2007
    // }
}
