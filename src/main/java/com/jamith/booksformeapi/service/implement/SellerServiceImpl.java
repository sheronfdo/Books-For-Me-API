package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.*;
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
import java.util.concurrent.ExecutionException;

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
            seller.setCreatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("sellers").document(seller.getSellerId()).set(seller);
            SellerSignUpResponseDTO sellerSignUpResponseDTO = new SellerSignUpResponseDTO();
            sellerSignUpResponseDTO.setId(userRecord.getUid());
            sellerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Seller Registered Successfully", sellerSignUpResponseDTO);
        } catch (FirebaseAuthException e) {
            return ResponseUtil.generateErrorResponse("Firebase Authentication Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Object> setSellerAddress(SellerAddressDTO sellerAddress) {
        try {
            Seller seller;
            DocumentReference docRef = db.collection("sellers").document(sellerAddress.getId());
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                seller = document.toObject(Seller.class); // Convert the document to a Seller object
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Seller Not Found", HttpStatus.BAD_REQUEST);
            }
            GeoPoint geoPoint = new GeoPoint(sellerAddress.getLatitude(), sellerAddress.getLongitude());

            Map<String, Object> address = new HashMap<>();
            address.put("street", sellerAddress.getAddressStreet());
            address.put("city", sellerAddress.getAddressCity());
            address.put("state", sellerAddress.getAddressState());
            address.put("postalCode", sellerAddress.getAddressPostalCode());
            address.put("country", sellerAddress.getAddressCountry());
            address.put("location", geoPoint);

            seller.setSellerId(sellerAddress.getId());
            seller.setAddress(address);
            seller.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("sellers").document(seller.getSellerId()).set(seller, SetOptions.merge());
            SellerSignUpResponseDTO sellerSignUpResponseDTO = new SellerSignUpResponseDTO();
            sellerSignUpResponseDTO.setId(sellerAddress.getId());
            sellerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Seller Address Added Successfully", sellerSignUpResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> setSellerBusinessRegisterDetail(SellerSignUpBrDTO sellerSignUpBrDTO) {
        try {
            Seller seller;
            DocumentReference docRef = db.collection("sellers").document(sellerSignUpBrDTO.getId());
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                seller = document.toObject(Seller.class); // Convert the document to a Seller object
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Seller Not Found", HttpStatus.BAD_REQUEST);
            }

            Map<String, String> businessDetails = new HashMap<>();
            businessDetails.put("companyName", sellerSignUpBrDTO.getCompanyName());
            businessDetails.put("businessRegistrationNumber", sellerSignUpBrDTO.getBusinessRegistrationNumber());
            businessDetails.put("documentUrl", sellerSignUpBrDTO.getBrDocDownUrl());

            seller.setSellerId(sellerSignUpBrDTO.getId());
            seller.setBusinessDetails(businessDetails);
            seller.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("sellers").document(seller.getSellerId()).set(seller, SetOptions.merge());
            SellerSignUpResponseDTO sellerSignUpResponseDTO = new SellerSignUpResponseDTO();
            sellerSignUpResponseDTO.setId(sellerSignUpBrDTO.getId());
            sellerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Seller Business Details Added Successfully", sellerSignUpResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> setSellerImage(SellerImageDTO sellerImageDTO) {
        try {
            Seller seller;
            DocumentReference docRef = db.collection("sellers").document(sellerImageDTO.getId());
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                seller = document.toObject(Seller.class); // Convert the document to a Seller object
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Seller Not Found", HttpStatus.BAD_REQUEST);
            }

            seller.setSellerId(sellerImageDTO.getId());
            seller.setImageUrl(sellerImageDTO.getImageUrl());
            seller.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("sellers").document(seller.getSellerId()).set(seller, SetOptions.merge());
            SellerSignUpResponseDTO sellerSignUpResponseDTO = new SellerSignUpResponseDTO();
            sellerSignUpResponseDTO.setId(sellerImageDTO.getId());
            sellerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Seller Image Added Successfully", sellerSignUpResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Object> updateSeller(SellerUpdateDTO sellerUpdateDTO) {
        try {
            Seller seller;
            DocumentReference docRef = db.collection("sellers").document(sellerUpdateDTO.getId());
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                seller = document.toObject(Seller.class); // Convert the document to a Seller object
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Seller Not Found", HttpStatus.BAD_REQUEST);
            }
            Map<String, String> businessDetails = new HashMap<>();
            businessDetails.put("companyName", sellerUpdateDTO.getCompanyName());
            businessDetails.put("businessRegistrationNumber", sellerUpdateDTO.getRegistrationNumber());
            businessDetails.put("documentUrl", seller.getBusinessDetails().get("documentUrl"));

            Map<String, Object> address = new HashMap<>();
            address.put("street", sellerUpdateDTO.getStreet());
            address.put("city", sellerUpdateDTO.getCity());
            address.put("state", sellerUpdateDTO.getState());
            address.put("postalCode", sellerUpdateDTO.getPostalCode());
            address.put("country", sellerUpdateDTO.getCountry());
            address.put("location", seller.getAddress().get("location"));


            seller.setSellerId(sellerUpdateDTO.getId());
            seller.setImageUrl(sellerUpdateDTO.getImageUrl());
            seller.setFullNameOrRepresentative(sellerUpdateDTO.getFullName());
            seller.setPhoneNumber(sellerUpdateDTO.getPhoneNumber());
            seller.setBusinessDetails(businessDetails);
            seller.setAddress(address);

            seller.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("sellers").document(seller.getSellerId()).set(seller, SetOptions.merge());
            SellerSignUpResponseDTO sellerSignUpResponseDTO = new SellerSignUpResponseDTO();
            sellerSignUpResponseDTO.setId(sellerUpdateDTO.getId());
            sellerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Seller Update Successfully", sellerSignUpResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

