package com.bar.stormhook.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.regex.*;

@Component
class TenantEnforcementFilter extends OncePerRequestFilter {
    private static final Pattern P = Pattern.compile("^/admin/tenants/([^/]+)(/.*)?$");

    @Override protected boolean shouldNotFilter(HttpServletRequest req) {
        return !"GET,POST,PUT,PATCH,DELETE".contains(req.getMethod())
                || !req.getRequestURI().startsWith("/admin/tenants/");
    }

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getDetails() == null) {
            res.sendError(401, "Unauthorized"); return;
        }
        var m = P.matcher(req.getRequestURI());
        if (!m.matches()) { chain.doFilter(req,res); return; }
        var pathTenant = m.group(1);
        var jwtTenant = String.valueOf(((java.util.Map<?,?>)auth.getDetails()).get("tenant_id"));
        if (jwtTenant == null || !jwtTenant.equals(pathTenant)) {
            res.sendError(403, "wrong tenant"); return;
        }
        chain.doFilter(req, res);
    }
}
