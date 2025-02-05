package com.dzhenetl.simstat.security.service;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.*;

@SuppressWarnings("unchecked")
public class CustomOidcUserService extends OidcUserService {

    private final JwtDecoder jwtDecoder;
    private final String keycloakClientId;

    public CustomOidcUserService(JwtDecoder jwtDecoder, String keycloakClientId) {
        this.jwtDecoder = jwtDecoder;
        this.keycloakClientId = keycloakClientId;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        var accessToken = userRequest.getAccessToken().getTokenValue();
        Jwt decodedJwt = jwtDecoder.decode(accessToken);
        Map<String, Object> accessTokenClaims = decodedJwt.getClaims();
        Collection<GrantedAuthority> authorities = new ArrayList<>(oidcUser.getAuthorities());
        authorities.addAll(getClientRoles(accessTokenClaims));
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private Collection<GrantedAuthority> getClientRoles(Map<String, Object> accessTokenClaims) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (accessTokenClaims.containsKey("resource_access")) {
            var resourceAccess = (Map<String, Object>) accessTokenClaims.get("resource_access");
            if (resourceAccess.containsKey(keycloakClientId)) {
                var clientAccess = (Map<String, Object>) resourceAccess.get(keycloakClientId);
                authorities.addAll(getRolesByAccess(clientAccess));
            }
        }
        return authorities;
    }

    private Collection<GrantedAuthority> getRolesByAccess(Map<String, Object> access) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        var roles = (List<String>) access.get("roles");
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
        return authorities;
    }
}
