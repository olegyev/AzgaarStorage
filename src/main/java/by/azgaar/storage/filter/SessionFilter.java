package by.azgaar.storage.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import by.azgaar.storage.security.CrossDomainCsrfTokenRepo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * This filter is invoked to add CSRF token to response header.
 * Is efficient only together with custom by.azgaar.storage.security.CrossDomainCsrfTokenRepo
 * (see there for more details).
 */

@Component
public class SessionFilter extends OncePerRequestFilter {

    private final CrossDomainCsrfTokenRepo csrfTokenRepository;

    @Autowired
    public SessionFilter(final CrossDomainCsrfTokenRepo csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = csrfTokenRepository.getCsrfToken();
        if (csrfToken != null && !response.containsHeader(csrfToken.getHeaderName())) {
            response.addHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }
        filterChain.doFilter(request, response);
    }

}