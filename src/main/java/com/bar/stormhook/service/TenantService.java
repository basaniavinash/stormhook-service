package com.bar.stormhook.service;

import com.bar.stormhook.model.Tenant;
import com.bar.stormhook.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantService {
    TenantRepository tenantRepository;
    public TenantService(TenantRepository tenantRepository){
        this.tenantRepository = tenantRepository;
    }

    public Tenant save(Tenant tenant){
        return tenantRepository.save(tenant);
    }

    public Tenant getById(UUID uuid) {
        return tenantRepository.getReferenceById(uuid);
    }
}
