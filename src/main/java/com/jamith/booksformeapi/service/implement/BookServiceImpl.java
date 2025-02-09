package com.jamith.booksformeapi.service.implement;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.jamith.booksformeapi.dto.requestDTO.AddNewBookDTO;
import com.jamith.booksformeapi.dto.responseDTO.BookAddResponseDTO;
import com.jamith.booksformeapi.entity.Book;
import com.jamith.booksformeapi.service.BookService;
import com.jamith.booksformeapi.utils.DateUtil;
import com.jamith.booksformeapi.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;


@Service
public class BookServiceImpl implements BookService {

    Firestore db = FirestoreClient.getFirestore();
    ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseEntity<Object> addNewBook(AddNewBookDTO addNewBookDTO) {
        try {
            Book book = modelMapper.map(addNewBookDTO, Book.class);
            book.setCreatedAt(DateUtil.fromFirestoreTimestamp());
            book.setUpdatedAt(DateUtil.fromFirestoreTimestamp());

            String documentId = db.collection("books").document().getId();
            ApiFuture<WriteResult> collectionsApiFuture = db.collection("books").document(documentId).set(book);
            BookAddResponseDTO bookAddResponseDTO = new BookAddResponseDTO();
            bookAddResponseDTO.setId(documentId);
            bookAddResponseDTO.setCreatedTime(collectionsApiFuture.get().getUpdateTime().toDate());
            return ResponseUtil.generateSuccessResponse("Book Added Successfully", bookAddResponseDTO);
        } catch (FirestoreException | ExecutionException | InterruptedException e) {
            return ResponseUtil.generateErrorResponse("Firestore Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.generateErrorResponse("Invalid Input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
