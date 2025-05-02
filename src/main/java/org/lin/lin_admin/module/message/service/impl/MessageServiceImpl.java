package org.lin.lin_admin.module.message.service.impl;

import org.lin.lin_admin.common.paging.PageResult;
import org.lin.lin_admin.module.message.dto.MessageDTO;
import org.lin.lin_admin.module.message.dto.MessageVO;
import org.lin.lin_admin.module.message.mapper.MessageMapper;
import org.lin.lin_admin.module.message.model.Message;
import org.lin.lin_admin.module.message.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    
    @Override
    public boolean create(MessageDTO dto, HttpServletRequest request) {
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        
        // 设置IP地址和用户代理信息
        message.setIpAddress(getClientIp(request));
        message.setUserAgent(request.getHeader("User-Agent"));
        
        // 设置初始状态为待处理
        message.setStatus("pending");
        
        return messageMapper.insert(message) > 0;
    }
    
    @Override
    public PageResult<MessageVO> page(int page, int size, String status) {
        int offset = (page - 1) * size;
        List<Message> messages;
        long total;
        
        if (status != null && !status.isEmpty()) {
            messages = messageMapper.findByStatus(status, offset, size);
            total = messageMapper.countByStatus(status);
        } else {
            messages = messageMapper.findAll(offset, size);
            total = messageMapper.count();
        }
        
        List<MessageVO> voList = messages.stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageResult<>(voList, total, page, size);
    }
    
    @Override
    public boolean delete(Long id) {
        return messageMapper.delete(id) > 0;
    }
    
    @Override
    public boolean reply(Long id, String replyContent, Long adminId) {
        LocalDateTime replyAt = LocalDateTime.now();
        return messageMapper.updateReply(id, replyContent, replyAt, adminId) > 0;
    }
    
    @Override
    public boolean updateStatus(Long id, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("无效的状态值: " + status);
        }
        return messageMapper.updateStatus(id, status) > 0;
    }
    
    @Override
    public MessageVO getById(Long id) {
        Message message = messageMapper.findById(id);
        if (message == null) {
            return null;
        }
        return convertToVO(message);
    }
    
    /**
     * 将Message实体转换为MessageVO
     */
    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        
        // 隐藏敏感信息，如联系方式
        if (vo.getContactInfo() != null && vo.getContactInfo().length() > 0) {
            vo.setContactInfo(maskContactInfo(vo.getContactInfo()));
        }
        
        // TODO: 通过adminId获取管理员名称
        if (message.getAdminId() != null) {
            vo.setAdminName("管理员");
        }
        
        return vo;
    }
    
    /**
     * 遮蔽联系信息
     */
    private String maskContactInfo(String contactInfo) {
        if (contactInfo == null || contactInfo.isEmpty()) {
            return contactInfo;
        }
        
        // 如果是邮箱，遮蔽用户名部分
        if (contactInfo.contains("@")) {
            int atIndex = contactInfo.indexOf('@');
            if (atIndex > 1) {
                String prefix = contactInfo.substring(0, Math.min(3, atIndex));
                return prefix + "***" + contactInfo.substring(atIndex);
            }
        }
        
        // 如果是电话号码，只显示前3位和后4位
        if (contactInfo.matches("\\d+") && contactInfo.length() >= 7) {
            return contactInfo.substring(0, 3) + "****" + 
                   contactInfo.substring(contactInfo.length() - 4);
        }
        
        // 默认遮蔽中间部分
        if (contactInfo.length() > 4) {
            return contactInfo.substring(0, 2) + "***" + 
                   contactInfo.substring(contactInfo.length() - 2);
        }
        
        return contactInfo;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * 验证状态值是否有效
     */
    private boolean isValidStatus(String status) {
        return "pending".equals(status) || 
               "replied".equals(status) || 
               "hidden".equals(status);
    }
} 