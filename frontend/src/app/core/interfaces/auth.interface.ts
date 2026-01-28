export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  // Add other fields if your backend returns them (e.g., user details)
}