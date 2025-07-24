import axios from 'axios';
import authService from './auth.service';

const API_URL = 'http://localhost:8080/api/user/';

class UserService {
  getProfile() {
    const user = authService.getCurrentUser();
    return axios.get(API_URL + 'profile', {
      headers: { Authorization: 'Bearer ' + user.token }
    });
  }
}

export default new UserService();