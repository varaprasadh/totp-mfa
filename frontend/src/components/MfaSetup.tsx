import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import authService from '../services/auth.service';
import './MfaSetup.css';

interface MfaSetupProps {
  onComplete: () => void;
  onCancel: () => void;
}

interface VerifyFormData {
  code: string;
}

const MfaSetup: React.FC<MfaSetupProps> = ({ onComplete, onCancel }) => {
  const [qrCodeImage, setQrCodeImage] = useState('');
  const [secret, setSecret] = useState('');
  const [loading, setLoading] = useState(true);
  const [verifying, setVerifying] = useState(false);
  const [error, setError] = useState('');
  
  const { register, handleSubmit, formState: { errors } } = useForm<VerifyFormData>();

  useEffect(() => {
    setupMfa();
  }, []);

  const setupMfa = async () => {
    try {
      const response = await authService.setupMfa();
      setQrCodeImage(response.data.qrCodeImage);
      setSecret(response.data.secret);
      setLoading(false);
    } catch (error) {
      console.error('Error setting up MFA:', error);
      setError('Failed to setup 2FA. Please try again.');
      setLoading(false);
    }
  };

  const onSubmit = async (data: VerifyFormData) => {
    setError('');
    setVerifying(true);

    try {
      await authService.verifyMfa(data.code);
      alert('2FA has been successfully enabled!');
      onComplete();
    } catch (error: any) {
      setError('Invalid verification code. Please try again.');
      setVerifying(false);
    }
  };

  if (loading) {
    return <div className="mfa-setup-modal">
      <div className="mfa-setup-content">
        <div className="loading">Setting up 2FA...</div>
      </div>
    </div>;
  }

  return (
    <div className="mfa-setup-modal">
      <div className="mfa-setup-content">
        <h2>Set Up Two-Factor Authentication</h2>
        
        <div className="mfa-instructions">
          <ol>
            <li>Install an authenticator app on your phone (Google Authenticator, Authy, etc.)</li>
            <li>Scan the QR code below with your authenticator app</li>
            <li>Enter the 6-digit code from your app to verify</li>
          </ol>
        </div>

        <div className="qr-code-section">
          {qrCodeImage && (
            <img 
              src={`data:image/png;base64,${qrCodeImage}`} 
              alt="MFA QR Code" 
              className="qr-code"
            />
          )}
          
          <div className="manual-entry">
            <p>Can't scan? Enter this code manually:</p>
            <code className="secret-code">{secret}</code>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="verify-form">
          <div className="form-group">
            <label htmlFor="code">Verification Code</label>
            <input
              type="text"
              className="form-control"
              placeholder="Enter 6-digit code"
              {...register('code', { 
                required: 'Verification code is required',
                pattern: {
                  value: /^[0-9]{6}$/,
                  message: 'Code must be 6 digits'
                }
              })}
            />
            {errors.code && <span className="error">{errors.code.message}</span>}
          </div>

          {error && <div className="alert alert-error">{error}</div>}

          <div className="form-actions">
            <button 
              type="button" 
              onClick={onCancel} 
              className="btn btn-secondary"
              disabled={verifying}
            >
              Cancel
            </button>
            <button 
              type="submit" 
              className="btn btn-primary" 
              disabled={verifying}
            >
              {verifying ? 'Verifying...' : 'Verify & Enable'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MfaSetup;