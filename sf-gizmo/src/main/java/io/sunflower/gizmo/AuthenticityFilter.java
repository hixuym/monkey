package io.sunflower.gizmo;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sunflower.gizmo.utils.NinjaConstant;

/**
 * @author svenkubiak
 */
public class AuthenticityFilter implements Filter {
    static private final Logger logger = LoggerFactory.getLogger(AuthenticityFilter.class);

    private final Ninja ninja;

    @Inject
    public AuthenticityFilter(Ninja ninja) {
        this.ninja = ninja;
    }

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String authenticityToken = context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN);

        if (!context.getSession().getAuthenticityToken().equals(authenticityToken)) {
            logger.warn("Authenticity token mismatch. Request from {} is forbidden!", context.getRemoteAddr());
            return ninja.getForbiddenResult(context);
        }

        return filterChain.next(context);
    }
}