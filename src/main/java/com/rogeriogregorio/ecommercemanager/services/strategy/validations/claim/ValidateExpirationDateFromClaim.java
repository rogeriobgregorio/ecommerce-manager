package com.rogeriogregorio.ecommercemanager.services.strategy.validations.claim;

import com.rogeriogregorio.ecommercemanager.dto.TokenClaimContextDto;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ValidateExpirationDateFromClaim implements TokenClaimStrategy {

    @Override
    public void validateTokenClaim(TokenClaimContextDto tokenClaimContext) {

        Instant expirationDateFromToken = tokenClaimContext.getDecodedJWT().getExpiresAt().toInstant();

        if (expirationDateFromToken.isBefore(Instant.now())) {
            throw new TokenJwtException("The token has expired");
        }
    }
}
