package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.VerificationRequest;

public interface VerificationService {
   boolean  verifyAccount(VerificationRequest request);
   boolean resendVerificationCode(String email);
   String generateVerificationCode();
   void verificationCode(String email);
}
