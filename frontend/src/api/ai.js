import axios from 'axios';

const BASE_URL = '/api/ai';

export default {
  // 生成关键词
  generateKeywords(data) {
    return axios.post(`${BASE_URL}/keywords`, data);
  },
  // 生成关键词介绍
  analyze(data) {
    return axios.post(`${BASE_URL}/analyze`, data);
  }
}; 