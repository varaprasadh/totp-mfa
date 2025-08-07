# MFA demo
A full-stack application demonstrating Multi-Factor Authentication (MFA) using Spring Boot and React with authenticator apps like Google Authenticator.
## Features
- User registration and login
- TOTP-based two-factor authentication
- QR code generation for easy setup with authenticator apps
- JWT-based authentication
- Enable/disable MFA from user dashboard
## Project Structure
```
mfa-app/
├── backend/         # Spring Boot API
└── frontend/        # React application
```
## Prerequisites
- Java 17+
- Node.js 14+
- Maven
- An authenticator app (Google Authenticator, Authy, etc.)
## Running the Application
### Backend (Spring Boot)
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```
The backend will start on `http://localhost:8080`
### Frontend (React)
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   ```
The frontend will start on `http://localhost:3000`
## Usage
1. **Register**: Create a new account at `/register`
2. **Login**: Sign in with your credentials at `/login`
3. **Enable MFA**: 
   - Go to your dashboard
   - Click "Enable 2FA"
   - Scan the QR code with your authenticator app
   - Enter the 6-digit code to verify
4. **Login with MFA**: After enabling MFA, you'll need to enter the 6-digit code from your authenticator app when logging in
## API Endpoints
• POST /api/auth/signup - Register new user
• POST /api/auth/signin - Login (with optional TOTP code)
• POST /api/auth/mfa/setup - Generate MFA secret and QR code
• POST /api/auth/mfa/verify - Verify and enable MFA
• POST /api/auth/mfa/disable - Disable MFA
• GET /api/user/profile - Get user profile (protected)
## Security Notes
• This is a demo application. In production:
  - Use HTTPS
  - Store JWT secrets securely
  - Use a production database
  - Implement rate limiting
  - Add CSRF protection
  - Use environment variables for configuration
