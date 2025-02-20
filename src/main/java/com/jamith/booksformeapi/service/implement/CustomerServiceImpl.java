package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.CustomerSignUpDTO;
import com.jamith.booksformeapi.dto.requestDTO.CustomerUpdateDTO;
import com.jamith.booksformeapi.dto.responseDTO.BookAddResponseDTO;
import com.jamith.booksformeapi.dto.responseDTO.CustomerSignUpResponseDTO;
import com.jamith.booksformeapi.entity.Book;
import com.jamith.booksformeapi.entity.Customer;
import com.jamith.booksformeapi.entity.Seller;
import com.jamith.booksformeapi.enums.UserRole;
import com.jamith.booksformeapi.service.CustomerService;
import com.jamith.booksformeapi.utils.DateUtil;
import com.jamith.booksformeapi.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class CustomerServiceImpl implements CustomerService {
    ModelMapper modelMapper = new ModelMapper();
    Firestore db = FirestoreClient.getFirestore();


    @Override
    public ResponseEntity<Object> customerSignUp(CustomerSignUpDTO customerSignUpDTO) {
        try {
            Customer customer = modelMapper.map(customerSignUpDTO, Customer.class);
            customer.setRole(UserRole.CUSTOMER);
            customer.setCreatedAt(DateUtil.fromFirestoreTimestamp());
            customer.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("customers").document(customer.getUid()).set(customer);
            CustomerSignUpResponseDTO customerSignUpResponseDTO = new CustomerSignUpResponseDTO();
            customerSignUpResponseDTO.setId(customer.getUid());
            customerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Customer Sign Up Successful.", customerSignUpResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> updateCustomer(CustomerUpdateDTO customerUpdateDTO) {
        try {
            Customer customer;
            DocumentReference docRef = db.collection("customers").document(customerUpdateDTO.getId());
            ApiFuture<DocumentSnapshot> future = docRef.get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                customer = document.toObject(Customer.class); // Convert the document to a Seller object
            } else {
                System.out.println("No such document!");
                return ResponseUtil.generateErrorResponse("Customer Not Found", HttpStatus.BAD_REQUEST);
            }

            customer.setDisplayName(customerUpdateDTO.getDisplayName());
            customer.setFirstName(customerUpdateDTO.getFirstName());
            customer.setLastName(customerUpdateDTO.getLastName());
            customer.setImageUri(customerUpdateDTO.getImageUrl());
            customer.setPhoneNumber(customerUpdateDTO.getPhoneNumber());

            customer.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            ApiFuture<WriteResult> collectionsApiFuture = db.collection("customers").document(customerUpdateDTO.getId()).set(customer);
            CustomerSignUpResponseDTO customerSignUpResponseDTO = new CustomerSignUpResponseDTO();
            customerSignUpResponseDTO.setId(customer.getUid());
            customerSignUpResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Customer Update Successful.", customerSignUpResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
