import React from 'react';
import { Navigate } from 'react-router-dom';
import authService from '../services/auth.service';

interface PrivateRouteProps {
  children: React.ReactElement;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const currentUser = authService.getCurrentUser();
  
  return currentUser && currentUser.token ? children : <Navigate to="/login" />;
};

export default PrivateRoute;