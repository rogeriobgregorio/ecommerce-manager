package com.rogeriogregorio.ecommercemanager.services.strategy.validations.claim;

import com.rogeriogregorio.ecommercemanager.dto.UserTokenDetailsDto;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ValidateExpirationDate implements TokenClaimStrategy {

    @Override
    public void validateTokenClaim(UserTokenDetailsDto userTokenDetails) {

        Instant expirationDateFromToken = userTokenDetails.getExpiresAt();

        if (expirationDateFromToken.isBefore(Instant.now())) {
            throw new TokenJwtException("The token has expired");
        }
    }
}
