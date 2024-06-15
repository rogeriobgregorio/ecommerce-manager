package com.rogeriogregorio.ecommercemanager.services.strategy.validations.claim;

import com.rogeriogregorio.ecommercemanager.dto.UserTokenDetailsDto;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateUserPassword implements TokenClaimStrategy {

    @Override
    public void validateTokenClaim(UserTokenDetailsDto userTokenDetails) {

        String userPasswordFromToken = userTokenDetails.getClaimUserPassword();
        String userPassword = userTokenDetails.getUserPassword();

        if (!userPasswordFromToken.equals(userPassword)) {
            throw new TokenJwtException("Invalid token");
        }
    }
}
