# WI-27: Feature-Level Functional Tests

**Tester:** Quake (QA/Testing Specialist)  
**Date:** 2026-02-10  
**Branch:** `squad/26-29-qa-validation`  
**Method:** Code-level validation (trace through source for each feature flow)

---

## FT-1: Login Flow

**Check:** LoginServlet calls Authenticate SOAP method, stores user in session, redirects.

| Step | Expected | Actual | File:Line | Status |
|------|----------|--------|-----------|--------|
| GET /login | Show login form with user dropdown | `doGet` forwards to `/WEB-INF/jsp/login.jsp` with `userList` attribute | `LoginServlet.java:52–71` | ✅ |
| User list | 7 hardcoded usernames | `VALID_USERS` array matches seed data users | `LoginServlet.java:39–47` | ✅ |
| POST /login | Validate username, set session, redirect | Checks username against `VALID_USERS`, sets `currentUser` and `isLoggedIn` on session, redirects to `/feed` | `LoginServlet.java:77–113` | ✅ |
| Invalid user | Show error on login form | Sets `errorMsg` attribute and re-forwards to login.jsp | `LoginServlet.java:99–103` | ✅ |
| Logout | Invalidate session | `?action=logout` calls `session.invalidate()` and redirects to `/login` | `LoginServlet.java:57–65` | ✅ |
| Session attrs | `currentUser`, `isLoggedIn` | Both set at `LoginServlet.java:108–109` | ✅ | ✅ |

**Note:** No SOAP "Authenticate" call — auth is a hardcoded username list check with no password verification. This is by design (see class javadoc: "There is no real authentication here").

**Result: ✅ PASS** (session-based auth with no password, as designed)

---

## FT-2: Feed / Trending Posts

**Check:** FeedServlet calls GetAllShoePosts, feed.jsp renders all posts with images.

| Step | Expected | Actual | File:Line | Status |
|------|----------|--------|-----------|--------|
| Auth check | Redirect unauthenticated users | Checks `currentUser` session attr, redirects to `/login` if null | `FeedServlet.java:47–51` | ✅ |
| SOAP call | Get trending posts | Calls `serviceClient.getTrendingPosts()` which sends `GetTrendingPosts` SOAP envelope | `FeedServlet.java:72` → `ZavaServiceClient.java:51–75` | ✅ |
| Forward | Pass posts to JSP | Sets `posts` attribute and forwards to `/WEB-INF/jsp/feed.jsp` | `FeedServlet.java:73–75` | ✅ |
| JSP rendering | Table with shoe data | Scriptlet iterates `posts`, renders brand, model, userName, likeCount, date in `dataTable` | `feed.jsp:20–49` | ✅ |
| Post links | Link to detail view | Each model name links to `/feed?action=view&id=<PostId>` | `feed.jsp:41` | ✅ |
| Profile links | Link to poster's profile | Each userName links to `/profile?user=<userName>` | `feed.jsp:43` | ✅ |
| Alternating rows | CSS classes for readability | Odd rows get `dataRowAlt` class | `feed.jsp:37` | ✅ |

**Note:** FeedServlet calls `getTrendingPosts()` (not `getAllShoePosts`) — trending posts are sorted by LikeCount DESC, limited to top 10 on the .NET side.

**Result: ✅ PASS**

---

## FT-3: Post Detail (View Post)

**Check:** viewPost.jsp retrieves single post + reviews link.

| Step | Expected | Actual | File:Line | Status |
|------|----------|--------|-----------|--------|
| Action dispatch | `?action=view&id=N` routes to detail | FeedServlet checks `action == "view"`, parses `id` param | `FeedServlet.java:55–68` | ✅ |
| SOAP call | Get single post | Calls `serviceClient.getShoePost(postId)` → `GetShoePost` SOAP envelope | `FeedServlet.java:61` → `ZavaServiceClient.java:116–139` | ✅ |
| Forward | Pass post to viewPost.jsp | Sets `post` attribute, forwards to `/WEB-INF/jsp/viewPost.jsp` | `FeedServlet.java:62–63` | ✅ |
| JSP rendering | Image + details | Shows image via `post.getImageUrl()`, brand, model, userName, date, likes, description | `viewPost.jsp:24–49` | ✅ |
| Image display | Shoe image with fallback | Checks `ImageUrl` not null/empty; shows `[ No Image ]` placeholder otherwise | `viewPost.jsp:28–34` | ✅ |
| Reviews link | Link to reviews page | Links to `/reviews?shoeId=<PostId>` | `viewPost.jsp:54` | ✅ |
| Not found | Error message | Shows "POST NOT FOUND" with link back to feed | `viewPost.jsp:57–59` | ✅ |

**Result: ✅ PASS**

---

## FT-4: New Post

**Check:** ShoePostServlet calls CreateShoePost with form data.

