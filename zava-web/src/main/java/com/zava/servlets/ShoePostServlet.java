package com.zava.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zava.messaging.*;
import com.zava.service.ZavaServiceClient;

/**
 * ShoePostServlet -- Handles creating new shoe posts.
 *
 * GET:  Shows the "Post Your Kicks" form.
 * POST: Submits the new shoe post to the backend via SOAP
 *       and redirects to the feed.
 *
 * URL: /post
 *
 * NOTE: This servlet handles the "new post" form only.
 * Viewing a post detail is handled by FeedServlet?action=view.
 * The web.xml comment says this does both but that's wrong
 * (TM wrote the comment before JK refactored it). - JK 2008
 *
 * @author tm
 * @version 1.0
 * @since 2007-05-01
 */
public class ShoePostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ZavaServiceClient serviceClient = new ZavaServiceClient();
    private ZavaMessagingClient messagingClient;

    @Override
    public void init() throws ServletException {
        ZavaMessagingConfig config = new ZavaMessagingConfig();
        messagingClient = new ZavaMessagingClient(config);
    }

    /**
     * Shows the new shoe post form.
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

        request.getRequestDispatcher("/WEB-INF/jsp/newPost.jsp").forward(request, response);
    }

    /**
     * Processes the new shoe post form submission.
     * Validates that brand and model are provided, then calls
     * the backend SOAP service to create the post.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authentication check - copied from LoginServlet
        // TODO: move this to a filter (2008)
        String currentUser = (String) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");

        // Minimal validation -- just check brand and model aren't empty
        if (brand == null || brand.trim().length() == 0 ||
            model == null || model.trim().length() == 0) {
            request.setAttribute("errorMsg", "Brand and Model are required.");
            request.getRequestDispatcher("/WEB-INF/jsp/newPost.jsp").forward(request, response);
            return;
        }

        // Send message to queue (fire-and-forget)
        String messageBody = currentUser + "|" + brand.trim() + "|" + model.trim() + "|"
                + (description != null ? description.trim() : "") + "|"
                + (imageUrl != null ? imageUrl.trim() : "");

        try {
            messagingClient.sendMessage("zava-messages", "AddShoePost", messageBody);
        } catch (ZavaMessagingException e) {
            request.setAttribute("errorMsg", "Failed to create post. Please try again.");
            request.getRequestDispatcher("/WEB-INF/jsp/newPost.jsp").forward(request, response);
            return;
        }

        // Success -- redirect to feed
        response.sendRedirect(request.getContextPath() + "/feed");
    }
}
