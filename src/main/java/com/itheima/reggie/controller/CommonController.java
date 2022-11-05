package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传和下载
 * @author prynn
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.upload.path}")
    private String uploadPath;


    /**
     * 文件上传
     * @param file 上传的文件
     * @return 上传成功后的文件名
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要将其保存到指定的目录下，否则本次请求完成后临时文件会被删除
        log.info(file.toString());

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();

        //生成文件的后缀
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        //通过UUID生成随机名,防止后期名称重复覆盖前面的图片.并加上文件后缀
        String fileName = UUID.randomUUID() + suffix;

        //创建一个目录对象
        File dir = new File(uploadPath);
        if(!dir.exists()){
            //如果指定目录不存在，则创建目录
            if(!dir.mkdirs()){
                R.error("文件目录创建失败");
            }
        }


        try {
            //将临时文件转存到指定目录
            file.transferTo(new File(uploadPath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，读取文件的内容
            FileInputStream fileInputStream = new FileInputStream(uploadPath + name);

            //输出流，将文件的内容写到响应中，就可以在浏览器中展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应头，告诉浏览器，这是一个图片
            response.setHeader("Content-Type","image/jpeg");

            int len;
            byte[] buffer = new byte[1024];
            while((len = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
                //刷新缓冲区
                outputStream.flush();
            }

            //关闭流
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