| Step | Expected | Actual | File:Line | Status |
|------|----------|--------|-----------|--------|
| Auth check | Redirect if not logged in | Checks `currentUser` session, redirects to `/login` | `ShoePostServlet.java:44–48` | ✅ |
| GET /post | Show new post form | Forwards to `/WEB-INF/jsp/newPost.jsp` | `ShoePostServlet.java:50` | ✅ |
| Form fields | brand, model, description, imageUrl | All present in newPost.jsp with `formTable` layout | `newPost.jsp:22–46` | ✅ |
| Required fields | Brand and Model required | JSP shows `*` indicators; servlet validates non-empty | `newPost.jsp:24–29`, `ShoePostServlet.java:75–80` | ✅ |
| POST /post | Submit via SOAP | Calls `serviceClient.addShoePost(currentUser, brand, model, description, imageUrl)` | `ShoePostServlet.java:83–85` | ✅ |
| SOAP envelope | AddShoePost with params | Builds XML with `userName`, `brand`, `model`, `description`, `imageUrl` elements | `ZavaServiceClient.java:152–178` | ✅ |
| Success redirect | Back to feed | Redirects to `/feed` on success (newPostId != -1) | `ShoePostServlet.java:94` | ✅ |
| Failure handling | Show error on form | Sets `errorMsg` and re-forwards to newPost.jsp | `ShoePostServlet.java:87–91` | ✅ |

**Result: ✅ PASS**

---

## FT-5: Reviews

**Check:** ReviewServlet calls GetReviewsForPost AND AddReview.

| Step | Expected | Actual | File:Line | Status |
|------|----------|--------|-----------|--------|
| Auth check | Redirect if not logged in | Checks `currentUser` session in both `doGet` and `doPost` | `ReviewServlet.java:47–51`, `:88–92` | ✅ |
| Missing shoeId | Redirect to feed | If `shoeId` param null/empty, redirects to `/feed` | `ReviewServlet.java:53–57` | ✅ |
| GET reviews | Load post + reviews | Calls `getShoePost(shoeId)` AND `getShoeReviews(shoeId)` | `ReviewServlet.java:63–68` | ✅ |
| SOAP: GetShoeReviews | Correct envelope | Sends `GetShoeReviews` with `shoeId` param | `ZavaServiceClient.java:189–213` | ✅ |
| JSP rendering | Reviews with stars | Builds star string (★☆), shows rating N/5, reviewer link, date, text | `reviews.jsp:46–66` | ✅ |
| Review form | Rating dropdown + text | Select with values 5→1 using star chars, textarea for review | `reviews.jsp:79–106` | ✅ |
| POST review | Submit via SOAP | Calls `submitShoeReview(shoeId, currentUser, rating, reviewText)` | `ReviewServlet.java:112–113` | ✅ |
| SOAP: SubmitShoeReview | Correct envelope | Sends `SubmitShoeReview` with `shoeId`, `userId`, `rating`, `reviewText` | `ZavaServiceClient.java:227–248` | ✅ |
| Error handling | Show error message | Checks result starts with "ERROR:", reloads page with error | `ReviewServlet.java:116–126` | ✅ |
| Success | Redirect back | Redirects to `/reviews?shoeId=N` | `ReviewServlet.java:129` | ✅ |

**Result: ✅ PASS**

---

## FT-6: Profile

**Check:** ProfileServlet calls GetUserProfile AND GetAllShoePosts (filtered).

| Step | Expected | Actual | File:Line | Status |
|------|----------|--------|-----------|--------|
| Auth check | Redirect if not logged in | Checks `currentUser` session | `ProfileServlet.java:50–54` | ✅ |
| Default user | Show logged-in user's profile | If `user` param null/empty, uses `currentUser` | `ProfileServlet.java:57–59` | ✅ |
| Other user | Show specified user's profile | Takes `user` query param | `ProfileServlet.java:57` | ✅ |
| SOAP call #1 | GetUserProfile | Calls `serviceClient.getUserProfile(userName)` → `GetUserProfile` envelope | `ProfileServlet.java:62` → `ZavaServiceClient.java:256–297` | ✅ |
| SOAP call #2 | GetUserPosts | Calls `serviceClient.getUserPosts(userName)` → `GetUserPosts` envelope | `ProfileServlet.java:68` → `ZavaServiceClient.java:305–347` | ✅ |
| Forward | Pass data to JSP | Sets `profile`, `profileUser`, `userPosts` attributes | `ProfileServlet.java:63–69` | ✅ |
| JSP: profile info | Display name, join date, bio | Shows DisplayName, JoinDate, Bio in `profileInfo` div | `profile.jsp:27–34` | ✅ |
| JSP: user posts | Table of user's kicks | Iterates `userPosts`, renders in `dataTable` | `profile.jsp:49–70` | ✅ |
| Not found | Profile not found message | Shows "Profile not found." if profile is null | `profile.jsp:39–41` | ✅ |

