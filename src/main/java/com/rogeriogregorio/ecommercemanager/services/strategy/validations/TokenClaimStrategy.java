package com.rogeriogregorio.ecommercemanager.services.strategy.validations;

import com.rogeriogregorio.ecommercemanager.dto.TokenClaimContextDto;
import org.springframework.stereotype.Component;

@Component
public interface TokenClaimStrategy {

    void validateTokenClaim(TokenClaimContextDto tokenClaimContext);
}
