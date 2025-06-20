import axios from 'axios';

const BASE_URL = '/api/relations';

export default {
  // 创建关系
  create(relation, userId) {
    return axios.post(`${BASE_URL}`, relation, { params: { userId } });
  },
  // 批量创建关系
  batchCreate(relations, userId) {
    return axios.post(`${BASE_URL}/batch`, relations, { params: { userId } });
  },
  // 获取所有关系
  getAll(userId) {
    return axios.get(`${BASE_URL}`, { params: { userId } });
  },
  // 根据ID获取关系
  getById(id, userId) {
    return axios.get(`${BASE_URL}/${id}`, { params: { userId } });
  },
  // 获取与指定块相关的所有关系
  getByBlock(blockId, userId) {
    return axios.get(`${BASE_URL}/block/${blockId}`, { params: { userId } });
  },
  // 根据关系类型获取关系
  getByType(type, userId) {
    return axios.get(`${BASE_URL}/type/${type}`, { params: { userId } });
  },
  // 更新关系类型
  update(id, relationType, userId) {
    return axios.put(`${BASE_URL}/${id}`, { relationType }, { params: { userId } });
  },
  // 删除关系
  delete(id, userId) {
    return axios.delete(`${BASE_URL}/${id}`, { params: { userId } });
  },
  // 删除与指定块相关的所有关系
  deleteByBlock(blockId, userId) {
    return axios.delete(`${BASE_URL}/block/${blockId}`, { params: { userId } });
  },
  // 删除用户所有关系
  deleteAll(userId) {
    return axios.delete(`${BASE_URL}`, { params: { userId } });
  },
  // 检查两个块之间是否存在关系
  exists(sourceBlockId, targetBlockId, userId) {
    return axios.post(`${BASE_URL}/exists`, { sourceBlockId, targetBlockId }, { params: { userId } });
  },
  // 获取两个块之间的关系
  getBetween(sourceBlockId, targetBlockId, userId) {
    return axios.post(`${BASE_URL}/between`, { sourceBlockId, targetBlockId }, { params: { userId } });
  },
  // 获取关系统计信息
  getStats(userId) {
    return axios.get(`${BASE_URL}/stats`, { params: { userId } });
  }
}; 