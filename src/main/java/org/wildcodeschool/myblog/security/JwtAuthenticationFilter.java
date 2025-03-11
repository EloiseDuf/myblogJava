package org.wildcodeschool.myblog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);  // Création du logger
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        logger.info("🔍 Vérification du token...");

        if (authHeader == null) {
            logger.warn("❌ Aucun header Authorization trouvé");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("❌ Format incorrect du token. Il faut commencer par 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);  // Extraction du token
        String username = jwtService.extractClaims(jwt).getSubject();  // Récupération du nom d'utilisateur à partir du token

        logger.info("✅ Token détecté, utilisateur extrait : " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("🔐 Tentative d'authentification pour l'utilisateur : " + username);

            // Charger les détails de l'utilisateur avec le nom d'utilisateur extrait du token
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Vérifier que le token n'est pas expiré
            if (jwtService.extractClaims(jwt).getExpiration().after(new Date())) {
                logger.info("✅ Token valide, authentification en cours...");

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("❌ Token expiré !");
            }
        } else {
            logger.warn("❌ Le nom d'utilisateur est null ou l'utilisateur est déjà authentifié.");
        }

        filterChain.doFilter(request, response);
    }
}
