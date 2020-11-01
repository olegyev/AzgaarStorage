package by.azgaar.storage.security;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is essentially a wrapper for a cookie based CSRF protection scheme.
 * The issue with the pure cookie based mechanism is that if you deploy the UI on a different domain to the API then
 * the client is not able to read the cookie value when a new CSRF token is generated (even if the cookie is not HTTP only).
 * This mechanism does the same thing, but also provides a response header so that the client can read this value and the use
 * some local mechanism to store the token (local storage, local user agent DB, construct a new cookie on the UI domain etc).
 *
 * @see <a href="https://stackoverflow.com/questions/45424496/csrf-cross-domain">https://stackoverflow.com/questions/45424496/csrf-cross-domain</a>
 */

@Component
public class CrossDomainCsrfTokenRepo implements CsrfTokenRepository {

    public static final String XSRF_HEADER_NAME = "X-XSRF-TOKEN";
    public static final String XSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
    private static final String CSRF_QUERY_PARAM_NAME = "_csrf";
    private final CookieCsrfTokenRepository delegate = new CookieCsrfTokenRepository();
    private CsrfToken token;

    public CrossDomainCsrfTokenRepo() {
        delegate.setCookieHttpOnly(true);
        delegate.setHeaderName(XSRF_HEADER_NAME);
        delegate.setCookieName(XSRF_TOKEN_COOKIE_NAME);
        delegate.setParameterName(CSRF_QUERY_PARAM_NAME);
    }

    @Override
    public CsrfToken generateToken(final HttpServletRequest request) {
        return delegate.generateToken(request);
    }

    @Override
    public void saveToken(final CsrfToken token, final HttpServletRequest request, final HttpServletResponse response) {
    	if (token != null) {
    		response.setHeader("Set-Cookie", XSRF_TOKEN_COOKIE_NAME + "=" + token.getToken() + "; Path=/; Secure; HttpOnly; SameSite=None");
            response.addHeader(XSRF_HEADER_NAME, token.getToken());
            this.token = token;
            /**
             * Commented out to prevent adding additional default incorrect CSRF cookie (without Same-Site=None; Secure).
             * Uncomment to add default CSRF cookie.
             */
            // delegate.saveToken(token, request, response);
        }
    }

    @Override
    public CsrfToken loadToken(final HttpServletRequest request) {
        return delegate.loadToken(request);
    }

    public CsrfToken getCsrfToken() {
        return token;
    }

}