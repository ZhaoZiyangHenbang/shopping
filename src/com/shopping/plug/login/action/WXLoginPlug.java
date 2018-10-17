package com.shopping.plug.login.action;

import com.self.common.utils.UtilsJson;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.Md5Encrypt;
import com.shopping.foundation.domain.Accessory;
import com.shopping.foundation.domain.Album;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by 果果 on 2017/6/23.
 */
@Controller
public class WXLoginPlug {

    @Autowired
    private ISysConfigService configService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IAlbumService albumService;
    @Autowired
    private IAccessoryService accessoryService;

    private String appid = "wx97fd084a0a76d5e1";
    private String AppSecret = "5a56ac2bda56f4ae7d2b965bcebe7d51";

    @RequestMapping({"/wx_login_api.htm"})
    public void wx_login_api(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        String redirect_uri = "http://www.yijia1234.com/wx_login_bind.htm";
        String uri = URLEncoder.encode(redirect_uri, "UTF-8");
        String auth_url = "https://open.weixin.qq.com/connect/qrconnect?appid="+appid+"&redirect_uri="
                + uri + "&response_type=code&scope=snsapi_login&state=STATE#";
        response.sendRedirect(auth_url);
    }

    @RequestMapping({"/wx_login_bind.htm"})
    public String wx_login_bind(HttpServletRequest request, HttpServletResponse response, String code,String state) throws IOException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid="+appid+"&secret="+AppSecret+"&code="+code+"&grant_type=authorization_code";
        url = CommUtil.null2String(getHttpContent(url, "UTF-8", "GET"));
        String access_token = (String) UtilsJson.toMap(url).get("access_token");
        String openid = (String) UtilsJson.toMap(url).get("openid");
        String UnionID = " https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid;
        UnionID = CommUtil.null2String(getHttpContent(UnionID, "UTF-8", "GET"));
        String nickname = (String) UtilsJson.toMap(UnionID).get("nickname");
        String headimgurl = (String) UtilsJson.toMap(UnionID).get("headimgurl");
        System.out.println("用户名="+nickname);
        User user = this.userService.getObjByProperty("wx_openid", openid);
        if(user == null){
            user = new User();
            user.setUserName(UUID.randomUUID().toString().replace("-","").substring(0,12));
            user.setUserRole("BUYER");
            user.setWx_openid(openid);
            user.setAddTime(new Date());
            user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
            user.setQq_psw("123456");
            Map params = new HashMap();
            params.put("type", "BUYER");
            List roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
            user.getRoles().addAll(roles);
            this.userService.save(user);
            //upLoad(request,headimgurl,user);
            Album album = new Album();
            album.setAddTime(new Date());
            album.setAlbum_default(true);
            album.setAlbum_name("默认相册");
            album.setAlbum_sequence(-10000);
            album.setUser(user);
            this.albumService.save(album);
            request.getSession(false).removeAttribute("verify_code");
            //request.getSession(false).setAttribute("bind", "qq");
            return "redirect:/shopping_login.htm?username=" +
                    CommUtil.encode(user.getUsername()) + "&password=123456";
        }
        request.getSession(false).removeAttribute("verify_code");
        return "redirect:/shopping_login.htm?username=" +CommUtil.encode(user.getUsername()) + "&password=" +CommUtil.encode(user.getQq_psw());
    }


    public static String getHttpContent(String url, String charSet, String method)
    {
        HttpURLConnection connection = null;
        String content = "";
        try {
            URL address_url = new URL(url);
            connection = (HttpURLConnection)address_url.openConnection();
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(1000000);
            connection.setReadTimeout(1000000);

            int response_code = connection.getResponseCode();
            if (response_code == 200) {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, charSet));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    content = content + line;
                }
                String str1 = content;
                return str1;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        if (connection != null) {
            connection.disconnect();
        }

        return "";
    }


    public void upLoad(HttpServletRequest request,String path,User user) throws IOException {
        String filePath = request.getSession().getServletContext().getRealPath("") + "/upload/avatar";
        File uploadDir = new File(filePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String name =  UUID.randomUUID().toString().replace("-","").substring(0,12)+".jpg";
        File f = new File(filePath + File.separator + name);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        out.write(path.getBytes());
        out.flush();
        out.close();
        Accessory photo = new Accessory();
        if (user.getPhoto() != null) {
            photo = user.getPhoto();
        } else {
            photo.setAddTime(new Date());
            photo.setWidth(132);
            photo.setHeight(132);
        }
        photo.setName(name);
        photo.setExt(".jpg");
        photo.setPath(this.configService.getSysConfig().getUploadFilePath() + "/avatar");
        if (user.getPhoto() == null)
            this.accessoryService.save(photo);
        else {
            this.accessoryService.update(photo);
        }
        user.setPhoto(photo);
        this.userService.update(user);
    }

}
