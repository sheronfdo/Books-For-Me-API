package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.SellerSignUpDTO;
import com.jamith.booksformeapi.dto.responseDTO.SellerSignUpResponseDTO;
import com.jamith.booksformeapi.entity.Seller;
import com.jamith.booksformeapi.service.SellerService;
import com.jamith.booksformeapi.utils.DateUtil;
import com.jamith.booksformeapi.enums.SellerType;
import com.jamith.booksformeapi.enums.UserRole;
import com.jamith.booksformeapi.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SellerServiceImpl implements SellerService {
    Firestore db = FirestoreClient.getFirestore();

    public ResponseEntity<Object> registerSeller(SellerSignUpDTO sellerSignUpDTO) {
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
            SellerSignUpResponseDTO sellerSignUpResponseDTO = new SellerSignUpResponseDTO();
            sellerSignUpResponseDTO.setId(userRecord.getUid());
            sellerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Seller Registered Successfully", sellerSignUpResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

