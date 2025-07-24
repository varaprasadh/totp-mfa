import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import authService from '../services/auth.service';
import './Auth.css';

interface LoginFormData {
  username: string;
  password: string;
  totpCode?: string;
}

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [requiresMfa, setRequiresMfa] = useState(false);

  const onSubmit = async (data: LoginFormData) => {
    setMessage('');
    setLoading(true);

    try {
      const response = await authService.login(data.username, data.password, data.totpCode);
      
      if (response.requiresMfa && !data.totpCode) {
        setRequiresMfa(true);
        setLoading(false);
        setMessage('Please enter your 2FA code');
        return;
      }

      navigate('/dashboard');
    } catch (error: any) {
      const resMessage =
        (error.response &&
          error.response.data &&
          error.response.data.message) ||
        error.message ||
        error.toString();

      setLoading(false);
      setMessage(resMessage);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-form">
        <h2>Login</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              className="form-control"
              {...register('username', { required: 'Username is required' })}
            />
            {errors.username && <span className="error">{errors.username.message}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              className="form-control"
              {...register('password', { required: 'Password is required' })}
            />
            {errors.password && <span className="error">{errors.password.message}</span>}
          </div>

          {requiresMfa && (
            <div className="form-group">
              <label htmlFor="totpCode">2FA Code</label>
              <input
                type="text"
                className="form-control"
                placeholder="Enter 6-digit code"
                {...register('totpCode', { 
                  required: requiresMfa ? '2FA code is required' : false,
                  pattern: {
                    value: /^[0-9]{6}$/,
                    message: 'Code must be 6 digits'
                  }
                })}
              />
              {errors.totpCode && <span className="error">{errors.totpCode.message}</span>}
            </div>
          )}

          <div className="form-group">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading && <span className="spinner"></span>}
              Login
            </button>
          </div>

          {message && (
            <div className="alert">
              {message}
            </div>
          )}

          <div className="form-footer">
            Don't have an account? <a href="/register">Register</a>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;