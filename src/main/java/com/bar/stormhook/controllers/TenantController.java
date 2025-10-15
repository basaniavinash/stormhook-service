package com.bar.stormhook.controllers;

import com.bar.stormhook.model.Tenant;
import com.bar.stormhook.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/tenants")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @PostMapping
    public Tenant saveTenant(@RequestBody Tenant tenant){
        return tenantService.save(tenant);
    }

    @GetMapping(value = "/{id}")
    public Tenant getById(@PathVariable("id") UUID uuid){
        return tenantService.getById(uuid);
    }

    @PutMapping(value = "/{id}")
    public Tenant updateTenant(@RequestBody Tenant tenant){
        return tenantService.save(tenant);
    }
}
