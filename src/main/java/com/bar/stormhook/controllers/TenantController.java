package com.bar.stormhook.controllers;

import com.bar.stormhook.model.Tenant;
import com.bar.stormhook.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/tenants")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @PreAuthorize("hasAuthority('SCOPE_admin:tenants:create')")
    @PostMapping
    public Tenant saveTenant(@RequestBody Tenant tenant){
        return tenantService.save(tenant);
    }

    @PreAuthorize("hasAuthority('SCOPE_admin:tenants:read')")
    @GetMapping(value = "/{id}")
    public Tenant getById(@PathVariable("id") UUID uuid){
        return tenantService.getById(uuid);
    }

    @PreAuthorize("hasAuthority('SCOPE_admin:tenants:write')")
    @PutMapping(value = "/{id}")
    public Tenant updateTenant(@RequestBody Tenant tenant){
        return tenantService.save(tenant);
    }
}
