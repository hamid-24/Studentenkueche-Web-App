package de.hsflensburg.certainlyuncertainse2hausarbeit.filters;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class GlobalLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // don't cache the last request so that the user can't falsely sign in upon logging out
        // by returning the browser history
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);

        // redirect the request to the login page if the user is not signed in,
        // mainly for prevention of thymeleaf errors
        User user = (User) request.getSession().getAttribute("me");
        if (user == null) {
            response.sendRedirect("/login");
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
