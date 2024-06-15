package com.rogeriogregorio.ecommercemanager.services.strategy.validations;

import com.rogeriogregorio.ecommercemanager.dto.UserTokenDetailsDto;
import org.springframework.stereotype.Component;

@Component
public interface TokenClaimStrategy {

    void validateTokenClaim(UserTokenDetailsDto tokenClaimContext);
}
