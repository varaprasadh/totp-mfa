import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/auth.service';
import userService from '../services/user.service';
import MfaSetup from './MfaSetup';
import './Dashboard.css';

interface UserProfile {
  id: number;
  username: string;
  email: string;
  mfaEnabled: boolean;
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [showMfaSetup, setShowMfaSetup] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const response = await userService.getProfile();
      setProfile(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Error loading profile:', error);
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const handleMfaToggle = () => {
    if (profile?.mfaEnabled) {
      if (window.confirm('Are you sure you want to disable 2FA?')) {
        disableMfa();
      }
    } else {
      setShowMfaSetup(true);
    }
  };

  const disableMfa = async () => {
    try {
      await authService.disableMfa();
      await loadProfile();
      alert('2FA has been disabled');
    } catch (error) {
      console.error('Error disabling MFA:', error);
      alert('Failed to disable 2FA');
    }
  };

  const handleMfaSetupComplete = () => {
    setShowMfaSetup(false);
    loadProfile();
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <button onClick={handleLogout} className="btn btn-secondary">
          Logout
        </button>
      </div>

      <div className="dashboard-content">
        <div className="profile-card">
          <h2>Profile Information</h2>
          <div className="profile-info">
            <p><strong>Username:</strong> {profile?.username}</p>
            <p><strong>Email:</strong> {profile?.email}</p>
            <p><strong>2FA Status:</strong> 
              <span className={profile?.mfaEnabled ? 'status-enabled' : 'status-disabled'}>
                {profile?.mfaEnabled ? ' Enabled' : ' Disabled'}
              </span>
            </p>
          </div>

          <div className="mfa-section">
            <h3>Two-Factor Authentication</h3>
            <p>
              {profile?.mfaEnabled 
                ? 'Your account is protected with 2FA.' 
                : 'Enable 2FA to add an extra layer of security to your account.'}
            </p>
            <button 
              onClick={handleMfaToggle} 
              className={profile?.mfaEnabled ? 'btn btn-danger' : 'btn btn-primary'}
            >
              {profile?.mfaEnabled ? 'Disable 2FA' : 'Enable 2FA'}
            </button>
          </div>
        </div>

        {showMfaSetup && (
          <MfaSetup 
            onComplete={handleMfaSetupComplete} 
            onCancel={() => setShowMfaSetup(false)} 
          />
        )}
      </div>
    </div>
  );
};

export default Dashboard;