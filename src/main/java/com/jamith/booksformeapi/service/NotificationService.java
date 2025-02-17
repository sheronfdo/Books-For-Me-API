package com.jamith.booksformeapi.service;

public interface NotificationService {
    String sendNotificationToToken(String token, String title, String body);
}
