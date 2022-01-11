package com.yang.user.Controller;

import com.yang.pojo.IMOOCJSONResult;
import com.yang.user.impl.UserLoginService;
import com.yang.user.impl.pojo.BO.UserBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(value = "注册相关", tags = {"跟注册相关的接口"})
@RestController
@RequestMapping("passport")
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    /**
     * 注册接口
     * @param userBO
     * @return
     */
    @PostMapping("/register")
    public IMOOCJSONResult register(@RequestBody UserBO userBO) {
        // 注册功能Controller
        if(userBO==null||userBO.getUsername()==null||
        userBO.getEmail()==null||userBO.getPassword()==null||userBO.getRealname()==null
                ||userBO.getUsername()==""||userBO.getEmail()==""||userBO.getPassword()==""||userBO.getRealname()=="")
        {
            return IMOOCJSONResult.errorMsg("信息不能为空");
        }
        if(userLoginService.Reigister(userBO))
        {
            return IMOOCJSONResult.ok("注册成功");
        }
        return IMOOCJSONResult.errorMsg("注册失败,邮箱可能已存在");
    }

    /**
     * 登录接口
     * @param userBO
     * @return
     */
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO)
    {
        // 登录功能Controller
        if(userBO==null||userBO.getEmail()==""||userBO.getPassword()=="")
        {
            return IMOOCJSONResult.errorMsg("信息不能为空");
        }
        UserBO newUserBO = userLoginService.Login(userBO);
        if(newUserBO != null)
        {
            return IMOOCJSONResult.ok(newUserBO);
        }
        return IMOOCJSONResult.errorMsg("邮箱或密码错误");
    }

}
