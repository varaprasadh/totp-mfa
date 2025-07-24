export interface User {
  id: number;
  username: string;
  email: string;
  token: string;
  mfaEnabled: boolean;
  requiresMfa?: boolean;
}

export interface LoginResponse {
  token: string | null;
  id: number;
  username: string;
  email: string;
  mfaEnabled: boolean;
  requiresMfa: boolean;
}

export interface MfaSetupResponse {
  secret: string;
  qrCodeUrl: string;
  qrCodeImage: string;
}