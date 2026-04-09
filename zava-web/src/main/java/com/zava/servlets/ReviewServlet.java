package com.zava.servlets;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.zava.beans.Review;
import com.zava.beans.ShoePost;
import com.zava.messaging.*;
import com.zava.service.ZavaServiceClient;

/**
 * ReviewServlet -- Handles shoe reviews display and submission.
 *
 * GET:  Shows reviews for a specific shoe post, plus the review form.
 * POST: Submits a new review to the backend via SOAP.
 *
 * URL: /reviews?shoeId=5
 *
 * Requires shoeId parameter -- if missing, redirects to feed.
 * This was a pain to debug because the WSDL uses "shoeId" but the
 * database column is "ShoePostId" and the C# property is also
 * "ShoePostId". We use "shoeId" to match the web method parameter. - TM 2008
 *
 * @author tm
 * @version 1.0
 * @since 2007-05-01
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/reviews"})
public class ReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ZavaServiceClient serviceClient = new ZavaServiceClient();
    private ZavaMessagingClient messagingClient;

    @Override
    public void init() throws ServletException {
        super.init();
        ZavaMessagingConfig config = new ZavaMessagingConfig();
        this.messagingClient = new ZavaMessagingClient(config);
    }

    /**
     * Displays reviews for a shoe post.
     * Also loads the shoe post itself for context (brand, model, etc).
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

        String shoeIdParam = request.getParameter("shoeId");
        if (shoeIdParam == null || shoeIdParam.trim().length() == 0) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        try {
            int shoeId = Integer.parseInt(shoeIdParam);

            // Get the shoe post for context
            ShoePost post = serviceClient.getShoePost(shoeId);
            request.setAttribute("post", post);

            // Get reviews for this shoe
            List<Review> reviews = serviceClient.getShoeReviews(shoeId);
            request.setAttribute("reviews", reviews);
            request.setAttribute("shoeId", Integer.valueOf(shoeId));

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/reviews.jsp").forward(request, response);
    }

    /**
     * Processes a new review submission.
     * Collects rating and review text, calls the backend SOAP service.
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

        String shoeIdParam = request.getParameter("shoeId");
        String ratingParam = request.getParameter("rating");
        String reviewText = request.getParameter("reviewText");

        if (shoeIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/feed");
            return;
        }

        int shoeId = Integer.parseInt(shoeIdParam);
        int rating = 3; // default
        try {
            rating = Integer.parseInt(ratingParam);
        } catch (Exception e) {
            // use default
        }

        // Submit the review via message queue (fire-and-forget)
        String messageBody = shoeId + "|" + currentUser + "|" + rating + "|"
                + (reviewText != null ? reviewText : "");
        try {
            messagingClient.sendMessage("zava-messages", "SubmitShoeReview", messageBody);
        } catch (ZavaMessagingException e) {
            request.setAttribute("errorMsg", "Failed to submit review: " + e.getMessage());
            ShoePost post = serviceClient.getShoePost(shoeId);
            request.setAttribute("post", post);
            List<Review> reviews = serviceClient.getShoeReviews(shoeId);
            request.setAttribute("reviews", reviews);
            request.setAttribute("shoeId", Integer.valueOf(shoeId));
            request.getRequestDispatcher("/WEB-INF/jsp/reviews.jsp").forward(request, response);
            return;
        }

        // Success -- redirect back to the reviews page
        response.sendRedirect(request.getContextPath() + "/reviews?shoeId=" + shoeId);
    }
}
