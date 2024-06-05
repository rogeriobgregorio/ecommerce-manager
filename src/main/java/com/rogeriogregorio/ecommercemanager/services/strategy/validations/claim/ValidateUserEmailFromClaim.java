package com.rogeriogregorio.ecommercemanager.services.strategy.validations.claim;

import com.rogeriogregorio.ecommercemanager.dto.TokenClaimContextDto;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateUserEmailFromClaim implements TokenClaimStrategy {

    @Override
    public void validateTokenClaim(TokenClaimContextDto tokenClaimContext) {

        String userEmailFromToken = tokenClaimContext.getDecodedJWT().getClaim("userEmail").asString();
        String userEmail = tokenClaimContext.getUser().getEmail();

        if (!userEmailFromToken.equals(userEmail)) {
            throw new TokenJwtException("The user with the token email was not found");
        }
    }
}
