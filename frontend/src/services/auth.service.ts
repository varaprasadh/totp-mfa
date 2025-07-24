import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';

class AuthService {
  login(username: string, password: string, totpCode?: string) {
    return axios
      .post(API_URL + 'signin', {
        username,
        password,
        totpCode
      })
      .then(response => {
        if (response.data.token) {
          localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
      });
  }

  logout() {
    localStorage.removeItem('user');
  }

  register(username: string, email: string, password: string) {
    return axios.post(API_URL + 'signup', {
      username,
      email,
      password
    });
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) return JSON.parse(userStr);
    return null;
  }

  setupMfa() {
    const user = this.getCurrentUser();
    return axios.post(API_URL + 'mfa/setup', {}, {
      headers: { Authorization: 'Bearer ' + user.token }
    });
  }

  verifyMfa(code: string) {
    const user = this.getCurrentUser();
    return axios.post(API_URL + 'mfa/verify', { code }, {
      headers: { Authorization: 'Bearer ' + user.token }
    });
  }

  disableMfa() {
    const user = this.getCurrentUser();
    return axios.post(API_URL + 'mfa/disable', {}, {
      headers: { Authorization: 'Bearer ' + user.token }
    });
  }
}

export default new AuthService();