package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.responseDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.entity.Seller;
import com.jamith.booksformeapi.service.SellerService;
import com.jamith.booksformeapi.util.DateUtil;
import com.jamith.booksformeapi.util.SellerType;
import com.jamith.booksformeapi.util.UserRole;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SellerServiceImpl implements SellerService {

    public String registerSeller(SellerSignUpDTO sellerSignUpDTO) {

        Firestore db = FirestoreClient.getFirestore();
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(sellerSignUpDTO.getEmail())
                    .setPassword(sellerSignUpDTO.getPasswordHash())
                    .setDisplayName(sellerSignUpDTO.getFullNameOrRepresentative())
                    .setPhoneNumber(sellerSignUpDTO.getPhoneNumber());
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), Map.of("role", UserRole.SELLER.name()));


            Map<String, String> address = new HashMap<>();
            address.put("street", sellerSignUpDTO.getAddressStreet());
            address.put("city", sellerSignUpDTO.getAddressCity());
            address.put("state", sellerSignUpDTO.getAddressState());
            address.put("postalCode", sellerSignUpDTO.getAddressPostalCode());
            address.put("country", sellerSignUpDTO.getAddressCountry());

            Map<String, String> businessDetails = new HashMap<>();
            businessDetails.put("companyName", sellerSignUpDTO.getCompanyName());
            businessDetails.put("businessRegistrationNumber", sellerSignUpDTO.getBusinessRegistrationNumber());

            Seller seller = new Seller();
            seller.setSellerId(userRecord.getUid());
            seller.setEmail(sellerSignUpDTO.getEmail());
            if (sellerSignUpDTO.getSellerType().equalsIgnoreCase("Company"))
                seller.setSellerType(SellerType.COMPANY);
            else if (sellerSignUpDTO.getSellerType().equalsIgnoreCase("Individual"))
                seller.setSellerType(SellerType.INDIVIDUAL);
            seller.setFullNameOrRepresentative(sellerSignUpDTO.getFullNameOrRepresentative());
            seller.setPhoneNumber(sellerSignUpDTO.getPhoneNumber());

            seller.setRole(UserRole.SELLER);
            seller.setSellerId(userRecord.getUid());
            seller.setVerified(false);
            seller.setBusinessDetails(businessDetails);
            seller.setAddress(address);
            seller.setCreatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("sellers").document(seller.getSellerId()).set(seller);
            return "Seller registered at: " + collectionsApiFuture.get().getUpdateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error registering seller";
        }
    }
}