**Result: ✅ PASS**

---

## FT-7: Shoe Images

**Check:** All 18 shoe images exist at `zava-web/src/main/webapp/images/shoes/shoe_{1-18}.jpg`.

| Image | Exists | Referenced In Seed Data |
|-------|--------|------------------------|
| shoe_1.jpg | ✅ | PostId 1 — `/images/shoes/shoe_1.jpg` |
| shoe_2.jpg | ✅ | PostId 2 — `/images/shoes/shoe_2.jpg` |
| shoe_3.jpg | ✅ | PostId 3 — `/images/shoes/shoe_3.jpg` |
| shoe_4.jpg | ✅ | PostId 4 — `/images/shoes/shoe_4.jpg` |
| shoe_5.jpg | ✅ | PostId 5 — `/images/shoes/shoe_5.jpg` |
| shoe_6.jpg | ✅ | PostId 6 — `/images/shoes/shoe_6.jpg` |
| shoe_7.jpg | ✅ | PostId 7 — `/images/shoes/shoe_7.jpg` |
| shoe_8.jpg | ✅ | PostId 8 — `/images/shoes/shoe_8.jpg` |
| shoe_9.jpg | ✅ | PostId 9 — `/images/shoes/shoe_9.jpg` |
| shoe_10.jpg | ✅ | PostId 10 — `/images/shoes/shoe_10.jpg` |
| shoe_11.jpg | ✅ | PostId 11 — `/images/shoes/shoe_11.jpg` |
| shoe_12.jpg | ✅ | PostId 12 — `/images/shoes/shoe_12.jpg` |
| shoe_13.jpg | ✅ | PostId 13 — `/images/shoes/shoe_13.jpg` |
| shoe_14.jpg | ✅ | PostId 14 — `/images/shoes/shoe_14.jpg` |
| shoe_15.jpg | ✅ | PostId 15 — `/images/shoes/shoe_15.jpg` |
| shoe_16.jpg | ✅ | PostId 16 — `/images/shoes/shoe_16.jpg` |
| shoe_17.jpg | ✅ | PostId 17 — `/images/shoes/shoe_17.jpg` |
| shoe_18.jpg | ✅ | PostId 18 — `/images/shoes/shoe_18.jpg` |

All 18 images present. Directory also contains `.gitkeep`.

**Result: ✅ PASS**

---

## FT-8: CSS Class Usage

**Check:** Each JSP uses correct CSS classes from the design spec.

| JSP | CSS Classes Used | Matches Spec |
|-----|-----------------|--------------|
| header.jsp | `.container`, `.header`, `.headerTitle`, `.headerTagline` | ✅ |
| nav.jsp | `.nav`, `.navLink`, `.navActive`, `.navSeparator`, `.navUser` | ✅ |
| footer.jsp | `.footer`, `.footerLink`, `.navSeparator`, `.browserNote` | ✅ |
| feed.jsp | `.pageTitle`, `.dataTable`, `.dataHeader`, `.dataRowAlt`, `.metaText`, `.pagination`, `.navSeparator` | ✅ |
| login.jsp | `.pageTitle`, `.loginForm`, `.formLabel`, `.formInput`, `.formButton`, `.errorMsg` | ✅ |
| newPost.jsp | `.pageTitle`, `.formTable`, `.formLabel`, `.formInput`, `.formButton`, `.required`, `.errorMsg`, `.metaText` | ✅ |
| viewPost.jsp | `.pageTitle`, `.shoeCard`, `.shoeImage`, `.metaText` | ✅ |
| reviews.jsp | `.pageTitle`, `.sectionTitle`, `.shoeCard`, `.reviewBox`, `.reviewStars`, `.reviewMeta`, `.reviewText`, `.formTable`, `.formLabel`, `.formInput`, `.formButton`, `.required`, `.errorMsg` | ✅ |
| profile.jsp | `.pageTitle`, `.sectionTitle`, `.metaText`, `.profileInfo`, `.profileBio`, `.dataTable`, `.dataHeader`, `.dataRowAlt` | ✅ |

All CSS classes in `zava.css` are referenced by at least one JSP. All JSP class references resolve to definitions in `zava.css`.

**Result: ✅ PASS**

---

## Summary

| Test | Feature | Result |
|------|---------|--------|
| FT-1 | Login | ✅ PASS |
| FT-2 | Feed | ✅ PASS |
| FT-3 | Post Detail | ✅ PASS |
| FT-4 | New Post | ✅ PASS |
| FT-5 | Reviews | ✅ PASS |
| FT-6 | Profile | ✅ PASS |
| FT-7 | Shoe Images | ✅ PASS |
| FT-8 | CSS Classes | ✅ PASS |

**Overall Feature Test Result: ✅ ALL PASS (8/8)**
