package com.bookingapp.application.auth;

import com.bookingapp.application.profile.UserView;

public record AuthResult(String token, UserView user) {
}
