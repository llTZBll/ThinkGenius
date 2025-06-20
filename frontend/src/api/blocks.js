import axios from 'axios';

const BASE_URL = '/api/blocks';

export default {
  // 获取所有块
  getAll(userId) {
    return axios.get(`${BASE_URL}`, { params: { userId } });
  },
  // 创建新块（自动布局）
  createAuto(block, userId) {
    return axios.post(`${BASE_URL}/auto-position`, block, { params: { userId } });
  },
  // 重新布局所有块
  relayout(userId) {
    return axios.post(`${BASE_URL}/relayout`, null, { params: { userId } });
  },
  // 检查位置是否可用
  checkPosition(data, userId) {
    return axios.post(`${BASE_URL}/check-position`, data, { params: { userId } });
  },
  // 找到最近的可用位置
  findNearestPosition(data, userId) {
    return axios.post(`${BASE_URL}/find-nearest-position`, data, { params: { userId } });
  },
  // 获取画布配置
  getCanvasConfig() {
    return axios.get(`${BASE_URL}/canvas-config`);
  },
  // 创建新块（手动指定位置）
  create(block, userId) {
    return axios.post(`${BASE_URL}`, block, { params: { userId } });
  },
  // 按类型获取块
  getByType(type, userId) {
    return axios.get(`${BASE_URL}/type/${type}`, { params: { userId } });
  },
  // 按ID获取块
  getById(id, userId) {
    return axios.get(`${BASE_URL}/${id}`, { params: { userId } });
  },
  // 更新块
  update(id, block, userId) {
    return axios.put(`${BASE_URL}/${id}`, block, { params: { userId } });
  },
  // 更新块位置
  updatePosition(id, position, userId) {
    return axios.patch(`${BASE_URL}/${id}/position`, position, { params: { userId } });
  },
  // 删除块
  delete(id, userId) {
    return axios.delete(`${BASE_URL}/${id}`, { params: { userId } });
  },
  // 删除用户所有块
  deleteAll(userId) {
    return axios.delete(`${BASE_URL}`, { params: { userId } });
  }
}; 