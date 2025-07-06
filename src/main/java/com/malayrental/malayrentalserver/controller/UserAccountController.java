package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.security.TokenInfo;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.UserAccountService;
import com.malayrental.malayrentalserver.service.impl.UserAccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/api/user")
public class UserAccountController {

    private final UserAccountService userAccountService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * 解析请求中的data参数
     * @param req 请求体
     * @return 解析后的data映射，如果解析失败则返回null
     */
    private Map<String, Object> parseDataMap(Map<String, Object> req) {
        Object dataObj = req.get("data");
        if (!(dataObj instanceof Map)) {
            return null;
        }
        return (Map<String, Object>) dataObj;
    }
    
    /**
     * 从data映射中安全地获取String类型参数
     * @param data 数据映射
     * @param key 键名
     * @return 字符串值，如果不存在或为null则返回null
     */
    private String getStringParam(Map<String, Object> data, String key) {
        return data.get(key) == null ? null : data.get(key).toString();
    }

    /**
     * 创建包含用户信息的Map，包括令牌信息
     * @param user 用户账号对象
     * @param tokenExpiryTime 令牌过期时间
     * @return 包含用户信息的Map
     */
    public static Map<String, Object> createUserInfoMap(UserAccount user, java.time.LocalDateTime tokenExpiryTime) {
        // 获取基本信息
        Map<String, Object> content = UserAccountServiceImpl.createUserBasicInfoMap(user);
        // 修改键名以保持兼容性
        content.put("role", content.remove("userRole"));
        
        // 添加令牌信息
        content.put("userToken", user.getUserToken());
        if (user.getOpenId() != null) {
            content.put("openId", user.getOpenId());
        }
        content.put("tokenExpired", tokenExpiryTime != null ? tokenExpiryTime.format(DATE_FORMATTER) : null);
        return content;
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            String userName = getStringParam(data, "userName");
            String phone = getStringParam(data, "phoneNumber");
            String avatar = getStringParam(data, "avatar");
            String password = getStringParam(data, "password");
            
            // 检查是否为微信注册
            String openId = getStringParam(data, "openId");
            if (openId != null) {
                // 微信注册并直接登录
                UserAccount[] userHolder = new UserAccount[1];
                int result = userAccountService.registerWxUser(userName, phone, avatar, password, openId, userHolder);
                
                if (result == 0) {
                    UserAccount user = userHolder[0];
                    Map<String, Object> content = createUserInfoMap(user, user.getTokenExpired());
                    return ApiResponse.ok("微信注册并登录成功", content);
                }
                
                return switch (result) {
                    case 1 -> ApiResponse.error(400, "账号已存在");
                    case 2 -> ApiResponse.error(400, "参数不合法");
                    default -> ApiResponse.error(500, "系统错误请稍后再试！");
                };
            }
            
            // 普通注册
            int result = userAccountService.registerUser(userName, phone, avatar, password);
            return switch (result) {
                case 0 -> ApiResponse.ok("注册成功，请登录", null);
                case 1 -> ApiResponse.error(400, "账号已存在");
                case 2 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            String phone = getStringParam(data, "phoneNumber");
            String password = getStringParam(data, "password");
            String ip = request.getRemoteAddr();
            UserAccount[] userHolder = new UserAccount[1];
            int result = userAccountService.loginUser(phone, password, ip, userHolder);
            return switch (result) {
                case 0 -> {
                    UserAccount user = userHolder[0];
                    TokenInfo tokenInfo = userAccountService.generateUserToken(user.getUserId());
                    if (tokenInfo != null) {
                        user.setUserToken(tokenInfo.token());
                        user.setTokenExpired(tokenInfo.expired());
                    }
                    Map<String, Object> content = createUserInfoMap(user, user.getTokenExpired());
                    yield ApiResponse.ok("登录成功", content);
                }
                case 1 -> ApiResponse.error(400, "账号或密码错误");
                case 2 -> ApiResponse.error(400, "参数不合法");
                case 3 -> ApiResponse.error(400, "账号状态异常，请联系客服");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/deleteUser")
    public ApiResponse deleteUser(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            String runUserId = getStringParam(data, "runUser");
            String userId = getStringParam(data, "userId");
            if (runUserId == null || userId == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            int result = userAccountService.deleteUser(runUserId, userId);
            return switch (result) {
                case 0 -> ApiResponse.ok("删除成功", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/updateUser")
    public ApiResponse updateUser(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            int result = userAccountService.updateUser(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("更新成功", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/banUser")
    public ApiResponse banUser(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            int result = userAccountService.banUser(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("用户已封禁", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/unbanUser")
    public ApiResponse unbanUser(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            int result = userAccountService.unbanUser(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("用户已解封", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/getUserList")
    public ApiResponse getUserList(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        
        try {
            java.util.List<java.util.Map<String, Object>> resultList = new java.util.ArrayList<>();
            int code = userAccountService.getUserList(data, resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取用户列表成功", resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/logout")
    public ApiResponse logout(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        
        try {
            int code = userAccountService.logout(data);
            return switch (code) {
                case 0 -> ApiResponse.ok("用户登出成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/autoLogin")
    public ApiResponse autoLogin(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            
            String phone = getStringParam(data, "phoneNumber");
            String userToken = getStringParam(data, "userToken");
            String ip = request.getRemoteAddr();
            UserAccount[] userHolder = new UserAccount[1];
            int result = userAccountService.autoLogin(phone, userToken, ip, userHolder);
            return switch (result) {
                case 0 -> {
                    UserAccount user = userHolder[0];
                    Map<String, Object> content = createUserInfoMap(user, user.getTokenExpired());
                    yield ApiResponse.ok("登录成功", content);
                }
                case 1 -> ApiResponse.error(400, "账号或密码错误");
                case 2 -> ApiResponse.error(400, "参数不合法");
                case 3 -> ApiResponse.error(400, "账号状态异常，请联系客服");
                case 4 -> ApiResponse.error(400, "你的登录信息已失效，请重新登录");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }
}