package com.rogeriogregorio.ecommercemanager.services.strategy.validations.claim;

import com.rogeriogregorio.ecommercemanager.dto.UserTokenDetailsDto;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateUserEmail implements TokenClaimStrategy {

    @Override
    public void validateTokenClaim(UserTokenDetailsDto userTokenDetails) {

        String userEmailFromToken = userTokenDetails.getClaimUserPassword();
        String userEmail = userTokenDetails.getUserEmail();

        if (!userEmailFromToken.equals(userEmail)) {
            throw new TokenJwtException("The user with the token email was not found");
        }
    }
}
