package com.rogeriogregorio.ecommercemanager.services.strategy.validations.claim;

import com.rogeriogregorio.ecommercemanager.dto.TokenClaimContextDto;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateUserPasswordFromClaim implements TokenClaimStrategy {

    @Override
    public void validateTokenClaim(TokenClaimContextDto tokenClaimContext) {

        String userPasswordFromToken = tokenClaimContext.getDecodedJWT().getClaim("userPassword").asString();
        String userPassword = tokenClaimContext.getUser().getPassword();

        if (!userPasswordFromToken.equals(userPassword)) {
            throw new TokenJwtException("Invalid token");
        }
    }
}
