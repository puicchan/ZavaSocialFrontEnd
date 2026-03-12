package com.zava.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet -- Handles user authentication for Zava Social.
 *
 * GET:  Shows the login form with a dropdown of usernames.
 * POST: Sets session attributes and redirects to the feed.
 *
 * Logout is handled via ?action=logout on this same servlet,
 * because TM didn't want to create a separate LogoutServlet
 * just to call session.invalidate(). Fair enough.
 *
 * NOTE: There is no real authentication here. We just check if the
 * username exists in our hardcoded list. Passwords are ignored.
 * This is a community site, not a bank. - JK 2007
 *
 * @author jk
 * @version 1.0
 * @since 2007-04-15
 */
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Hardcoded list of valid usernames from the seed data.
     * These match the Users table in the .NET backend database.
     * We hardcode them here because the backend doesn't have a
     * "list all users" endpoint and nobody wants to add one.
     */
    private static final String[] VALID_USERS = {
        "kickflipkid",
        "solequeen",
        "retrorunner",
        "freshout_thebox",
        "grailhunter",
        "lacedup_lou",
        "hi_top_hana"
    };

    /**
     * Shows the login form, or handles logout if action=logout.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // Handle logout
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Set the user list for the dropdown
        request.setAttribute("userList", VALID_USERS);

        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    /**
     * Processes the login form submission.
     * Sets session attributes and redirects to feed on success.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userName = request.getParameter("userName");

        // Minimal validation -- just check that a username was selected
        if (userName == null || userName.trim().length() == 0) {
            request.setAttribute("errorMsg", "Please select a username.");
            request.setAttribute("userList", VALID_USERS);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        // Check if username is in our valid list
        boolean valid = false;
        for (int i = 0; i < VALID_USERS.length; i++) {
            if (VALID_USERS[i].equals(userName)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            request.setAttribute("errorMsg", "Invalid username.");
            request.setAttribute("userList", VALID_USERS);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        // Set session attributes
        HttpSession session = request.getSession();
        session.setAttribute("currentUser", userName);
        session.setAttribute("isLoggedIn", "true");

        // Redirect to feed
        response.sendRedirect(request.getContextPath() + "/feed");
    }
}
