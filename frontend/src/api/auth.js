import axios from 'axios';

const BASE_URL = '/api/auth';

export default {
  // 用户注册
  register(data) {
    return axios.post(`${BASE_URL}/register`, data);
  },
  // 用户登录
  login(data) {
    return axios.post(`${BASE_URL}/login`, data);
  },
  // 获取用户信息（需带token）
  getUser(token) {
    return axios.get(`${BASE_URL}/user`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}; 