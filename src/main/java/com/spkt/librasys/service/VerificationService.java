package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.VerificationRequest;

/**
 * Giao diện VerificationService định nghĩa các hành vi liên quan đến việc xác minh tài khoản người dùng.
 * Các phương thức bao gồm việc xác minh tài khoản, gửi lại mã xác minh, và tạo mã xác minh mới.
 */
public interface VerificationService {

   /**
    * Xác minh tài khoản người dùng thông qua mã xác minh.
    *
    * @param request Chứa thông tin yêu cầu xác minh tài khoản, bao gồm email và mã xác minh.
    * @return true nếu tài khoản được xác minh thành công, false nếu xác minh không thành công.
    */
   boolean verifyAccount(VerificationRequest request);

   /**
    * Gửi lại mã xác minh đến email đã đăng ký.
    *
    * @param email Địa chỉ email của người dùng yêu cầu gửi lại mã xác minh.
    * @return true nếu gửi lại mã xác minh thành công, false nếu gặp lỗi trong quá trình gửi.
    */
   boolean resendVerificationCode(String email);

   /**
    * Tạo một mã xác minh mới.
    *
    * @return Mã xác minh mới được tạo.
    */
   String generateVerificationCode();

   /**
    * Gửi mã xác minh đến địa chỉ email người dùng.
    *
    * @param email Địa chỉ email của người dùng cần nhận mã xác minh.
    */
   void verificationCode(String email);
}
