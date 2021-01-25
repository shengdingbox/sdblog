package com.zhouzifei.tool.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhouzifei.tool.consts.VideoTypeConst;
import com.zhouzifei.tool.dto.VideoUrl;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

/**
 * @author 周子斐
 * @date 2021/1/22
 * @Description
 */
@Slf4j
public class KuaishouUtils {

        /**
         * 方法描述: 解析下载视频
         *
         * @param url
         * @author tarzan
         * @date 2020年08月04日 10:33:40
         */
        public static VideoUrl ksParseUrl(String url){
            HashMap<String, String> headers = Maps.newHashMap();
            headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Mobile Safari/537.36");
            String redirectUrl = HttpUtil.createGet(url).addHeaders(headers).execute().header("Location");
            String body= HttpUtil.createGet(redirectUrl).addHeaders(headers).execute().body();
            Document doc= Jsoup.parse(body);
            Elements videoElement = doc.select("script[type=text/javascript]");
            String videoInfo = videoElement.get(3).data().replaceAll("window.pageData= ","");
            JSONObject json = JSONObject.parseObject(videoInfo);
            String title = json.getJSONObject("video").getString("caption");
            String videoUrl=json.getJSONObject("video").getString("srcNoMark");
            videoUrl=videoUrl.substring(0,videoUrl.indexOf("?"));
            log.debug(videoUrl);
            log.debug(title);
            VideoUrl videoUrl1 = new VideoUrl();
            videoUrl1.setType(VideoTypeConst.MP4.getType());
            videoUrl1.setCode("200");
            videoUrl1.setUrl(videoUrl);
            videoUrl1.setSuccess("1");
            videoUrl1.setPlayer("ckplayer");
            videoUrl1.setOriginalUrl(url);
            return videoUrl1;
        }
}
